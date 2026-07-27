package com.techknife.report.controller;

import com.techknife.audit.annotation.Auditable;
import com.techknife.audit.entity.AuditAction;
import com.techknife.audit.entity.AuditModule;
import com.techknife.backend.dto.ApiResponse;
import com.techknife.report.dto.DashboardWidgetDTO;
import com.techknife.report.dto.WidgetLayoutDTO;
import com.techknife.report.entity.DashboardType;
import com.techknife.report.entity.ReportCategoryType;
import com.techknife.report.service.DashboardWidgetService;
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
@RequestMapping("/api/v1/dashboard-widgets")
@RequiredArgsConstructor
@Tag(name = "Report Engine - Dashboard Widgets", description = "Dashboard Widgets & Grid Layout Management API")
@SecurityRequirement(name = "bearerAuth")
public class DashboardWidgetController {

    private final DashboardWidgetService widgetService;

    @PostMapping
    @PreAuthorize("hasAuthority('DASHBOARD_MANAGE') or hasRole('ROLE_ADMIN')")
    @Auditable(action = AuditAction.CREATE, module = AuditModule.REPORT, entityType = "DashboardWidget", description = "Dashboard Modified")
    @Operation(summary = "Create Dashboard Widget")
    public ResponseEntity<ApiResponse<DashboardWidgetDTO>> createWidget(@Valid @RequestBody DashboardWidgetDTO dto) {
        DashboardWidgetDTO result = widgetService.createWidget(dto);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(result, "Dashboard Modified"));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('DASHBOARD_MANAGE') or hasRole('ROLE_ADMIN')")
    @Auditable(action = AuditAction.UPDATE, module = AuditModule.REPORT, entityType = "DashboardWidget", description = "Dashboard Modified")
    @Operation(summary = "Update Dashboard Widget")
    public ResponseEntity<ApiResponse<DashboardWidgetDTO>> updateWidget(
            @PathVariable String id,
            @Valid @RequestBody DashboardWidgetDTO dto) {
        DashboardWidgetDTO result = widgetService.updateWidget(id, dto);
        return ResponseEntity.ok(ApiResponse.success(result, "Dashboard Modified"));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('REPORT_VIEW') or hasRole('ROLE_ADMIN')")
    @Operation(summary = "Get Widget by ID")
    public ResponseEntity<ApiResponse<DashboardWidgetDTO>> getWidgetById(@PathVariable String id) {
        DashboardWidgetDTO result = widgetService.getWidgetById(id);
        return ResponseEntity.ok(ApiResponse.success(result, "Fetched widget successfully"));
    }

    @GetMapping
    @PreAuthorize("hasAuthority('REPORT_VIEW') or hasRole('ROLE_ADMIN')")
    @Operation(summary = "Get All Active Widgets")
    public ResponseEntity<ApiResponse<List<DashboardWidgetDTO>>> getAllActiveWidgets() {
        List<DashboardWidgetDTO> result = widgetService.getAllActiveWidgets();
        return ResponseEntity.ok(ApiResponse.success(result, "Fetched all active widgets successfully"));
    }

    @GetMapping("/category/{category}")
    @PreAuthorize("hasAuthority('REPORT_VIEW') or hasRole('ROLE_ADMIN')")
    @Operation(summary = "Get Widgets by Category")
    public ResponseEntity<ApiResponse<List<DashboardWidgetDTO>>> getWidgetsByCategory(@PathVariable ReportCategoryType category) {
        List<DashboardWidgetDTO> result = widgetService.getWidgetsByCategory(category);
        return ResponseEntity.ok(ApiResponse.success(result, "Fetched category widgets successfully"));
    }

    @GetMapping("/search")
    @PreAuthorize("hasAuthority('REPORT_VIEW') or hasRole('ROLE_ADMIN')")
    @Operation(summary = "Search Widgets")
    public ResponseEntity<ApiResponse<List<DashboardWidgetDTO>>> searchWidgets(@RequestParam(required = false) String query) {
        List<DashboardWidgetDTO> result = widgetService.searchWidgets(query);
        return ResponseEntity.ok(ApiResponse.success(result, "Searched widgets successfully"));
    }

    @PostMapping("/layout")
    @PreAuthorize("hasAuthority('DASHBOARD_MANAGE') or hasRole('ROLE_ADMIN')")
    @Auditable(action = AuditAction.UPDATE, module = AuditModule.REPORT, entityType = "WidgetLayout", description = "Dashboard Modified")
    @Operation(summary = "Save Widget Grid Layout")
    public ResponseEntity<ApiResponse<WidgetLayoutDTO>> saveWidgetLayout(@Valid @RequestBody WidgetLayoutDTO dto) {
        WidgetLayoutDTO result = widgetService.saveWidgetLayout(dto);
        return ResponseEntity.ok(ApiResponse.success(result, "Dashboard Modified"));
    }

    @GetMapping("/layout/user/{userId}")
    @PreAuthorize("hasAuthority('REPORT_VIEW') or hasRole('ROLE_ADMIN')")
    @Operation(summary = "Get Widget Layout by User and Dashboard Type")
    public ResponseEntity<ApiResponse<WidgetLayoutDTO>> getWidgetLayout(
            @PathVariable String userId,
            @RequestParam DashboardType dashboardType) {
        WidgetLayoutDTO result = widgetService.getWidgetLayout(userId, dashboardType);
        return ResponseEntity.ok(ApiResponse.success(result, "Fetched widget layout successfully"));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('DASHBOARD_MANAGE') or hasRole('ROLE_ADMIN')")
    @Auditable(action = AuditAction.DELETE, module = AuditModule.REPORT, entityType = "DashboardWidget", description = "Dashboard Modified")
    @Operation(summary = "Delete Widget")
    public ResponseEntity<ApiResponse<Void>> deleteWidget(@PathVariable String id) {
        widgetService.deleteWidget(id);
        return ResponseEntity.ok(ApiResponse.success(null, "Dashboard Modified"));
    }
}
