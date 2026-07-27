package com.techknife.report.controller;

import com.techknife.backend.dto.ApiResponse;
import com.techknife.report.dto.ExecutiveDashboardDTO;
import com.techknife.report.dto.KpiReportDTO;
import com.techknife.report.entity.DashboardType;
import com.techknife.report.service.ReportService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/executive-dashboards")
@RequiredArgsConstructor
@Tag(name = "Report Engine - Executive Dashboards & KPIs", description = "KPI Reports & Executive Level Overview Dashboards API")
@SecurityRequirement(name = "bearerAuth")
public class ExecutiveDashboardController {

    private final ReportService reportService;

    @GetMapping("/{dashboardType}")
    @PreAuthorize("hasAuthority('REPORT_VIEW') or hasRole('ROLE_ADMIN')")
    @Operation(summary = "Get Executive Dashboard (CEO, HR, Finance, Sales, Project, Operations)")
    public ResponseEntity<ApiResponse<ExecutiveDashboardDTO>> getExecutiveDashboard(@PathVariable DashboardType dashboardType) {
        ExecutiveDashboardDTO result = reportService.generateExecutiveDashboard(dashboardType);
        return ResponseEntity.ok(ApiResponse.success(result, "Fetched executive dashboard successfully"));
    }

    @GetMapping("/kpis")
    @PreAuthorize("hasAuthority('REPORT_VIEW') or hasRole('ROLE_ADMIN')")
    @Operation(summary = "Get All KPI Metrics")
    public ResponseEntity<ApiResponse<List<KpiReportDTO>>> getAllKpis() {
        List<KpiReportDTO> result = reportService.getAllKpiReports();
        return ResponseEntity.ok(ApiResponse.success(result, "Fetched all KPI reports successfully"));
    }

    @GetMapping("/kpi/{metricKey}")
    @PreAuthorize("hasAuthority('REPORT_VIEW') or hasRole('ROLE_ADMIN')")
    @Operation(summary = "Get Specific KPI Metric by Key")
    public ResponseEntity<ApiResponse<KpiReportDTO>> getKpiReport(@PathVariable String metricKey) {
        KpiReportDTO result = reportService.generateKpiReport(metricKey);
        return ResponseEntity.ok(ApiResponse.success(result, "Fetched KPI metric successfully"));
    }
}
