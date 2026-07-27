package com.techknife.report.controller;

import com.techknife.audit.annotation.Auditable;
import com.techknife.audit.entity.AuditAction;
import com.techknife.audit.entity.AuditModule;
import com.techknife.backend.dto.ApiResponse;
import com.techknife.report.dto.ReportScheduleDTO;
import com.techknife.report.service.ReportScheduleService;
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
@RequestMapping("/api/v1/report-schedules")
@RequiredArgsConstructor
@Tag(name = "Report Engine - Schedules", description = "Report Scheduling API")
@SecurityRequirement(name = "bearerAuth")
public class ReportScheduleController {

    private final ReportScheduleService scheduleService;

    @PostMapping
    @PreAuthorize("hasAuthority('REPORT_SCHEDULE') or hasRole('ROLE_ADMIN')")
    @Auditable(action = AuditAction.CREATE, module = AuditModule.REPORT, entityType = "ReportSchedule", description = "Created Schedule")
    @Operation(summary = "Create Report Schedule")
    public ResponseEntity<ApiResponse<ReportScheduleDTO>> createSchedule(@Valid @RequestBody ReportScheduleDTO dto) {
        ReportScheduleDTO result = scheduleService.createSchedule(dto);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(result, "Created report schedule successfully"));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('REPORT_SCHEDULE') or hasRole('ROLE_ADMIN')")
    @Auditable(action = AuditAction.UPDATE, module = AuditModule.REPORT, entityType = "ReportSchedule", description = "Schedule Updated")
    @Operation(summary = "Update Report Schedule")
    public ResponseEntity<ApiResponse<ReportScheduleDTO>> updateSchedule(
            @PathVariable String id,
            @Valid @RequestBody ReportScheduleDTO dto) {
        ReportScheduleDTO result = scheduleService.updateSchedule(id, dto);
        return ResponseEntity.ok(ApiResponse.success(result, "Schedule Updated"));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('REPORT_VIEW') or hasRole('ROLE_ADMIN')")
    @Operation(summary = "Get Schedule by ID")
    public ResponseEntity<ApiResponse<ReportScheduleDTO>> getScheduleById(@PathVariable String id) {
        ReportScheduleDTO result = scheduleService.getScheduleById(id);
        return ResponseEntity.ok(ApiResponse.success(result, "Fetched schedule successfully"));
    }

    @GetMapping("/report/{reportId}")
    @PreAuthorize("hasAuthority('REPORT_VIEW') or hasRole('ROLE_ADMIN')")
    @Operation(summary = "Get Schedules by Report ID")
    public ResponseEntity<ApiResponse<List<ReportScheduleDTO>>> getSchedulesByReportId(@PathVariable String reportId) {
        List<ReportScheduleDTO> result = scheduleService.getSchedulesByReportId(reportId);
        return ResponseEntity.ok(ApiResponse.success(result, "Fetched report schedules successfully"));
    }

    @GetMapping("/active")
    @PreAuthorize("hasAuthority('REPORT_VIEW') or hasRole('ROLE_ADMIN')")
    @Operation(summary = "Get All Active Schedules")
    public ResponseEntity<ApiResponse<List<ReportScheduleDTO>>> getAllActiveSchedules() {
        List<ReportScheduleDTO> result = scheduleService.getAllActiveSchedules();
        return ResponseEntity.ok(ApiResponse.success(result, "Fetched active schedules successfully"));
    }

    @GetMapping("/search")
    @PreAuthorize("hasAuthority('REPORT_VIEW') or hasRole('ROLE_ADMIN')")
    @Operation(summary = "Search Schedules")
    public ResponseEntity<ApiResponse<List<ReportScheduleDTO>>> searchSchedules(@RequestParam(required = false) String query) {
        List<ReportScheduleDTO> result = scheduleService.searchSchedules(query);
        return ResponseEntity.ok(ApiResponse.success(result, "Searched schedules successfully"));
    }

    @PutMapping("/{id}/status")
    @PreAuthorize("hasAuthority('REPORT_SCHEDULE') or hasRole('ROLE_ADMIN')")
    @Auditable(action = AuditAction.UPDATE, module = AuditModule.REPORT, entityType = "ReportSchedule", description = "Schedule Updated")
    @Operation(summary = "Toggle Schedule Status")
    public ResponseEntity<ApiResponse<Void>> toggleScheduleStatus(
            @PathVariable String id,
            @RequestParam boolean active) {
        scheduleService.toggleScheduleStatus(id, active);
        return ResponseEntity.ok(ApiResponse.success(null, "Updated schedule active status"));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('REPORT_SCHEDULE') or hasRole('ROLE_ADMIN')")
    @Auditable(action = AuditAction.DELETE, module = AuditModule.REPORT, entityType = "ReportSchedule", description = "Deleted Schedule")
    @Operation(summary = "Delete Schedule")
    public ResponseEntity<ApiResponse<Void>> deleteSchedule(@PathVariable String id) {
        scheduleService.deleteSchedule(id);
        return ResponseEntity.ok(ApiResponse.success(null, "Deleted schedule successfully"));
    }
}
