package com.techknife.iam.service.impl;

import com.techknife.audit.dto.AuditLogRequest;
import com.techknife.audit.entity.AuditAction;
import com.techknife.audit.entity.AuditModule;
import com.techknife.audit.service.AuditService;
import com.techknife.backend.exception.ResourceNotFoundException;
import com.techknife.backend.exception.UnauthorizedException;
import com.techknife.iam.dto.request.LoginRequest;
import com.techknife.iam.dto.request.LogoutRequest;
import com.techknife.iam.dto.request.RefreshTokenRequest;
import com.techknife.iam.dto.response.CurrentUserResponse;
import com.techknife.iam.dto.response.LoginResponse;
import com.techknife.iam.dto.response.RefreshTokenResponse;
import com.techknife.iam.dto.response.UserProfileResponse;
import com.techknife.iam.entity.LoginHistory;
import com.techknife.iam.entity.RefreshToken;
import com.techknife.iam.entity.User;
import com.techknife.iam.enums.AccountStatus;
import com.techknife.iam.repository.LoginHistoryRepository;
import com.techknife.iam.repository.RefreshTokenRepository;
import com.techknife.iam.repository.UserRepository;
import com.techknife.iam.service.AuthenticationService;
import com.techknife.security.UserPrincipal;
import com.techknife.security.jwt.JwtProperties;
import com.techknife.security.jwt.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.Instant;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;

