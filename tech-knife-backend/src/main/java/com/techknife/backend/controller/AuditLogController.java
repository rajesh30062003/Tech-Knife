package com.techknife.backend.controller;

import com.techknife.backend.dto.ApiResponse;
import com.techknife.backend.dto.PagedResponse;
import com.techknife.backend.entity.AuditLog;
import com.techknife.backend.service.AuditLogService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/audit-logs")
@RequiredArgsConstructor
@Tag(name = "Audit Logging & Security Trail", description = "System Controller & Service Invocation Audit Trail Logs")
public class AuditLogController {

    private final AuditLogService auditLogService;

    @GetMapping
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'ADMIN')")
    @Operation(summary = "Retrieve paginated security and operation audit logs")
    public ResponseEntity<ApiResponse<PagedResponse<AuditLog>>> getAuditLogs(
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "15") int size,
            @RequestParam(value = "principal", required = false) String principal,
            @RequestParam(value = "module", required = false) String module,
            @RequestParam(value = "status", required = false) String status) {
        PagedResponse<AuditLog> auditLogs = auditLogService.getPaginatedAuditLogs(page, size, principal, module, status);
        return ResponseEntity.ok(ApiResponse.success(auditLogs, "Audit logs retrieved successfully"));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'ADMIN')")
    @Operation(summary = "Retrieve detailed audit log entry by Mongo ID")
    public ResponseEntity<ApiResponse<AuditLog>> getAuditLogById(@PathVariable("id") String id) {
        AuditLog auditLog = auditLogService.getAuditLogById(id);
        return ResponseEntity.ok(ApiResponse.success(auditLog, "Audit log entry details fetched"));
    }
}
