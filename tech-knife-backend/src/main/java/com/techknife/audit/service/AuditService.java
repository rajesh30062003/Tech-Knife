package com.techknife.audit.service;

import com.techknife.audit.dto.AuditLogRequest;
import com.techknife.audit.dto.AuditLogResponse;
import com.techknife.audit.entity.AuditAction;
import com.techknife.audit.entity.AuditModule;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.Instant;

/**
 * Service contract for logging and retrieving system audit events.
 */
public interface AuditService {

    /**
     * Synchronously records an audit log entry.
     */
    void logEvent(AuditLogRequest request);

    /**
     * Asynchronously records an audit log entry to prevent blocking core operations.
     */
    void logAsync(AuditLogRequest request);

    /**
     * Retrieves an audit log record by ID.
     */
    AuditLogResponse getAuditLogById(String id);

    /**
     * Retrieves audit records for a specific user ID.
     */
    Page<AuditLogResponse> getAuditLogsByUserId(String userId, Pageable pageable);

    /**
     * Retrieves audit records for a specific application module.
     */
    Page<AuditLogResponse> getAuditLogsByModule(AuditModule module, Pageable pageable);

    /**
     * Retrieves audit records for a specific audit action.
     */
    Page<AuditLogResponse> getAuditLogsByAction(AuditAction action, Pageable pageable);

    /**
     * Retrieves audit records recorded within a timestamp range.
     */
    Page<AuditLogResponse> getAuditLogsByDateRange(Instant start, Instant end, Pageable pageable);
}
