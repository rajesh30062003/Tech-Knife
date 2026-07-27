package com.techknife.report.controller;

import com.techknife.audit.annotation.Auditable;
import com.techknife.audit.entity.AuditAction;
import com.techknife.audit.entity.AuditModule;
import com.techknife.backend.dto.ApiResponse;
import com.techknife.report.dto.ExportHistoryDTO;
import com.techknife.report.dto.ExportJobDTO;
import com.techknife.report.entity.ExportFormat;
import com.techknife.report.service.ReportExportService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/report-exports")
@RequiredArgsConstructor
@Tag(name = "Report Engine - Exports", description = "Export Engine (PDF, Excel, CSV, JSON, ZIP) API")
@SecurityRequirement(name = "bearerAuth")
public class ReportExportController {

    private final ReportExportService exportService;

    @PostMapping("/{reportId}")
    @PreAuthorize("hasAuthority('REPORT_EXPORT') or hasRole('ROLE_ADMIN')")
    @Auditable(action = AuditAction.EXPORT, module = AuditModule.REPORT, entityType = "ReportExport", description = "Report Exported")
    @Operation(summary = "Trigger Report Export")
    public ResponseEntity<ApiResponse<ExportJobDTO>> triggerExport(
            @PathVariable String reportId,
            @RequestParam ExportFormat format) {
        ExportJobDTO result = exportService.triggerExport(reportId, format);
        return ResponseEntity.ok(ApiResponse.success(result, "Triggered export job successfully"));
    }

    @GetMapping("/jobs/{jobId}")
    @PreAuthorize("hasAuthority('REPORT_VIEW') or hasRole('ROLE_ADMIN')")
    @Operation(summary = "Get Export Job Status")
    public ResponseEntity<ApiResponse<ExportJobDTO>> getExportJobStatus(@PathVariable String jobId) {
        ExportJobDTO result = exportService.getExportJobStatus(jobId);
        return ResponseEntity.ok(ApiResponse.success(result, "Fetched export job status successfully"));
    }

    @GetMapping("/jobs/report/{reportId}")
    @PreAuthorize("hasAuthority('REPORT_VIEW') or hasRole('ROLE_ADMIN')")
    @Operation(summary = "Get Export Jobs by Report ID")
    public ResponseEntity<ApiResponse<List<ExportJobDTO>>> getExportJobsByReport(@PathVariable String reportId) {
        List<ExportJobDTO> result = exportService.getExportJobsByReport(reportId);
        return ResponseEntity.ok(ApiResponse.success(result, "Fetched report export jobs successfully"));
    }

    @GetMapping("/history/report/{reportId}")
    @PreAuthorize("hasAuthority('REPORT_VIEW') or hasRole('ROLE_ADMIN')")
    @Operation(summary = "Get Export History by Report ID")
    public ResponseEntity<ApiResponse<List<ExportHistoryDTO>>> getExportHistoryByReport(@PathVariable String reportId) {
        List<ExportHistoryDTO> result = exportService.getExportHistoryByReport(reportId);
        return ResponseEntity.ok(ApiResponse.success(result, "Fetched export history successfully"));
    }

    @GetMapping("/history/user/{userId}")
    @PreAuthorize("hasAuthority('REPORT_VIEW') or hasRole('ROLE_ADMIN')")
    @Operation(summary = "Get Export History by User ID")
    public ResponseEntity<ApiResponse<List<ExportHistoryDTO>>> getExportHistoryByUser(@PathVariable String userId) {
        List<ExportHistoryDTO> result = exportService.getExportHistoryByUser(userId);
        return ResponseEntity.ok(ApiResponse.success(result, "Fetched user export history successfully"));
    }
}
