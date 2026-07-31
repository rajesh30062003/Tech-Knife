package com.techknife.backend.serviceImpl;

import com.techknife.backend.audit.Auditable;
import com.techknife.backend.constant.Permission;
import com.techknife.backend.constant.Role;
import com.techknife.backend.dto.*;
import com.techknife.backend.entity.RefreshToken;
import com.techknife.backend.entity.User;
import com.techknife.backend.entity.VerificationOtp;
import com.techknife.backend.exception.BadRequestException;
import com.techknife.backend.exception.ResourceNotFoundException;
import com.techknife.backend.exception.UnauthorizedException;
import com.techknife.backend.mail.EmailService;
import com.techknife.backend.mapper.UserMapper;
import com.techknife.backend.repository.RefreshTokenRepository;
import com.techknife.backend.repository.UserRepository;
import com.techknife.backend.repository.VerificationOtpRepository;
import com.techknife.security.jwt.JwtTokenProvider;
import com.techknife.security.UserPrincipal;
import com.techknife.backend.service.AuthService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.time.Instant;
import java.util.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider tokenProvider;
    @org.springframework.beans.factory.annotation.Qualifier("backendUserRepository")
    private final UserRepository userRepository;
    @org.springframework.beans.factory.annotation.Qualifier("backendRefreshTokenRepository")
    private final RefreshTokenRepository refreshTokenRepository;
    @org.springframework.beans.factory.annotation.Qualifier("backendVerificationOtpRepository")
    private final VerificationOtpRepository verificationOtpRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserMapper userMapper;
    private final EmailService emailService;
    private final org.springframework.data.mongodb.core.MongoTemplate mongoTemplate;

    @Value("${app.jwt.expiration-ms:86400000}")
    private long jwtExpirationMs;

    @Value("${app.jwt.refresh-expiration-ms:604800000}")
    private long refreshExpirationMs;

    private static final SecureRandom RANDOM = new SecureRandom();

    @Override
    @Auditable(module = "AUTH", action = "LOGIN", logParameters = false)
    public AuthResponse authenticateUser(AuthRequest request) {
        String inputEmail = request.getEmail() != null ? request.getEmail().trim().toLowerCase() : "";

        // Enforce Official Email Login Rule: Reject mobile numbers or non-email credentials
        if (inputEmail.matches("^\\+?[0-9]{7,15}$") || !inputEmail.contains("@")) {
            throw new BadRequestException("Login using mobile number or invalid ID is strictly prohibited. You must log in using your official email address.");
        }

        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(inputEmail, request.getPassword())
        );

        SecurityContextHolder.getContext().setAuthentication(authentication);
        String accessToken = tokenProvider.generateToken(authentication);

        UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();
        User user = findUserByIdOrUserId(userPrincipal.getId());

        // Perform targeted atomic MongoDB update for lastLoginAt (NEVER execute save/insert during login)
        if (user.getId() != null) {
            mongoTemplate.updateFirst(
                    org.springframework.data.mongodb.core.query.Query.query(
                            org.springframework.data.mongodb.core.query.Criteria.where("_id").is(user.getId())
                    ),
                    org.springframework.data.mongodb.core.query.Update.update("lastLoginAt", Instant.now()),
                    User.class
            );
        } else if (user.getEmail() != null) {
            mongoTemplate.updateFirst(
                    org.springframework.data.mongodb.core.query.Query.query(
                            org.springframework.data.mongodb.core.query.Criteria.where("email").is(user.getEmail())
                    ),
                    org.springframework.data.mongodb.core.query.Update.update("lastLoginAt", Instant.now()),
                    User.class
            );
        }

        user.setLastLoginAt(Instant.now());

        // Generate and persist refresh token with rotation
        String refreshTokenString = createRefreshToken(user.getId() != null ? user.getId() : user.getEmail());

        return userMapper.toAuthResponse(user, accessToken, refreshTokenString, jwtExpirationMs);
    }

    @Override
    @Auditable(module = "AUTH", action = "REGISTER", logParameters = false)
    public AuthResponse registerUser(RegisterRequest request) {
        String email = request.getEmail().trim().toLowerCase();

        if (email.matches("^\\+?[0-9]{7,15}$") || !email.contains("@")) {
            throw new BadRequestException("Registration requires a valid official email address. Mobile numbers cannot be used as primary login identities.");
        }

        if (userRepository.existsByEmail(email)) {
            throw new BadRequestException("Official email address is already registered: " + email);
        }

        Set<Role> roles = request.getRoles();
        if (roles == null || roles.isEmpty()) {
            roles = Set.of(Role.ROLE_EMPLOYEE);
        }

        User user = User.builder()
                .email(email)
                .password(passwordEncoder.encode(request.getPassword()))
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .designation(request.getDesignation() != null ? request.getDesignation() : "Staff Member")
                .department(request.getDepartment() != null ? request.getDepartment() : "General")
                .phoneNumber(request.getPhoneNumber())
                .avatarUrl("https://images.unsplash.com/photo-1534528741775-53994a69daeb?auto=format&fit=crop&q=80&w=250")
                .enabled(true)
                .accountNonLocked(true)
                .emailVerified(false)
                .roles(roles)
                .build();

        User savedUser = userRepository.save(user);

        // Send welcome email asynchronously
        String welcomeBody = String.format(
                "<h3>Welcome to Tech Knife Enterprise Management System</h3>" +
                "<p>Hello %s,</p>" +
                "<p>Your account (<b>%s</b>) has been successfully activated. Please verify your email using the system portal.</p>" +
                "<p>Best regards,<br/>Tech Knife Security & Governance Team</p>",
                savedUser.getFirstName(), savedUser.getEmail()
        );
        emailService.sendEmail(savedUser.getEmail(), "Account Provisioned - Tech Knife Enterprise", welcomeBody);

        UserPrincipal principal = UserPrincipal.create(savedUser);
        Authentication authentication = new UsernamePasswordAuthenticationToken(principal, null, principal.getAuthorities());
        String accessToken = tokenProvider.generateToken(authentication);
        String refreshTokenString = createRefreshToken(savedUser.getId());

        return userMapper.toAuthResponse(savedUser, accessToken, refreshTokenString, jwtExpirationMs);
    }

    @Override
    @Auditable(module = "AUTH", action = "REFRESH_TOKEN", logParameters = false)
    public AuthResponse refreshToken(String refreshTokenStr) {
        RefreshToken refreshToken = refreshTokenRepository.findByToken(refreshTokenStr)
                .orElseThrow(() -> new UnauthorizedException("Invalid or revoked refresh token"));

        if (refreshToken.isRevoked() || refreshToken.getExpiryDate().isBefore(Instant.now())) {
            refreshTokenRepository.delete(refreshToken);
            throw new UnauthorizedException("Refresh token has expired or been revoked. Please log in again.");
        }

        User user = findUserByIdOrUserId(refreshToken.getUserId());

        // Refresh Token Rotation: Revoke previous refresh token
        refreshTokenRepository.delete(refreshToken);

        // Issue new rotated refresh token
        String newRefreshTokenString = createRefreshToken(user.getId());

        UserPrincipal principal = UserPrincipal.create(user);
        Authentication authentication = new UsernamePasswordAuthenticationToken(principal, null, principal.getAuthorities());
        String newAccessToken = tokenProvider.generateToken(authentication);

        return userMapper.toAuthResponse(user, newAccessToken, newRefreshTokenString, jwtExpirationMs);
    }

    @Override
    @Auditable(module = "AUTH", action = "LOGOUT", logParameters = false)
    public void logout(String refreshTokenStr) {
        if (refreshTokenStr != null && !refreshTokenStr.isEmpty()) {
            refreshTokenRepository.findByToken(refreshTokenStr).ifPresent(token -> {
                token.setRevoked(true);
                refreshTokenRepository.save(token);
            });
        }
    }

    @Override
    @Auditable(module = "AUTH", action = "FORGOT_PASSWORD", logParameters = false)
    public void forgotPassword(ForgotPasswordRequest request) {
        String email = request.getEmail().trim().toLowerCase();
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User", "email", email));

        String otpCode = String.format("%06d", RANDOM.nextInt(1000000));
        
        verificationOtpRepository.deleteByEmailAndType(email, VerificationOtp.OtpType.PASSWORD_RESET);

        VerificationOtp otpEntity = VerificationOtp.builder()
                .email(email)
                .otpCode(otpCode)
                .type(VerificationOtp.OtpType.PASSWORD_RESET)
                .expiryDate(Instant.now().plusSeconds(900)) // 15 mins validity
                .used(false)
                .build();

        verificationOtpRepository.save(otpEntity);

        String emailText = String.format(
                "<h3>Tech Knife Password Reset OTP</h3>" +
                "<p>Hello %s,</p>" +
                "<p>Your single-use password reset verification code is: <b>%s</b></p>" +
                "<p>This code will expire in 15 minutes. If you did not request a password reset, please contact IT Security immediately.</p>",
                user.getFirstName(), otpCode
        );

        emailService.sendEmail(email, "Password Reset Request - Tech Knife", emailText);
    }

    @Override
    @Auditable(module = "AUTH", action = "RESET_PASSWORD", logParameters = false)
    public void resetPassword(ResetPasswordRequest request) {
        String email = request.getEmail().trim().toLowerCase();
        
        VerificationOtp otpEntity = verificationOtpRepository
                .findByEmailAndOtpCodeAndTypeAndUsedFalse(email, request.getOtpCode(), VerificationOtp.OtpType.PASSWORD_RESET)
                .orElseThrow(() -> new BadRequestException("Invalid or expired password reset verification OTP"));

        if (otpEntity.getExpiryDate().isBefore(Instant.now())) {
            throw new BadRequestException("Password reset verification OTP has expired");
        }

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User", "email", email));

        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        userRepository.save(user);

        otpEntity.setUsed(true);
        verificationOtpRepository.save(otpEntity);
    }

    @Override
    @Auditable(module = "AUTH", action = "SEND_EMAIL_OTP", logParameters = false)
    public void sendEmailVerificationOtp(SendOtpRequest request) {
        String email = request.getEmail().trim().toLowerCase();
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User", "email", email));

        String otpCode = String.format("%06d", RANDOM.nextInt(1000000));

        verificationOtpRepository.deleteByEmailAndType(email, request.getType());

        VerificationOtp otpEntity = VerificationOtp.builder()
                .email(email)
                .otpCode(otpCode)
                .type(request.getType())
                .expiryDate(Instant.now().plusSeconds(900))
                .used(false)
                .build();

        verificationOtpRepository.save(otpEntity);

        String emailText = String.format(
                "<h3>Tech Knife Email Verification OTP</h3>" +
                "<p>Hello %s,</p>" +
                "<p>Your email verification OTP is: <b>%s</b></p>" +
                "<p>Valid for 15 minutes.</p>",
                user.getFirstName(), otpCode
        );

        emailService.sendEmail(email, "Verify Your Email - Tech Knife", emailText);
    }

    @Override
    @Auditable(module = "AUTH", action = "VERIFY_EMAIL_OTP", logParameters = false)
    public boolean verifyEmailOtp(VerifyOtpRequest request) {
        String email = request.getEmail().trim().toLowerCase();

        VerificationOtp otpEntity = verificationOtpRepository
                .findByEmailAndOtpCodeAndTypeAndUsedFalse(email, request.getOtpCode(), request.getType())
                .orElseThrow(() -> new BadRequestException("Invalid verification OTP provided"));

        if (otpEntity.getExpiryDate().isBefore(Instant.now())) {
            throw new BadRequestException("Verification OTP has expired");
        }

        otpEntity.setUsed(true);
        verificationOtpRepository.save(otpEntity);

        if (request.getType() == VerificationOtp.OtpType.EMAIL_VERIFICATION) {
            userRepository.findByEmail(email).ifPresent(user -> {
                user.setEmailVerified(true);
                userRepository.save(user);
            });
        }

        return true;
    }

    @Override
    @Auditable(module = "AUTH", action = "CHANGE_PASSWORD", logParameters = false)
    public void changePassword(String userId, ChangePasswordRequest request) {
        User user = findUserByIdOrUserId(userId);

        if (!passwordEncoder.matches(request.getCurrentPassword(), user.getPassword())) {
            throw new BadRequestException("Current password provided is incorrect");
        }

        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        userRepository.save(user);
    }

    @Override
    public UserResponse getCurrentUser(String userId) {
        User user = findUserByIdOrUserId(userId);

        Set<Permission> permissions = new HashSet<>();
        if (user.getRoles() != null) {
            for (Role role : user.getRoles()) {
                permissions.addAll(getPermissionsForRole(role));
            }
        }

        return UserResponse.builder()
                .id(user.getId())
                .email(user.getEmail())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .designation(user.getDesignation())
                .department(user.getDepartment())
                .phoneNumber(user.getPhoneNumber())
                .avatarUrl(user.getAvatarUrl())
                .enabled(user.isEnabled())
                .accountNonLocked(user.isAccountNonLocked())
                .emailVerified(user.isEmailVerified())
                .roles(user.getRoles())
                .permissions(permissions)
                .lastLoginAt(user.getLastLoginAt())
                .createdAt(user.getCreatedAt())
                .updatedAt(user.getUpdatedAt())
                .build();
    }

    @Override
    @Auditable(module = "AUTH", action = "UPDATE_PROFILE_PICTURE", logParameters = false)
    public UserResponse updateProfilePicture(String userId, ProfilePictureRequest request) {
        User user = findUserByIdOrUserId(userId);

        user.setAvatarUrl(request.getAvatarUrl());
        User updated = userRepository.save(user);
        return getCurrentUser(updated.getId());
    }

    private String createRefreshToken(String userId) {
        refreshTokenRepository.deleteByUserId(userId);

        RefreshToken refreshToken = RefreshToken.builder()
                .userId(userId)
                .token(UUID.randomUUID().toString() + "-" + System.currentTimeMillis())
                .expiryDate(Instant.now().plusMillis(refreshExpirationMs))
                .revoked(false)
                .build();

        RefreshToken saved = refreshTokenRepository.save(refreshToken);
        return saved.getToken();
    }

    private User findUserByIdOrUserId(String identifier) {
        if (identifier == null || identifier.trim().isEmpty()) {
            throw new ResourceNotFoundException("User", "id", "null");
        }
        String cleanId = identifier.trim();
        return userRepository.findById(cleanId)
                .or(() -> userRepository.findByUserId(cleanId))
                .or(() -> userRepository.findByEmail(cleanId.toLowerCase()))
                .orElseThrow(() -> new ResourceNotFoundException("User", "id/userId/email", cleanId));
    }

    private Set<Permission> getPermissionsForRole(Role role) {
        Set<Permission> permissions = new HashSet<>();
        if (role == null) return permissions;

        switch (role) {
            case ROLE_SUPER_ADMIN, ROLE_ADMIN, ROLE_CEO, ROLE_CTO, ROLE_CMO, ROLE_MD, ROLE_DIRECTOR:
                permissions.addAll(Arrays.asList(Permission.values()));
                break;
            case ROLE_MANAGER, ROLE_PROJECT_LEAD:
                permissions.addAll(Arrays.asList(
                        Permission.USER_READ, Permission.PROJECT_READ, Permission.PROJECT_WRITE,
                        Permission.CRM_READ, Permission.CRM_WRITE, Permission.RECRUITMENT_READ
                ));
                break;
            case ROLE_EMPLOYEE, ROLE_INTERN:
                permissions.addAll(Arrays.asList(
                        Permission.USER_READ, Permission.PROJECT_READ, Permission.PAYROLL_READ
                ));
                break;
            case ROLE_CUSTOMER:
                permissions.add(Permission.PROJECT_READ);
                break;
            default:
                permissions.add(Permission.USER_READ);
        }
        return permissions;
    }
}
