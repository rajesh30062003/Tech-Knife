package com.techknife.iam.service.impl;

import com.techknife.audit.dto.AuditLogRequest;
import com.techknife.audit.entity.AuditAction;
import com.techknife.audit.entity.AuditModule;
import com.techknife.audit.service.AuditService;
import com.techknife.backend.exception.ResourceNotFoundException;
import com.techknife.backend.exception.UnauthorizedException;
import com.techknife.iam.dto.response.LoginHistoryResponse;
import com.techknife.iam.dto.response.SessionResponse;
import com.techknife.iam.entity.LoginHistory;
import com.techknife.iam.entity.RefreshToken;
import com.techknife.iam.repository.LoginHistoryRepository;
import com.techknife.iam.repository.RefreshTokenRepository;
import com.techknife.iam.service.SessionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Implementation of SessionService providing active session listing, remote device revocation, and authentication history paging.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class SessionServiceImpl implements SessionService {

    private final RefreshTokenRepository refreshTokenRepository;
    private final LoginHistoryRepository loginHistoryRepository;
    private final AuditService auditService;

    @Override
    @Transactional(readOnly = true)
    public List<SessionResponse> getActiveSessions(String userId) {
        log.info("Fetching active login sessions for userId '{}'", userId);
        Instant now = Instant.now();

        List<RefreshToken> tokens = refreshTokenRepository.findByUserId(userId);

        return tokens.stream()
                .filter(token -> !token.isRevoked() && token.getExpiry().isAfter(now))
                .map(token -> SessionResponse.builder()
                        .sessionId(token.getId())
                        .userId(token.getUserId())
                        .deviceInfo(token.getDeviceInfo())
                        .ipAddress(token.getIpAddress())
                        .createdAt(token.getCreatedAt())
                        .lastActiveAt(token.getCreatedAt())
                        .expiresAt(token.getExpiry())
                        .active(true)
                        .currentSession(false)
                        .build())
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void terminateSession(String userId, String sessionId) {
        log.info("Terminating session '{}' for userId '{}'", sessionId, userId);
        RefreshToken token = refreshTokenRepository.findById(sessionId)
                .orElseGet(() -> refreshTokenRepository.findByToken(sessionId)
                        .orElseThrow(() -> new ResourceNotFoundException("Session", "sessionId", sessionId)));

        if (!token.getUserId().equals(userId)) {
            log.warn("Unauthorized attempt by userId '{}' to terminate session belonging to userId '{}'", userId, token.getUserId());
            throw new UnauthorizedException("You are not authorized to terminate this session");
        }

        token.setRevoked(true);
        refreshTokenRepository.save(token);

        auditService.logAsync(AuditLogRequest.builder()
                .userId(userId)
                .action(AuditAction.DELETE)
                .module(AuditModule.AUTHENTICATION)
                .entityType("REFRESH_TOKEN_SESSION")
                .entityId(sessionId)
                .description("Remote user session terminated")
                .status("SUCCESS")
                .timestamp(Instant.now())
                .build());
    }

    @Override
    @Transactional
    public void terminateAllSessions(String userId) {
        log.info("Terminating all active sessions for userId '{}'", userId);
        List<RefreshToken> tokens = refreshTokenRepository.findByUserId(userId);

        for (RefreshToken token : tokens) {
            if (!token.isRevoked()) {
                token.setRevoked(true);
            }
        }
        refreshTokenRepository.saveAll(tokens);

        auditService.logAsync(AuditLogRequest.builder()
                .userId(userId)
                .action(AuditAction.DELETE)
                .module(AuditModule.AUTHENTICATION)
                .entityType("USER_SESSIONS")
                .entityId(userId)
                .description("All user active sessions terminated")
                .status("SUCCESS")
                .timestamp(Instant.now())
                .build());
    }

    @Override
    @Transactional(readOnly = true)
    public Page<LoginHistoryResponse> getLoginHistory(String userId, Pageable pageable) {
        log.info("Retrieving login history page for userId '{}'", userId);
        Page<LoginHistory> historyPage = loginHistoryRepository.findByUserIdOrderByLoginTimeDesc(userId, pageable);

        return historyPage.map(history -> LoginHistoryResponse.builder()
                .id(history.getId())
                .userId(history.getUserId())
                .loginTime(history.getLoginTime())
                .ipAddress(history.getIpAddress())
                .userAgent(history.getUserAgent())
                .deviceInfo(history.getDeviceInfo())
                .location(history.getLocation())
                .status(history.getStatus())
                .failureReason(history.getFailureReason())
                .build());
    }
}
