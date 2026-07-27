package com.techknife.backend.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;

@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@Document(collection = "audit_logs")
public class AuditLog extends BaseEntity {

    private String principal;
    private String action;
    private String module;
    private String method;
    private String ipAddress;
    private String userAgent;
    private String status;
    private String requestPayload;
    private String responsePayload;
    private long executionTimeMs;
    private Instant timestamp;
}
