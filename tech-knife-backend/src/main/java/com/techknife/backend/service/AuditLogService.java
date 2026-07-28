package com.techknife.backend.service;

import com.techknife.backend.dto.PagedResponse;
import com.techknife.backend.entity.AuditLog;

public interface AuditLogService {
    PagedResponse<AuditLog> getPaginatedAuditLogs(int page, int size, String principal, String module, String status);
    AuditLog getAuditLogById(String id);
    void logAction(String action, String module, String entityType, String entityId, String performedBy, String details, String ipAddress);
}

