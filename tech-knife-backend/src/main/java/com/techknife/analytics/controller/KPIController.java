package com.techknife.analytics.controller;

import com.techknife.analytics.dto.KPIDTO;
import com.techknife.analytics.dto.KPIGroupDTO;
import com.techknife.analytics.dto.KPIHistoryDTO;
import com.techknife.analytics.entity.KPICategory;
import com.techknife.analytics.service.KPIService;
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
@RequestMapping("/api/v1/analytics/kpis")
@RequiredArgsConstructor
@Tag(name = "Analytics - Key Performance Indicators", description = "Enterprise KPI Management & Tracking API")
@SecurityRequirement(name = "bearerAuth")
public class KPIController {

    private final KPIService kpiService;

    @GetMapping
    @PreAuthorize("hasAnyAuthority('KPI_VIEW', 'ANALYTICS_VIEW') or hasRole('ROLE_ADMIN')")
    @Operation(summary = "Get All KPIs")
    public ResponseEntity<ApiResponse<List<KPIDTO>>> getAllKPIs() {
        List<KPIDTO> kpis = kpiService.getAllKPIs();
        return ResponseEntity.ok(ApiResponse.success(kpis, "KPIs retrieved successfully"));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('KPI_VIEW', 'ANALYTICS_VIEW') or hasRole('ROLE_ADMIN')")
    @Operation(summary = "Get KPI by ID")
    public ResponseEntity<ApiResponse<KPIDTO>> getKPIById(@PathVariable String id) {
        KPIDTO kpi = kpiService.getKPIById(id);
        return ResponseEntity.ok(ApiResponse.success(kpi, "KPI retrieved successfully"));
    }

    @GetMapping("/code/{code}")
    @PreAuthorize("hasAnyAuthority('KPI_VIEW', 'ANALYTICS_VIEW') or hasRole('ROLE_ADMIN')")
    @Operation(summary = "Get KPI by Code")
    public ResponseEntity<ApiResponse<KPIDTO>> getKPIByCode(@PathVariable String code) {
        KPIDTO kpi = kpiService.getKPIByCode(code);
        return ResponseEntity.ok(ApiResponse.success(kpi, "KPI retrieved successfully"));
    }

    @GetMapping("/category/{category}")
    @PreAuthorize("hasAnyAuthority('KPI_VIEW', 'ANALYTICS_VIEW') or hasRole('ROLE_ADMIN')")
    @Operation(summary = "Get KPIs by Category")
    public ResponseEntity<ApiResponse<List<KPIDTO>>> getKPIsByCategory(@PathVariable KPICategory category) {
        List<KPIDTO> kpis = kpiService.getKPIsByCategory(category);
        return ResponseEntity.ok(ApiResponse.success(kpis, "Category KPIs retrieved successfully"));
    }

    @PostMapping
    @PreAuthorize("hasAuthority('ANALYTICS_MANAGE') or hasRole('ROLE_ADMIN')")
    @Auditable(action = AuditAction.CREATE, module = AuditModule.ANALYTICS, entityType = "KPI", description = "KPI Created")
    @Operation(summary = "Create KPI")
    public ResponseEntity<ApiResponse<KPIDTO>> createKPI(@Valid @RequestBody KPIDTO dto) {
        KPIDTO created = kpiService.createKPI(dto);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(created, "KPI created successfully"));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('ANALYTICS_MANAGE') or hasRole('ROLE_ADMIN')")
    @Auditable(action = AuditAction.UPDATE, module = AuditModule.ANALYTICS, entityType = "KPI", description = "KPI Updated")
    @Operation(summary = "Update KPI")
    public ResponseEntity<ApiResponse<KPIDTO>> updateKPI(@PathVariable String id, @Valid @RequestBody KPIDTO dto) {
        KPIDTO updated = kpiService.updateKPI(id, dto);
        return ResponseEntity.ok(ApiResponse.success(updated, "KPI updated successfully"));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('ANALYTICS_MANAGE') or hasRole('ROLE_ADMIN')")
    @Auditable(action = AuditAction.DELETE, module = AuditModule.ANALYTICS, entityType = "KPI", description = "KPI Deleted")
    @Operation(summary = "Delete KPI")
    public ResponseEntity<ApiResponse<Void>> deleteKPI(@PathVariable String id) {
        kpiService.deleteKPI(id);
        return ResponseEntity.ok(ApiResponse.success(null, "KPI deleted successfully"));
    }

    @PostMapping("/{id}/refresh")
    @PreAuthorize("hasAnyAuthority('ANALYTICS_MANAGE', 'ANALYTICS_VIEW') or hasRole('ROLE_ADMIN')")
    @Auditable(action = AuditAction.UPDATE, module = AuditModule.ANALYTICS, entityType = "KPI", description = "Analytics Refresh / KPI Refreshed")
    @Operation(summary = "Refresh KPI Value & Record History")
    public ResponseEntity<ApiResponse<KPIDTO>> refreshKPIValue(@PathVariable String id) {
        KPIDTO refreshed = kpiService.refreshKPIValue(id);
        return ResponseEntity.ok(ApiResponse.success(refreshed, "KPI refreshed successfully"));
    }

    @GetMapping("/{id}/history")
    @PreAuthorize("hasAnyAuthority('KPI_VIEW', 'ANALYTICS_VIEW') or hasRole('ROLE_ADMIN')")
    @Operation(summary = "Get KPI Historical Data Points")
    public ResponseEntity<ApiResponse<List<KPIHistoryDTO>>> getKPIHistory(@PathVariable String id) {
        List<KPIHistoryDTO> history = kpiService.getKPIHistory(id);
        return ResponseEntity.ok(ApiResponse.success(history, "KPI history retrieved successfully"));
    }

    @PostMapping("/groups")
    @PreAuthorize("hasAuthority('ANALYTICS_MANAGE') or hasRole('ROLE_ADMIN')")
    @Auditable(action = AuditAction.CREATE, module = AuditModule.ANALYTICS, entityType = "KPIGroup", description = "KPI Group Created")
    @Operation(summary = "Create KPI Group")
    public ResponseEntity<ApiResponse<KPIGroupDTO>> createKPIGroup(@Valid @RequestBody KPIGroupDTO dto) {
        KPIGroupDTO group = kpiService.createKPIGroup(dto);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(group, "KPI group created successfully"));
    }

    @GetMapping("/groups")
    @PreAuthorize("hasAnyAuthority('KPI_VIEW', 'ANALYTICS_VIEW') or hasRole('ROLE_ADMIN')")
    @Operation(summary = "Get All KPI Groups")
    public ResponseEntity<ApiResponse<List<KPIGroupDTO>>> getAllKPIGroups() {
        List<KPIGroupDTO> groups = kpiService.getAllKPIGroups();
        return ResponseEntity.ok(ApiResponse.success(groups, "KPI groups retrieved successfully"));
    }
}
