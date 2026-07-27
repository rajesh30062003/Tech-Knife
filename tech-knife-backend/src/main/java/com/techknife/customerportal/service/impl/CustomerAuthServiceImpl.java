package com.techknife.customerportal.service.impl;

import com.techknife.customerportal.dto.*;
import com.techknife.customerportal.entity.CustomerAccount;
import com.techknife.customerportal.entity.CustomerProfile;
import com.techknife.customerportal.repository.CustomerAccountRepository;
import com.techknife.customerportal.repository.CustomerProfileRepository;
import com.techknife.customerportal.service.CustomerAuthService;
import com.techknife.mail.EmailService;
import com.techknife.security.UserPrincipal;
import com.techknife.security.jwt.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.HashSet;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class CustomerAuthServiceImpl implements CustomerAuthService {

    private final CustomerAccountRepository customerAccountRepository;
    private final CustomerProfileRepository customerProfileRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;
    private final EmailService emailService;

    @Override
    public CustomerLoginResponse login(CustomerLoginRequest request) {
        CustomerAccount account = customerAccountRepository.findByEmail(request.getEmail().toLowerCase())
                .orElseThrow(() -> new IllegalArgumentException("Invalid email or password"));

        if (!passwordEncoder.matches(request.getPassword(), account.getPassword())) {
            throw new IllegalArgumentException("Invalid email or password");
        }

        if ("SUSPENDED".equalsIgnoreCase(account.getStatus())) {
            throw new IllegalStateException("Your customer account has been suspended. Please contact support.");
        }

        account.setLastLoginAt(Instant.now());
        customerAccountRepository.save(account);

        UserPrincipal principal = UserPrincipal.create(
                account.getId(),
                account.getEmail(),
                account.getRoles(),
                new HashSet<>(account.getPermissions())
        );

        String accessToken = jwtTokenProvider.generateAccessToken(principal);
        String refreshToken = jwtTokenProvider.generateRefreshToken(principal);

        return CustomerLoginResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .tokenType("Bearer")
                .customerAccountId(account.getId())
                .customerCode(account.getCustomerCode())
                .email(account.getEmail())
                .companyName(account.getCompanyName())
                .contactPersonName(account.getContactPersonName())
                .roles(account.getRoles())
                .permissions(account.getPermissions())
                .build();
    }

    @Override
    public CustomerLoginResponse register(CustomerRegisterRequest request) {
        if (customerAccountRepository.existsByEmail(request.getEmail().toLowerCase())) {
            throw new IllegalArgumentException("Email already registered: " + request.getEmail());
        }

        String customerCode = "CUST-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        String verificationToken = UUID.randomUUID().toString();

        CustomerAccount account = CustomerAccount.builder()
                .customerCode(customerCode)
                .email(request.getEmail().toLowerCase())
                .password(passwordEncoder.encode(request.getPassword()))
                .companyName(request.getCompanyName())
                .contactPersonName(request.getContactPersonName())
                .phone(request.getPhone())
                .status("ACTIVE")
                .emailVerified(false)
                .verificationToken(verificationToken)
                .lastLoginAt(Instant.now())
                .build();

        account = customerAccountRepository.save(account);

        CustomerProfile profile = CustomerProfile.builder()
                .customerAccountId(account.getId())
                .companyName(account.getCompanyName())
                .contactName(account.getContactPersonName())
                .contactEmail(account.getEmail())
                .phone(account.getPhone())
                .build();

        customerProfileRepository.save(profile);

        try {
            emailService.sendSimpleEmail(
                    account.getEmail(),
                    "Welcome to TechKnife Customer Portal - Verify Your Email",
                    "Hello " + request.getContactPersonName() + ",\n\n" +
                            "Welcome to TechKnife Enterprise Customer Portal. Your account has been created.\n" +
                            "Verification Token: " + verificationToken + "\n\n" +
                            "Thank you,\nTechKnife Team"
            );
        } catch (Exception e) {
            log.warn("Failed to send welcome email to {}: {}", account.getEmail(), e.getMessage());
        }

        UserPrincipal principal = UserPrincipal.create(
                account.getId(),
                account.getEmail(),
                account.getRoles(),
                new HashSet<>(account.getPermissions())
        );

        String accessToken = jwtTokenProvider.generateAccessToken(principal);
        String refreshToken = jwtTokenProvider.generateRefreshToken(principal);

        return CustomerLoginResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .tokenType("Bearer")
                .customerAccountId(account.getId())
                .customerCode(account.getCustomerCode())
                .email(account.getEmail())
                .companyName(account.getCompanyName())
                .contactPersonName(account.getContactPersonName())
                .roles(account.getRoles())
                .permissions(account.getPermissions())
                .build();
    }

    @Override
    public void forgotPassword(CustomerForgotPasswordRequest request) {
        CustomerAccount account = customerAccountRepository.findByEmail(request.getEmail().toLowerCase())
                .orElseThrow(() -> new IllegalArgumentException("No account found with email: " + request.getEmail()));

        String resetToken = UUID.randomUUID().toString();
        account.setPasswordResetToken(resetToken);
        account.setPasswordResetExpiry(Instant.now().plus(24, ChronoUnit.HOURS));
        customerAccountRepository.save(account);

        try {
            emailService.sendSimpleEmail(
                    account.getEmail(),
                    "TechKnife Customer Portal - Password Reset Request",
                    "Hello " + account.getContactPersonName() + ",\n\n" +
                            "We received a request to reset your password.\n" +
                            "Reset Token: " + resetToken + "\n\n" +
                            "If you did not request this, please ignore this email.\n\n" +
                            "Thank you,\nTechKnife Team"
            );
        } catch (Exception e) {
            log.warn("Failed to send password reset email to {}: {}", account.getEmail(), e.getMessage());
        }
    }

    @Override
    public void resetPassword(CustomerResetPasswordRequest request) {
        CustomerAccount account = customerAccountRepository.findByPasswordResetToken(request.getToken())
                .orElseThrow(() -> new IllegalArgumentException("Invalid or expired password reset token"));

        if (account.getPasswordResetExpiry() == null || account.getPasswordResetExpiry().isBefore(Instant.now())) {
            throw new IllegalArgumentException("Password reset token has expired");
        }

        account.setPassword(passwordEncoder.encode(request.getNewPassword()));
        account.setPasswordResetToken(null);
        account.setPasswordResetExpiry(null);
        customerAccountRepository.save(account);
    }

    @Override
    public void verifyEmail(String token) {
        CustomerAccount account = customerAccountRepository.findByVerificationToken(token)
                .orElseThrow(() -> new IllegalArgumentException("Invalid email verification token"));

        account.setEmailVerified(true);
        account.setVerificationToken(null);
        customerAccountRepository.save(account);
    }

    @Override
    public void changePassword(String customerAccountId, CustomerChangePasswordRequest request) {
        CustomerAccount account = customerAccountRepository.findById(customerAccountId)
                .orElseThrow(() -> new IllegalArgumentException("Customer account not found"));

        if (!passwordEncoder.matches(request.getCurrentPassword(), account.getPassword())) {
            throw new IllegalArgumentException("Current password does not match");
        }

        account.setPassword(passwordEncoder.encode(request.getNewPassword()));
        customerAccountRepository.save(account);
    }
}
