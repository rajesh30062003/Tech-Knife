package com.techknife.project.report.controller;

import com.techknife.backend.dto.ApiResponse;
import com.techknife.project.report.dto.ProjectReportDTO;
import com.techknife.project.report.service.ProjectReportService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/project/reports")
@RequiredArgsConstructor
@Tag(name = "Executive Reports", description = "Endpoints for Status, Sprint, Timesheet, Task, Risk, Resource, and Completion Forecast Reports & CSV Export")
@SecurityRequirement(name = "bearerAuth")
public class ProjectReportController {

    private final ProjectReportService projectReportService;

    @GetMapping("/generate")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Generate Executive Project Report")
    public ResponseEntity<ApiResponse<ProjectReportDTO>> generateReport(
            @RequestParam String reportType,
            @RequestParam String projectId,
            @RequestParam(required = false, defaultValue = "SYSTEM") String userId) {
        ProjectReportDTO dto = projectReportService.generateReport(reportType, projectId, userId);
        return ResponseEntity.ok(ApiResponse.success(dto, "Report generated successfully"));
    }

    @GetMapping("/export/csv")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Export Report as CSV File")
    public ResponseEntity<String> exportReportCsv(
            @RequestParam String reportType,
            @RequestParam String projectId,
            @RequestParam(required = false, defaultValue = "SYSTEM") String userId) {
        ProjectReportDTO dto = projectReportService.generateReport(reportType, projectId, userId);
        String csv = projectReportService.exportReportToCsv(dto);

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + reportType.toLowerCase() + "_report.csv")
                .contentType(MediaType.parseMediaType("text/csv"))
                .body(csv);
    }
}
