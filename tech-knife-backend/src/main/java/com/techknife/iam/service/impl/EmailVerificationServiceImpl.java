package com.techknife.iam.service.impl;

import com.techknife.audit.dto.AuditLogRequest;
import com.techknife.audit.entity.AuditAction;
import com.techknife.audit.entity.AuditModule;
import com.techknife.audit.service.AuditService;
import com.techknife.backend.exception.BadRequestException;
import com.techknife.backend.exception.ResourceNotFoundException;
import com.techknife.iam.dto.request.ResendVerificationRequest;
import com.techknife.iam.dto.request.VerifyEmailRequest;
import com.techknife.iam.entity.EmailVerificationToken;
import com.techknife.iam.entity.User;
import com.techknife.iam.repository.EmailVerificationTokenRepository;
import com.techknife.iam.repository.UserRepository;
import com.techknife.iam.service.EmailVerificationService;
import com.techknife.mail.EmailService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.UUID;

/**
 * Implementation of EmailVerificationService supporting email validation tokens, expiration windows, and email dispatching.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class EmailVerificationServiceImpl implements EmailVerificationService {

    private final UserRepository userRepository;
    private final EmailVerificationTokenRepository emailVerificationTokenRepository;
    private final EmailService emailService;
    private final AuditService auditService;

    @Override
    @Transactional
    public String generateVerificationToken(String userId) {
        log.info("Generating email verification token for userId '{}'", userId);
        User user = userRepository.findByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "userId", userId));

        String token = UUID.randomUUID().toString();
        Instant now = Instant.now();

        EmailVerificationToken tokenEntity = EmailVerificationToken.builder()
                .token(token)
                .userId(user.getUserId())
                .expiry(now.plus(24, ChronoUnit.HOURS))
                .verified(false)
                .createdAt(now)
                .build();

        emailVerificationTokenRepository.save(tokenEntity);
        return token;
    }

    @Override
    @Transactional
    public boolean verifyEmail(VerifyEmailRequest request) {
        log.info("Processing email verification request");
        EmailVerificationToken verificationToken = emailVerificationTokenRepository.findByToken(request.getVerificationToken())
                .orElseThrow(() -> new BadRequestException("Invalid or expired email verification token"));

        if (verificationToken.isVerified() || verificationToken.getExpiry().isBefore(Instant.now())) {
            throw new BadRequestException("Email verification token has expired or has already been used");
        }

        User user = userRepository.findByUserId(verificationToken.getUserId())
                .orElseThrow(() -> new ResourceNotFoundException("User", "userId", verificationToken.getUserId()));

        user.setEmailVerified(true);
        user.setUpdatedAt(Instant.now());
        userRepository.save(user);

        verificationToken.setVerified(true);
        emailVerificationTokenRepository.save(verificationToken);

        auditService.logAsync(AuditLogRequest.builder()
                .userId(user.getUserId())
                .userEmail(user.getOfficialEmail())
                .action(AuditAction.VERIFY_EMAIL)
                .module(AuditModule.AUTHENTICATION)
                .entityType("USER")
                .entityId(user.getUserId())
                .description("Successfully verified user email address")
                .status("SUCCESS")
                .timestamp(Instant.now())
                .build());

        return true;
    }

    @Override
    @Transactional
    public void resendVerificationEmail(ResendVerificationRequest request) {
        log.info("Resending email verification token for email '{}'", request.getEmail());
        User user = userRepository.findByOfficialEmail(request.getEmail())
                .orElseThrow(() -> new ResourceNotFoundException("User", "email", request.getEmail()));

        if (user.isEmailVerified()) {
            throw new BadRequestException("User email address is already verified");
        }

        String token = generateVerificationToken(user.getUserId());

        try {
            emailService.sendSimpleEmail(user.getOfficialEmail(),
                    "Tech Knife Platform - Verify Your Email Address",
                    "Hello " + user.getFirstName() + ",\n\n"
                            + "Please click or submit the following email verification token:\n"
                            + token + "\n\n"
                            + "This token will expire in 24 hours.\n\n"
                            + "Regards,\nTech Knife IAM Team");
        } catch (Exception ex) {
            log.error("Failed to send verification email to '{}': {}", user.getOfficialEmail(), ex.getMessage());
        }

        auditService.logAsync(AuditLogRequest.builder()
                .userId(user.getUserId())
                .userEmail(user.getOfficialEmail())
                .action(AuditAction.VERIFY_EMAIL)
                .module(AuditModule.AUTHENTICATION)
                .entityType("EMAIL_VERIFICATION_TOKEN")
                .entityId(token)
                .description("Resent email verification token")
                .status("SUCCESS")
                .timestamp(Instant.now())
                .build());
    }
}
