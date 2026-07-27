package com.techknife.analytics.controller;

import com.techknife.analytics.dto.SystemHealthDTO;
import com.techknife.analytics.service.SystemHealthService;
import com.techknife.audit.annotation.Auditable;
import com.techknife.audit.entity.AuditAction;
import com.techknife.audit.entity.AuditModule;
import com.techknife.backend.dto.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/analytics/system-health")
@RequiredArgsConstructor
@Tag(name = "Analytics - System Health", description = "System Health & Infrastructure Monitoring API")
@SecurityRequirement(name = "bearerAuth")
public class SystemHealthController {

    private final SystemHealthService systemHealthService;

    @GetMapping
    @PreAuthorize("hasAnyAuthority('SYSTEM_HEALTH_VIEW', 'ANALYTICS_VIEW') or hasRole('ROLE_ADMIN')")
    @Auditable(action = AuditAction.EXPORT, module = AuditModule.ANALYTICS, entityType = "SystemHealth", description = "System Health Checked")
    @Operation(summary = "Get Current System Health & Metrics")
    public ResponseEntity<ApiResponse<SystemHealthDTO>> getCurrentSystemHealth() {
        SystemHealthDTO health = systemHealthService.getCurrentSystemHealth();
        return ResponseEntity.ok(ApiResponse.success(health, "System health retrieved successfully"));
    }

    @PostMapping("/snapshot")
    @PreAuthorize("hasAnyAuthority('SYSTEM_HEALTH_VIEW', 'ANALYTICS_MANAGE') or hasRole('ROLE_ADMIN')")
    @Auditable(action = AuditAction.CREATE, module = AuditModule.ANALYTICS, entityType = "SystemHealth", description = "Health Snapshot Captured")
    @Operation(summary = "Capture Fresh System Health Snapshot")
    public ResponseEntity<ApiResponse<SystemHealthDTO>> captureSystemHealthSnapshot() {
        SystemHealthDTO health = systemHealthService.captureSystemHealthSnapshot();
        return ResponseEntity.ok(ApiResponse.success(health, "System health snapshot captured successfully"));
    }
}
