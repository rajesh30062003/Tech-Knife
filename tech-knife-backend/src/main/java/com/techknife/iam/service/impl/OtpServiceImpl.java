package com.techknife.iam.service.impl;

import com.techknife.audit.dto.AuditLogRequest;
import com.techknife.audit.entity.AuditAction;
import com.techknife.audit.entity.AuditModule;
import com.techknife.audit.service.AuditService;
import com.techknife.backend.exception.BadRequestException;
import com.techknife.backend.exception.ResourceNotFoundException;
import com.techknife.iam.dto.request.SendOtpRequest;
import com.techknife.iam.dto.request.VerifyOtpRequest;
import com.techknife.iam.entity.OtpVerification;
import com.techknife.iam.entity.User;
import com.techknife.iam.repository.OtpVerificationRepository;
import com.techknife.iam.repository.UserRepository;
import com.techknife.iam.service.OtpService;
import com.techknife.mail.EmailService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.security.SecureRandom;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Optional;

/**
 * Implementation of OtpService handling secure multi-factor random OTP generation, retry limit checks, and email delivery.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class OtpServiceImpl implements OtpService {

    private static final int MAX_ATTEMPTS = 5;
    private static final int OTP_EXPIRY_MINUTES = 5;
    private static final SecureRandom SECURE_RANDOM = new SecureRandom();

    private final OtpVerificationRepository otpVerificationRepository;
    private final UserRepository userRepository;
    private final EmailService emailService;
    private final AuditService auditService;

    @Override
    @Transactional
    public String generateOtp(String userId, String purpose) {
        log.info("Generating OTP challenge for userId '{}', purpose '{}'", userId, purpose);
        String code = String.format("%06d", SECURE_RANDOM.nextInt(1_000_000));
        Instant now = Instant.now();
        String targetPurpose = StringUtils.hasText(purpose) ? purpose.trim().toUpperCase() : "AUTHENTICATION";

        OtpVerification otpEntity = OtpVerification.builder()
                .userId(userId)
                .otp(code)
                .purpose(targetPurpose)
                .expiry(now.plus(OTP_EXPIRY_MINUTES, ChronoUnit.MINUTES))
                .attempts(0)
                .verified(false)
                .createdAt(now)
                .build();

        otpVerificationRepository.save(otpEntity);
        return code;
    }

    @Override
    @Transactional
    public void sendOtp(SendOtpRequest request) {
        log.info("Processing send OTP request for userId/email challenge");
        User user;

        if (StringUtils.hasText(request.getUserId())) {
            user = userRepository.findByUserId(request.getUserId())
                    .orElseThrow(() -> new ResourceNotFoundException("User", "userId", request.getUserId()));
        } else if (StringUtils.hasText(request.getEmail())) {
            user = userRepository.findByOfficialEmail(request.getEmail())
                    .orElseThrow(() -> new ResourceNotFoundException("User", "email", request.getEmail()));
        } else {
            throw new BadRequestException("Either userId or email must be provided to send OTP");
        }

        String purpose = StringUtils.hasText(request.getPurpose()) ? request.getPurpose() : "AUTHENTICATION";
        String otpCode = generateOtp(user.getUserId(), purpose);

        try {
            emailService.sendSimpleEmail(user.getOfficialEmail(),
                    "Tech Knife Platform - Security Verification OTP",
                    "Hello " + user.getFirstName() + ",\n\n"
                            + "Your One-Time Password (OTP) for " + purpose + " is:\n\n"
                            + otpCode + "\n\n"
                            + "This code is valid for " + OTP_EXPIRY_MINUTES + " minutes. Do not share this code with anyone.\n\n"
                            + "Regards,\nTech Knife IAM Security Team");
        } catch (Exception ex) {
            log.error("Failed to send OTP email notification to '{}': {}", user.getOfficialEmail(), ex.getMessage());
        }

        auditService.logAsync(AuditLogRequest.builder()
                .userId(user.getUserId())
                .userEmail(user.getOfficialEmail())
                .action(AuditAction.CREATE)
                .module(AuditModule.AUTHENTICATION)
                .entityType("OTP_CHALLENGE")
                .entityId(user.getUserId())
                .description("Dispatched OTP verification code for purpose: " + purpose)
                .status("SUCCESS")
                .timestamp(Instant.now())
                .build());
    }

    @Override
    @Transactional
    public boolean verifyOtp(VerifyOtpRequest request) {
        log.info("Verifying OTP challenge for userId '{}'", request.getUserId());

        if (isMaxRetryExceeded(request.getUserId(), request.getPurpose())) {
            throw new BadRequestException("Maximum OTP verification retry attempts exceeded. Please request a new OTP.");
        }

        String targetPurpose = StringUtils.hasText(request.getPurpose()) ? request.getPurpose().trim().toUpperCase() : "AUTHENTICATION";

        Optional<OtpVerification> otpOpt = otpVerificationRepository
                .findFirstByUserIdAndPurposeOrderByCreatedAtDesc(request.getUserId(), targetPurpose);

        if (otpOpt.isEmpty()) {
            log.warn("No OTP record found for userId '{}', purpose '{}'", request.getUserId(), targetPurpose);
            return false;
        }

        OtpVerification otpEntity = otpOpt.get();

        if (otpEntity.isVerified() || otpEntity.getExpiry().isBefore(Instant.now())) {
            log.warn("OTP record for userId '{}' is already verified or expired", request.getUserId());
            return false;
        }

        otpEntity.setAttempts(otpEntity.getAttempts() + 1);

        boolean matches = otpEntity.getOtp().equals(request.getOtp());
        if (matches) {
            otpEntity.setVerified(true);
            log.info("OTP verification successful for userId '{}'", request.getUserId());
        } else {
            log.warn("OTP code mismatch for userId '{}' (attempt {}/{})", request.getUserId(), otpEntity.getAttempts(), MAX_ATTEMPTS);
        }

        otpVerificationRepository.save(otpEntity);

        auditService.logAsync(AuditLogRequest.builder()
                .userId(request.getUserId())
                .action(AuditAction.VERIFY_EMAIL)
                .module(AuditModule.AUTHENTICATION)
                .entityType("OTP_VERIFICATION")
                .entityId(otpEntity.getId())
                .description(matches ? "OTP verification passed" : "OTP verification failed")
                .status(matches ? "SUCCESS" : "FAILED")
                .timestamp(Instant.now())
                .build());

        return matches;
    }

    @Override
    @Transactional
    public void expireOtp(String userId, String purpose) {
        log.info("Expiring active OTP challenges for userId '{}', purpose '{}'", userId, purpose);
        String targetPurpose = StringUtils.hasText(purpose) ? purpose.trim().toUpperCase() : "AUTHENTICATION";

        otpVerificationRepository.findFirstByUserIdAndPurposeOrderByCreatedAtDesc(userId, targetPurpose)
                .ifPresent(otp -> {
                    otp.setExpiry(Instant.now());
                    otpVerificationRepository.save(otp);
                });
    }

    @Override
    public boolean isMaxRetryExceeded(String userId, String purpose) {
        String targetPurpose = StringUtils.hasText(purpose) ? purpose.trim().toUpperCase() : "AUTHENTICATION";
        return otpVerificationRepository.findFirstByUserIdAndPurposeOrderByCreatedAtDesc(userId, targetPurpose)
                .map(otp -> otp.getAttempts() >= MAX_ATTEMPTS)
                .orElse(false);
    }
}