/**
 * Implementation of AuthenticationService providing secure credential validation, JWT issuance, brute force protection, and session revocation.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AuthenticationServiceImpl implements AuthenticationService {

    private static final int MAX_FAILED_ATTEMPTS = 5;
    private static final long LOCK_DURATION_SECONDS = 900; // 15 minutes

    private final UserRepository userRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final LoginHistoryRepository loginHistoryRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;
    private final JwtProperties jwtProperties;
    private final AuditService auditService;

    @Override
    @Transactional
    public LoginResponse login(LoginRequest request) {
        return login(request, "0.0.0.0", "UNKNOWN");
    }

    @Override
    @Transactional
    public LoginResponse login(LoginRequest request, String ipAddress, String userAgent) {
        log.info("Processing login request for official email '{}'", request.getOfficialEmail());

        User user = userRepository.findByOfficialEmail(request.getOfficialEmail())
                .orElseThrow(() -> {
                    log.warn("Login failed: User with email '{}' not found", request.getOfficialEmail());
                    return new UnauthorizedException("Invalid email or password");
                });

        Instant now = Instant.now();

        // Account Lock Check
        if (user.isAccountLocked()) {
            if (user.getLockExpiration() != null && now.isAfter(user.getLockExpiration())) {
                log.info("Lock duration expired for user '{}'. Unlocking account.", user.getUserId());
                user.setAccountLocked(false);
                user.setFailedLoginAttempts(0);
                user.setLockExpiration(null);
                if (user.getAccountStatus() == AccountStatus.LOCKED) {
                    user.setAccountStatus(AccountStatus.ACTIVE);
                }
                userRepository.save(user);
            } else {
                log.warn("Login attempt blocked for locked account user '{}'", user.getUserId());
                recordLoginHistory(user.getUserId(), ipAddress, userAgent, request.getDeviceInfo(), "FAILED", "ACCOUNT_LOCKED");
                throw new UnauthorizedException("Account is locked due to multiple failed login attempts. Please try again later.");
            }
        }

        // Credentials Verification
        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            log.warn("Password mismatch for user '{}'", user.getUserId());
            handleFailedLogin(user, ipAddress, userAgent, request.getDeviceInfo());
            throw new UnauthorizedException("Invalid email or password");
        }

        // Account Status Check
        if (user.getAccountStatus() == AccountStatus.INACTIVE || user.getAccountStatus() == AccountStatus.SUSPENDED) {
            log.warn("Login blocked for user '{}' with account status '{}'", user.getUserId(), user.getAccountStatus());
            recordLoginHistory(user.getUserId(), ipAddress, userAgent, request.getDeviceInfo(), "FAILED", "ACCOUNT_" + user.getAccountStatus());
            throw new UnauthorizedException("Account is disabled or suspended. Please contact administrator.");
        }

        // Successful Authentication Reset
        user.setFailedLoginAttempts(0);
        user.setAccountLocked(false);
        user.setLockExpiration(null);
        user.setLastLogin(now);
        user.setUpdatedAt(now);
        userRepository.save(user);

        // Record Audit and Login History
        recordLoginHistory(user.getUserId(), ipAddress, userAgent, request.getDeviceInfo(), "SUCCESS", null);

        UserPrincipal principal = UserPrincipal.create(
                user.getUserId(),
                user.getOfficialEmail(),
                user.getPassword(),
                user.getAccountStatus() == AccountStatus.ACTIVE,
                !user.isAccountLocked(),
                user.getRoles() != null ? new ArrayList<>(user.getRoles()) : new ArrayList<>(),
                user.getPermissions() != null ? user.getPermissions() : new HashSet<>()
        );

        String accessToken = jwtTokenProvider.generateAccessToken(principal);
        String refreshToken = jwtTokenProvider.generateRefreshToken(principal);

        // Persist Refresh Token
        RefreshToken tokenEntity = RefreshToken.builder()
                .token(refreshToken)
                .userId(user.getUserId())
                .expiry(now.plusMillis(jwtProperties.getRefreshExpirationMs()))
                .revoked(false)
                .deviceInfo(StringUtils.hasText(request.getDeviceInfo()) ? request.getDeviceInfo() : userAgent)
                .ipAddress(ipAddress)
                .createdAt(now)
                .build();
        refreshTokenRepository.save(tokenEntity);

        // Audit Event
        auditService.logAsync(AuditLogRequest.builder()
                .userId(user.getUserId())
                .userName(user.getFullName())
                .userEmail(user.getOfficialEmail())
                .action(AuditAction.LOGIN)
                .module(AuditModule.AUTHENTICATION)
                .entityType("USER")
                .entityId(user.getUserId())
                .description("User logged in successfully")
                .ipAddress(ipAddress)
                .userAgent(userAgent)
                .status("SUCCESS")
                .timestamp(now)
                .build());

        UserProfileResponse userProfile = mapToUserProfileResponse(user);

        return LoginResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .tokenType("Bearer")
                .expiresIn(now.plusMillis(jwtProperties.getAccessExpirationMs()))
                .userProfile(userProfile)
                .roles(user.getRoles())
                .permissions(user.getPermissions())
                .build();
    }

    @Override
    @Transactional
    public RefreshTokenResponse refreshToken(RefreshTokenRequest request) {
        String tokenStr = request.getRefreshToken();
        log.info("Processing token refresh request");

        if (!jwtTokenProvider.validateRefreshToken(tokenStr)) {
            log.warn("Invalid refresh token JWT signature or expiry");
            throw new UnauthorizedException("Invalid or expired refresh token");
        }

        RefreshToken storedToken = refreshTokenRepository.findByToken(tokenStr)
                .orElseThrow(() -> new UnauthorizedException("Refresh token not found or revoked"));

        if (storedToken.isRevoked() || storedToken.getExpiry().isBefore(Instant.now())) {
            log.warn("Refresh token is revoked or expired in database");
            throw new UnauthorizedException("Refresh token is revoked or expired");
        }

        String userId = storedToken.getUserId();
        User user = userRepository.findByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "userId", userId));

        if (user.getAccountStatus() != AccountStatus.ACTIVE || user.isAccountLocked()) {
            throw new UnauthorizedException("User account is not active");
        }

        Instant now = Instant.now();

        // Rotate Refresh Token
        storedToken.setRevoked(true);
        refreshTokenRepository.save(storedToken);

        UserPrincipal principal = UserPrincipal.create(
                user.getUserId(),
                user.getOfficialEmail(),
                user.getRoles() != null ? new ArrayList<>(user.getRoles()) : new ArrayList<>(),
                user.getPermissions() != null ? user.getPermissions() : new HashSet<>()
        );

        String newAccessToken = jwtTokenProvider.generateAccessToken(principal);
        String newRefreshToken = jwtTokenProvider.generateRefreshToken(principal);

        RefreshToken newTokenEntity = RefreshToken.builder()
                .token(newRefreshToken)
                .userId(user.getUserId())
                .expiry(now.plusMillis(jwtProperties.getRefreshExpirationMs()))
                .revoked(false)
                .deviceInfo(storedToken.getDeviceInfo())
                .ipAddress(storedToken.getIpAddress())
                .createdAt(now)
                .build();
        refreshTokenRepository.save(newTokenEntity);

        return RefreshTokenResponse.builder()
                .accessToken(newAccessToken)
                .refreshToken(newRefreshToken)
                .tokenType("Bearer")
                .expiresIn(now.plusMillis(jwtProperties.getAccessExpirationMs()))
                .build();
    }

    @Override
    @Transactional
    public void logout(LogoutRequest request) {
        log.info("Processing logout request for token");
        Optional<RefreshToken> tokenOpt = refreshTokenRepository.findByToken(request.getRefreshToken());

        if (tokenOpt.isPresent()) {
            RefreshToken token = tokenOpt.get();
            token.setRevoked(true);
            refreshTokenRepository.save(token);

            if (Boolean.TRUE.equals(request.getAllDevices())) {
                logoutAllDevices(token.getUserId());
            }

            auditService.logAsync(AuditLogRequest.builder()
                    .userId(token.getUserId())
                    .action(AuditAction.LOGOUT)
                    .module(AuditModule.AUTHENTICATION)
                    .entityType("REFRESH_TOKEN")
                    .entityId(token.getId())
                    .description("User logged out successfully")
                    .status("SUCCESS")
                    .timestamp(Instant.now())
                    .build());
        }
    }

    @Override
    @Transactional
    public void logoutAllDevices(String userId) {
        log.info("Revoking all active refresh token sessions for userId '{}'", userId);
        List<RefreshToken> userTokens = refreshTokenRepository.findByUserId(userId);
        for (RefreshToken token : userTokens) {
            if (!token.isRevoked()) {
                token.setRevoked(true);
            }
        }
        refreshTokenRepository.saveAll(userTokens);

        auditService.logAsync(AuditLogRequest.builder()
                .userId(userId)
                .action(AuditAction.LOGOUT)
                .module(AuditModule.AUTHENTICATION)
                .entityType("USER")
                .entityId(userId)
                .description("Logged out from all active sessions")
                .status("SUCCESS")
                .timestamp(Instant.now())
                .build());
    }

    @Override
    @Transactional(readOnly = true)
    public CurrentUserResponse getCurrentUser(String userId) {
        User user = userRepository.findByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "userId", userId));

        return CurrentUserResponse.builder()
                .userId(user.getUserId())
                .employeeId(user.getEmployeeId())
                .customerId(user.getCustomerId())
                .officialEmail(user.getOfficialEmail())
                .name(user.getFullName())
                .profileImage(user.getProfilePictureUrl())
                .roles(user.getRoles())
                .permissions(user.getPermissions())
                .accountStatus(user.getAccountStatus())
                .build();
    }

    @Override
    public boolean validateAccessToken(String token) {
        return jwtTokenProvider.validateToken(token);
    }

    @Override
    public boolean validateRefreshToken(String token) {
        if (!jwtTokenProvider.validateRefreshToken(token)) {
            return false;
        }
        return refreshTokenRepository.findByToken(token)
                .map(t -> !t.isRevoked() && t.getExpiry().isAfter(Instant.now()))
                .orElse(false);
    }

    private void handleFailedLogin(User user, String ipAddress, String userAgent, String deviceInfo) {
        int failedAttempts = user.getFailedLoginAttempts() + 1;
        user.setFailedLoginAttempts(failedAttempts);

        if (failedAttempts >= MAX_FAILED_ATTEMPTS) {
            log.warn("Maximum failed login attempts reached for user '{}'. Locking account.", user.getUserId());
            user.setAccountLocked(true);
            user.setAccountStatus(AccountStatus.LOCKED);
            user.setLockExpiration(Instant.now().plusSeconds(LOCK_DURATION_SECONDS));
        }

        user.setUpdatedAt(Instant.now());
        userRepository.save(user);

        recordLoginHistory(user.getUserId(), ipAddress, userAgent, deviceInfo, "FAILED", "INVALID_CREDENTIALS");

        auditService.logAsync(AuditLogRequest.builder()
                .userId(user.getUserId())
                .userEmail(user.getOfficialEmail())
                .action(AuditAction.LOGIN)
                .module(AuditModule.AUTHENTICATION)
                .entityType("USER")
                .entityId(user.getUserId())
                .description("Failed login attempt (attempt " + failedAttempts + ")")
                .ipAddress(ipAddress)
                .userAgent(userAgent)
                .status("FAILED")
                .timestamp(Instant.now())
                .build());
    }

    private void recordLoginHistory(String userId, String ipAddress, String userAgent, String deviceInfo, String status, String failureReason) {
        LoginHistory history = LoginHistory.builder()
                .userId(userId)
                .loginTime(Instant.now())
                .ipAddress(ipAddress)
                .userAgent(userAgent)
                .deviceInfo(deviceInfo)
                .status(status)
                .failureReason(failureReason)
                .build();
        loginHistoryRepository.save(history);
    }

    private UserProfileResponse mapToUserProfileResponse(User user) {
        return UserProfileResponse.builder()
                .userId(user.getUserId())
                .officialEmail(user.getOfficialEmail())
                .personalEmail(user.getPersonalEmail())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .fullName(user.getFullName())
                .mobile(user.getMobile())
                .profilePictureUrl(user.getProfilePictureUrl())
                .department(user.getDepartment())
                .designation(user.getDesignation())
                .employeeId(user.getEmployeeId())
                .customerId(user.getCustomerId())
                .accountStatus(user.getAccountStatus())
                .accountType(user.getAccountType())
                .emailVerified(user.isEmailVerified())
                .mobileVerified(user.isMobileVerified())
                .roles(user.getRoles())
                .permissions(user.getPermissions())
                .createdAt(user.getCreatedAt())
                .updatedAt(user.getUpdatedAt())
                .lastLogin(user.getLastLogin())
                .build();
    }
}
