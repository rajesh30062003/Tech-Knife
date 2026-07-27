package com.techknife.report.controller;

import com.techknife.backend.dto.ApiResponse;
import com.techknife.report.dto.ReportExecutionDTO;
import com.techknife.report.entity.ExecutionStatus;
import com.techknife.report.entity.ExportFormat;
import com.techknife.report.service.ReportExecutionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/report-executions")
@RequiredArgsConstructor
@Tag(name = "Report Engine - Background Processing", description = "Report Queue & Long Running Jobs API")
@SecurityRequirement(name = "bearerAuth")
public class ReportExecutionController {

    private final ReportExecutionService executionService;

    @PostMapping("/queue/{reportId}")
    @PreAuthorize("hasAuthority('REPORT_VIEW') or hasRole('ROLE_ADMIN')")
    @Operation(summary = "Queue Long-running Report Generation")
    public ResponseEntity<ApiResponse<ReportExecutionDTO>> queueExecution(
            @PathVariable String reportId,
            @RequestParam(required = false) String scheduleId,
            @RequestParam(required = false, defaultValue = "PDF") ExportFormat format) {
        ReportExecutionDTO result = executionService.queueExecution(reportId, scheduleId, format);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(result, "Queued report execution successfully"));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('REPORT_VIEW') or hasRole('ROLE_ADMIN')")
    @Operation(summary = "Get Execution Status & Progress Tracking")
    public ResponseEntity<ApiResponse<ReportExecutionDTO>> getExecutionStatus(@PathVariable String id) {
        ReportExecutionDTO result = executionService.getExecutionStatus(id);
        return ResponseEntity.ok(ApiResponse.success(result, "Fetched execution status successfully"));
    }

    @GetMapping("/report/{reportId}")
    @PreAuthorize("hasAuthority('REPORT_VIEW') or hasRole('ROLE_ADMIN')")
    @Operation(summary = "Get Executions by Report ID")
    public ResponseEntity<ApiResponse<List<ReportExecutionDTO>>> getExecutionsByReport(@PathVariable String reportId) {
        List<ReportExecutionDTO> result = executionService.getExecutionsByReport(reportId);
        return ResponseEntity.ok(ApiResponse.success(result, "Fetched report executions successfully"));
    }

    @GetMapping("/status/{status}")
    @PreAuthorize("hasAuthority('REPORT_VIEW') or hasRole('ROLE_ADMIN')")
    @Operation(summary = "Get Executions by Status")
    public ResponseEntity<ApiResponse<List<ReportExecutionDTO>>> getExecutionsByStatus(@PathVariable ExecutionStatus status) {
        List<ReportExecutionDTO> result = executionService.getExecutionsByStatus(status);
        return ResponseEntity.ok(ApiResponse.success(result, "Fetched executions by status successfully"));
    }

    @PostMapping("/{id}/retry")
    @PreAuthorize("hasAuthority('REPORT_CREATE') or hasRole('ROLE_ADMIN')")
    @Operation(summary = "Retry Failed Report Job")
    public ResponseEntity<ApiResponse<ReportExecutionDTO>> retryFailedJob(@PathVariable String id) {
        ReportExecutionDTO result = executionService.retryFailedJob(id);
        return ResponseEntity.ok(ApiResponse.success(result, "Retrying failed report job"));
    }

    @PutMapping("/{id}/cancel")
    @PreAuthorize("hasAuthority('REPORT_CREATE') or hasRole('ROLE_ADMIN')")
    @Operation(summary = "Cancel Report Execution")
    public ResponseEntity<ApiResponse<Void>> cancelExecution(@PathVariable String id) {
        executionService.cancelExecution(id);
        return ResponseEntity.ok(ApiResponse.success(null, "Cancelled report execution"));
    }
}
