package com.techknife.audit.dto;

import com.techknife.audit.entity.AuditAction;
import com.techknife.audit.entity.AuditModule;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

/**
 * Data Transfer Object for recording an audit log event.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AuditLogRequest {

    private String userId;
    private String userName;
    private String userEmail;
    private String userRole;
    private AuditAction action;
    private AuditModule module;
    private String entityType;
    private String entityId;
    private String description;
    private String oldValue;
    private String newValue;
    private String ipAddress;
    private String userAgent;
    private String requestMethod;
    private String requestUri;
    private String status;
    private Long executionTime;
    private String sessionId;
    private Instant timestamp;
}
