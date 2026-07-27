package com.techknife.iam.service.impl;

import com.techknife.audit.dto.AuditLogRequest;
import com.techknife.audit.entity.AuditAction;
import com.techknife.audit.entity.AuditModule;
import com.techknife.audit.service.AuditService;
import com.techknife.backend.exception.BadRequestException;
import com.techknife.backend.exception.ResourceNotFoundException;
import com.techknife.iam.dto.request.ChangePasswordRequest;
import com.techknife.iam.dto.request.ForgotPasswordRequest;
import com.techknife.iam.dto.request.ResetPasswordRequest;
import com.techknife.iam.entity.PasswordResetToken;
import com.techknife.iam.entity.User;
import com.techknife.iam.repository.PasswordResetTokenRepository;
import com.techknife.iam.repository.UserRepository;
import com.techknife.iam.service.PasswordService;
import com.techknife.mail.EmailService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Optional;
import java.util.UUID;
import java.util.regex.Pattern;

/**
 * Implementation of PasswordService managing password reset token lifecycles, hash updates, and password policy validations.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class PasswordServiceImpl implements PasswordService {

    private static final Pattern PASSWORD_STRENGTH_PATTERN =
            Pattern.compile("^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=!_*\\-])(?=\\S+$).{8,64}$");

    private final UserRepository userRepository;
    private final PasswordResetTokenRepository passwordResetTokenRepository;
    private final PasswordEncoder passwordEncoder;
    private final EmailService emailService;
    private final AuditService auditService;

    @Override
    @Transactional
    public void forgotPassword(ForgotPasswordRequest request) {
        log.info("Processing forgot password request for email '{}'", request.getEmail());
        Optional<User> userOpt = userRepository.findByOfficialEmail(request.getEmail());

        if (userOpt.isEmpty()) {
            log.info("Forgot password email '{}' not found, suppressing response for security", request.getEmail());
            return;
        }

        User user = userOpt.get();
        String token = UUID.randomUUID().toString();
        Instant now = Instant.now();

        PasswordResetToken resetToken = PasswordResetToken.builder()
                .token(token)
                .userId(user.getUserId())
                .expiry(now.plus(30, ChronoUnit.MINUTES))
                .used(false)
                .createdAt(now)
                .build();

        passwordResetTokenRepository.save(resetToken);

        try {
            emailService.sendSimpleEmail(user.getOfficialEmail(),
                    "Tech Knife Platform - Password Reset Instructions",
                    "Hello " + user.getFirstName() + ",\n\n"
                            + "You requested a password reset. Your reset token is:\n"
                            + token + "\n\n"
                            + "This token will expire in 30 minutes.\n\n"
                            + "Regards,\nTech Knife IAM Team");
        } catch (Exception ex) {
            log.error("Failed to deliver password reset email to '{}': {}", user.getOfficialEmail(), ex.getMessage());
        }

        auditService.logAsync(AuditLogRequest.builder()
                .userId(user.getUserId())
                .userEmail(user.getOfficialEmail())
                .action(AuditAction.RESET_PASSWORD)
                .module(AuditModule.AUTHENTICATION)
                .entityType("PASSWORD_RESET_TOKEN")
                .entityId(resetToken.getId())
                .description("Generated password reset recovery token")
                .status("SUCCESS")
                .timestamp(now)
                .build());
    }

    @Override
    @Transactional
    public void resetPassword(ResetPasswordRequest request) {
        log.info("Processing password reset with token");
        if (!validatePasswordStrength(request.getNewPassword())) {
            throw new BadRequestException("New password does not satisfy security policy requirements (must contain upper, lower, digit, special char, 8-64 chars)");
        }

        PasswordResetToken resetToken = passwordResetTokenRepository.findByToken(request.getResetToken())
                .orElseThrow(() -> new BadRequestException("Invalid or expired password reset token"));

        if (resetToken.isUsed() || resetToken.getExpiry().isBefore(Instant.now())) {
            throw new BadRequestException("Password reset token has expired or already been used");
        }

        User user = userRepository.findByUserId(resetToken.getUserId())
                .orElseThrow(() -> new ResourceNotFoundException("User", "userId", resetToken.getUserId()));

        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        user.setFailedLoginAttempts(0);
        user.setAccountLocked(false);
        user.setLockExpiration(null);
        user.setUpdatedAt(Instant.now());
        userRepository.save(user);

        resetToken.setUsed(true);
        passwordResetTokenRepository.save(resetToken);

        auditService.logAsync(AuditLogRequest.builder()
                .userId(user.getUserId())
                .userEmail(user.getOfficialEmail())
                .action(AuditAction.RESET_PASSWORD)
                .module(AuditModule.AUTHENTICATION)
                .entityType("USER")
                .entityId(user.getUserId())
                .description("Successfully reset user password using token")
                .status("SUCCESS")
                .timestamp(Instant.now())
                .build());
    }

    @Override
    @Transactional
    public void changePassword(String userId, ChangePasswordRequest request) {
        log.info("Processing password change for userId '{}'", userId);

        User user = userRepository.findByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "userId", userId));

        if (!passwordEncoder.matches(request.getCurrentPassword(), user.getPassword())) {
            throw new BadRequestException("Current password provided is incorrect");
        }

        if (!validatePasswordStrength(request.getNewPassword())) {
            throw new BadRequestException("New password does not satisfy security policy requirements");
        }

        if (passwordEncoder.matches(request.getNewPassword(), user.getPassword())) {
            throw new BadRequestException("New password cannot be identical to current password");
        }

        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        user.setUpdatedAt(Instant.now());
        userRepository.save(user);

        auditService.logAsync(AuditLogRequest.builder()
                .userId(userId)
                .userEmail(user.getOfficialEmail())
                .action(AuditAction.UPDATE)
                .module(AuditModule.AUTHENTICATION)
                .entityType("USER")
                .entityId(userId)
                .description("User updated credentials via change password request")
                .status("SUCCESS")
                .timestamp(Instant.now())
                .build());
    }

    @Override
    public boolean validatePasswordStrength(String password) {
        if (!StringUtils.hasText(password)) {
            return false;
        }
        return PASSWORD_STRENGTH_PATTERN.matcher(password).matches();
    }

    @Override
    public boolean validatePasswordHistory(String userId, String newPassword) {
        return userRepository.findByUserId(userId)
                .map(user -> !passwordEncoder.matches(newPassword, user.getPassword()))
                .orElse(true);
    }
}
