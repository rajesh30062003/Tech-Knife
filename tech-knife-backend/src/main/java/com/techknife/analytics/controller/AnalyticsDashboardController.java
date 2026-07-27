package com.techknife.analytics.controller;

import com.techknife.analytics.dto.AnalyticsDashboardDTO;
import com.techknife.analytics.dto.DashboardMetricDTO;
import com.techknife.analytics.dto.DashboardSectionDTO;
import com.techknife.analytics.entity.ExecutiveRole;
import com.techknife.analytics.service.AnalyticsDashboardService;
import com.techknife.audit.annotation.Auditable;
import com.techknife.audit.entity.AuditAction;
import com.techknife.audit.entity.AuditModule;
import com.techknife.backend.dto.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/analytics/dashboards")
@RequiredArgsConstructor
@Tag(name = "Analytics - Dashboards", description = "Enterprise Analytics Dashboard Management API")
@SecurityRequirement(name = "bearerAuth")
public class AnalyticsDashboardController {

    private final AnalyticsDashboardService dashboardService;

    @GetMapping
    @PreAuthorize("hasAnyAuthority('ANALYTICS_VIEW', 'EXECUTIVE_DASHBOARD_VIEW') or hasRole('ROLE_ADMIN')")
    @Auditable(action = AuditAction.EXPORT, module = AuditModule.ANALYTICS, entityType = "AnalyticsDashboard", description = "Dashboard Access")
    @Operation(summary = "Get All Analytics Dashboards")
    public ResponseEntity<ApiResponse<List<AnalyticsDashboardDTO>>> getAllDashboards() {
        List<AnalyticsDashboardDTO> dashboards = dashboardService.getAllDashboards();
        return ResponseEntity.ok(ApiResponse.success(dashboards, "Dashboards retrieved successfully"));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('ANALYTICS_VIEW', 'EXECUTIVE_DASHBOARD_VIEW') or hasRole('ROLE_ADMIN')")
    @Operation(summary = "Get Dashboard by ID")
    public ResponseEntity<ApiResponse<AnalyticsDashboardDTO>> getDashboardById(@PathVariable String id) {
        AnalyticsDashboardDTO dashboard = dashboardService.getDashboardById(id);
        return ResponseEntity.ok(ApiResponse.success(dashboard, "Dashboard retrieved successfully"));
    }

    @GetMapping("/code/{code}")
    @PreAuthorize("hasAnyAuthority('ANALYTICS_VIEW', 'EXECUTIVE_DASHBOARD_VIEW') or hasRole('ROLE_ADMIN')")
    @Operation(summary = "Get Dashboard by Code")
    public ResponseEntity<ApiResponse<AnalyticsDashboardDTO>> getDashboardByCode(@PathVariable String code) {
        AnalyticsDashboardDTO dashboard = dashboardService.getDashboardByCode(code);
        return ResponseEntity.ok(ApiResponse.success(dashboard, "Dashboard retrieved successfully"));
    }

    @GetMapping("/role/{role}")
    @PreAuthorize("hasAnyAuthority('ANALYTICS_VIEW', 'EXECUTIVE_DASHBOARD_VIEW') or hasRole('ROLE_ADMIN')")
    @Operation(summary = "Get Dashboard by Executive Role")
    public ResponseEntity<ApiResponse<AnalyticsDashboardDTO>> getDashboardByRole(@PathVariable ExecutiveRole role) {
        AnalyticsDashboardDTO dashboard = dashboardService.getDashboardByRole(role);
        return ResponseEntity.ok(ApiResponse.success(dashboard, "Dashboard retrieved successfully"));
    }

    @PostMapping
    @PreAuthorize("hasAuthority('ANALYTICS_MANAGE') or hasRole('ROLE_ADMIN')")
    @Auditable(action = AuditAction.CREATE, module = AuditModule.ANALYTICS, entityType = "AnalyticsDashboard", description = "Created Dashboard")
    @Operation(summary = "Create Analytics Dashboard")
    public ResponseEntity<ApiResponse<AnalyticsDashboardDTO>> createDashboard(@Valid @RequestBody AnalyticsDashboardDTO dto) {
        AnalyticsDashboardDTO created = dashboardService.createDashboard(dto);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(created, "Dashboard created successfully"));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('ANALYTICS_MANAGE') or hasRole('ROLE_ADMIN')")
    @Auditable(action = AuditAction.UPDATE, module = AuditModule.ANALYTICS, entityType = "AnalyticsDashboard", description = "Updated Dashboard")
    @Operation(summary = "Update Analytics Dashboard")
    public ResponseEntity<ApiResponse<AnalyticsDashboardDTO>> updateDashboard(
            @PathVariable String id,
            @Valid @RequestBody AnalyticsDashboardDTO dto) {
        AnalyticsDashboardDTO updated = dashboardService.updateDashboard(id, dto);
        return ResponseEntity.ok(ApiResponse.success(updated, "Dashboard updated successfully"));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('ANALYTICS_MANAGE') or hasRole('ROLE_ADMIN')")
    @Auditable(action = AuditAction.DELETE, module = AuditModule.ANALYTICS, entityType = "AnalyticsDashboard", description = "Deleted Dashboard")
    @Operation(summary = "Delete Analytics Dashboard")
    public ResponseEntity<ApiResponse<Void>> deleteDashboard(@PathVariable String id) {
        dashboardService.deleteDashboard(id);
        return ResponseEntity.ok(ApiResponse.success(null, "Dashboard deleted successfully"));
    }

    @PostMapping("/{dashboardId}/sections")
    @PreAuthorize("hasAuthority('ANALYTICS_MANAGE') or hasRole('ROLE_ADMIN')")
    @Auditable(action = AuditAction.UPDATE, module = AuditModule.ANALYTICS, entityType = "AnalyticsDashboard", description = "Added Section to Dashboard")
    @Operation(summary = "Add Section to Dashboard")
    public ResponseEntity<ApiResponse<AnalyticsDashboardDTO>> addSection(
            @PathVariable String dashboardId,
            @Valid @RequestBody DashboardSectionDTO sectionDTO) {
        AnalyticsDashboardDTO updated = dashboardService.addSection(dashboardId, sectionDTO);
        return ResponseEntity.ok(ApiResponse.success(updated, "Section added successfully"));
    }

    @PostMapping("/{dashboardId}/sections/{sectionId}/metrics")
    @PreAuthorize("hasAuthority('ANALYTICS_MANAGE') or hasRole('ROLE_ADMIN')")
    @Auditable(action = AuditAction.UPDATE, module = AuditModule.ANALYTICS, entityType = "AnalyticsDashboard", description = "Added Metric to Dashboard Section")
    @Operation(summary = "Add Metric to Dashboard Section")
    public ResponseEntity<ApiResponse<AnalyticsDashboardDTO>> addMetricToSection(
            @PathVariable String dashboardId,
            @PathVariable String sectionId,
            @Valid @RequestBody DashboardMetricDTO metricDTO) {
        AnalyticsDashboardDTO updated = dashboardService.addMetricToSection(dashboardId, sectionId, metricDTO);
        return ResponseEntity.ok(ApiResponse.success(updated, "Metric added successfully"));
    }
}
