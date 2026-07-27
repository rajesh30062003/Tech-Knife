package com.techknife.report.controller;

import com.techknife.audit.annotation.Auditable;
import com.techknife.audit.entity.AuditAction;
import com.techknife.audit.entity.AuditModule;
import com.techknife.backend.dto.ApiResponse;
import com.techknife.report.dto.*;
import com.techknife.report.entity.ReportCategoryType;
import com.techknife.report.service.ReportService;
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
import java.util.Map;

@RestController
@RequestMapping("/api/v1/reports")
@RequiredArgsConstructor
@Tag(name = "Report Engine - Reports", description = "Report Builder & Management API")
@SecurityRequirement(name = "bearerAuth")
public class ReportController {

    private final ReportService reportService;

    @PostMapping
    @PreAuthorize("hasAuthority('REPORT_CREATE') or hasRole('ROLE_ADMIN')")
    @Auditable(action = AuditAction.CREATE, module = AuditModule.REPORT, entityType = "Report", description = "Report Created")
    @Operation(summary = "Create Saved Report")
    public ResponseEntity<ApiResponse<ReportDTO>> createReport(@Valid @RequestBody ReportDTO dto) {
        ReportDTO result = reportService.createReport(dto);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(result, "Report created successfully"));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('REPORT_CREATE') or hasRole('ROLE_ADMIN')")
    @Auditable(action = AuditAction.UPDATE, module = AuditModule.REPORT, entityType = "Report", description = "Updated Report")
    @Operation(summary = "Update Report")
    public ResponseEntity<ApiResponse<ReportDTO>> updateReport(
            @PathVariable String id,
            @Valid @RequestBody ReportDTO dto) {
        ReportDTO result = reportService.updateReport(id, dto);
        return ResponseEntity.ok(ApiResponse.success(result, "Report updated successfully"));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('REPORT_VIEW') or hasRole('ROLE_ADMIN')")
    @Operation(summary = "Get Report by ID")
    public ResponseEntity<ApiResponse<ReportDTO>> getReportById(@PathVariable String id) {
        ReportDTO result = reportService.getReportById(id);
        return ResponseEntity.ok(ApiResponse.success(result, "Fetched report successfully"));
    }

    @GetMapping
    @PreAuthorize("hasAuthority('REPORT_VIEW') or hasRole('ROLE_ADMIN')")
    @Operation(summary = "Get All Reports")
    public ResponseEntity<ApiResponse<List<ReportDTO>>> getAllReports() {
        List<ReportDTO> result = reportService.getAllReports();
        return ResponseEntity.ok(ApiResponse.success(result, "Fetched all reports successfully"));
    }

    @GetMapping("/saved")
    @PreAuthorize("hasAuthority('REPORT_VIEW') or hasRole('ROLE_ADMIN')")
    @Operation(summary = "Get Saved Reports")
    public ResponseEntity<ApiResponse<List<ReportDTO>>> getSavedReports() {
        List<ReportDTO> result = reportService.getSavedReports();
        return ResponseEntity.ok(ApiResponse.success(result, "Fetched saved reports successfully"));
    }

    @GetMapping("/category/{category}")
    @PreAuthorize("hasAuthority('REPORT_VIEW') or hasRole('ROLE_ADMIN')")
    @Operation(summary = "Get Reports by Category")
    public ResponseEntity<ApiResponse<List<ReportDTO>>> getReportsByCategory(@PathVariable ReportCategoryType category) {
        List<ReportDTO> result = reportService.getReportsByCategory(category);
        return ResponseEntity.ok(ApiResponse.success(result, "Fetched category reports successfully"));
    }

    @PostMapping("/execute")
    @PreAuthorize("hasAuthority('REPORT_VIEW') or hasRole('ROLE_ADMIN')")
    @Operation(summary = "Execute Dynamic Report Builder Query")
    public ResponseEntity<ApiResponse<Map<String, Object>>> executeReport(@Valid @RequestBody ReportBuildRequest request) {
        Map<String, Object> result = reportService.executeReport(request);
        return ResponseEntity.ok(ApiResponse.success(result, "Report executed successfully"));
    }

    @PostMapping("/search")
    @PreAuthorize("hasAuthority('REPORT_VIEW') or hasRole('ROLE_ADMIN')")
    @Operation(summary = "Search Reports")
    public ResponseEntity<ApiResponse<List<ReportDTO>>> searchReports(@RequestBody ReportSearchRequest searchRequest) {
        List<ReportDTO> result = reportService.searchReports(searchRequest);
        return ResponseEntity.ok(ApiResponse.success(result, "Report search results fetched successfully"));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('REPORT_CREATE') or hasRole('ROLE_ADMIN')")
    @Auditable(action = AuditAction.DELETE, module = AuditModule.REPORT, entityType = "Report", description = "Deleted Report")
    @Operation(summary = "Delete Report")
    public ResponseEntity<ApiResponse<Void>> deleteReport(@PathVariable String id) {
        reportService.deleteReport(id);
        return ResponseEntity.ok(ApiResponse.success(null, "Deleted report successfully"));
    }
}
