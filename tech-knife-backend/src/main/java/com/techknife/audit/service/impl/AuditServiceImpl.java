package com.techknife.audit.service.impl;

import com.techknife.audit.dto.AuditLogRequest;
import com.techknife.audit.dto.AuditLogResponse;
import com.techknife.audit.entity.AuditAction;
import com.techknife.audit.entity.AuditLog;
import com.techknife.audit.entity.AuditModule;
import com.techknife.audit.repository.AuditLogRepository;
import com.techknife.audit.service.AuditService;
import com.techknife.backend.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.Instant;
import java.util.regex.Pattern;

/**
 * Service implementation for audit logging operations with security sanitization and async capabilities.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AuditServiceImpl implements AuditService {

    private static final Pattern SENSITIVE_KEY_PATTERN = Pattern.compile(
            "(?i)\"(password|secret|token|jwt|refreshtoken|otp|pin|credential|authorization)\"\\s*:\\s*\"[^\"]*\"",
            Pattern.CASE_INSENSITIVE
    );

    private final AuditLogRepository auditLogRepository;

    @Override
    public void logEvent(AuditLogRequest request) {
        if (request == null) {
            log.warn("Audit log request skipped: null payload");
            return;
        }

        try {
            AuditLog auditLog = AuditLog.builder()
                    .timestamp(request.getTimestamp() != null ? request.getTimestamp() : Instant.now())
                    .userId(request.getUserId())
                    .userName(request.getUserName())
                    .userEmail(request.getUserEmail())
                    .userRole(request.getUserRole())
                    .action(request.getAction() != null ? request.getAction() : AuditAction.UPDATE)
                    .module(request.getModule() != null ? request.getModule() : AuditModule.SYSTEM)
                    .entityType(request.getEntityType())
                    .entityId(request.getEntityId())
                    .description(request.getDescription())
                    .oldValue(sanitizeSensitiveInfo(request.getOldValue()))
                    .newValue(sanitizeSensitiveInfo(request.getNewValue()))
                    .ipAddress(request.getIpAddress())
                    .userAgent(request.getUserAgent())
                    .requestMethod(request.getRequestMethod())
                    .requestUri(request.getRequestUri())
                    .status(StringUtils.hasText(request.getStatus()) ? request.getStatus() : "SUCCESS")
                    .executionTime(request.getExecutionTime())
                    .sessionId(request.getSessionId())
                    .build();

            auditLogRepository.save(auditLog);
            log.debug("Recorded audit log [{}] for action '{}' on module '{}'",
                    auditLog.getId(), auditLog.getAction(), auditLog.getModule());

        } catch (Exception ex) {
            log.error("Failed to write audit log entry to database: {}", ex.getMessage(), ex);
        }
    }

    @Async
    @Override
    public void logAsync(AuditLogRequest request) {
        logEvent(request);
    }

    @Override
    public AuditLogResponse getAuditLogById(String id) {
        AuditLog auditLog = auditLogRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("AuditLog", "id", id));
        return mapToResponse(auditLog);
    }

    @Override
    public Page<AuditLogResponse> getAuditLogsByUserId(String userId, Pageable pageable) {
        return auditLogRepository.findByUserId(userId, pageable).map(this::mapToResponse);
    }

    @Override
    public Page<AuditLogResponse> getAuditLogsByModule(AuditModule module, Pageable pageable) {
        return auditLogRepository.findByModule(module, pageable).map(this::mapToResponse);
    }

    @Override
    public Page<AuditLogResponse> getAuditLogsByAction(AuditAction action, Pageable pageable) {
        return auditLogRepository.findByAction(action, pageable).map(this::mapToResponse);
    }

    @Override
    public Page<AuditLogResponse> getAuditLogsByDateRange(Instant start, Instant end, Pageable pageable) {
        return auditLogRepository.findByTimestampBetween(start, end, pageable).map(this::mapToResponse);
    }

    private String sanitizeSensitiveInfo(String input) {
        if (!StringUtils.hasText(input)) {
            return input;
        }
        // Mask passwords, JWTs, secret keys, OTPs
        return SENSITIVE_KEY_PATTERN.matcher(input).replaceAll("\"$1\":\"[REDACTED]\"");
    }

    private AuditLogResponse mapToResponse(AuditLog log) {
        return AuditLogResponse.builder()
                .id(log.getId())
                .timestamp(log.getTimestamp())
                .userId(log.getUserId())
                .userName(log.getUserName())
                .userEmail(log.getUserEmail())
                .userRole(log.getUserRole())
                .action(log.getAction())
                .module(log.getModule())
                .entityType(log.getEntityType())
                .entityId(log.getEntityId())
                .description(log.getDescription())
                .oldValue(log.getOldValue())
                .newValue(log.getNewValue())
                .ipAddress(log.getIpAddress())
                .userAgent(log.getUserAgent())
                .requestMethod(log.getRequestMethod())
                .requestUri(log.getRequestUri())
                .status(log.getStatus())
                .executionTime(log.getExecutionTime())
                .sessionId(log.getSessionId())
                .build();
    }
}
