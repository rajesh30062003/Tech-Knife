package com.techknife.audit.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;

/**
 * MongoDB document entity capturing immutable audit trail entries.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "audit_logs")
public class AuditLog {

    @Id
    private String id;

    @Indexed
    private Instant timestamp;

    @Indexed
    private String userId;

    private String userName;

    private String userEmail;

    private String userRole;

    @Indexed
    private AuditAction action;

    @Indexed
    private AuditModule module;

    private String entityType;

    @Indexed
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
}
