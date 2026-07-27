package com.techknife.timetracking.controller;

import com.techknife.backend.dto.ApiResponse;
import com.techknife.timetracking.dto.TimesheetApprovalRequest;
import com.techknife.timetracking.dto.TimesheetDTO;
import com.techknife.timetracking.service.TimesheetService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/v1/timetracking/timesheets")
@RequiredArgsConstructor
@Tag(name = "Timesheets", description = "Endpoints for Daily/Weekly/Monthly Timesheet Submission and Approval Workflow")
@SecurityRequirement(name = "bearerAuth")
public class TimesheetController {

    private final TimesheetService timesheetService;

    @PostMapping("/submit")
    @PreAuthorize("hasAuthority('TIME_TRACK_CREATE') or hasRole('ROLE_ADMIN')")
    @Operation(summary = "Generate and Submit Timesheet")
    public ResponseEntity<ApiResponse<TimesheetDTO>> submitTimesheet(
            @RequestParam String employeeId,
            @RequestParam String periodType,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        TimesheetDTO dto = timesheetService.generateOrSubmitTimesheet(employeeId, periodType, startDate, endDate);
        return ResponseEntity.ok(ApiResponse.success(dto, "Timesheet submitted successfully"));
    }

    @PostMapping("/{id}/approve")
    @PreAuthorize("hasAuthority('TIME_TRACK_APPROVE') or hasRole('ROLE_ADMIN')")
    @Operation(summary = "Approve Timesheet")
    public ResponseEntity<ApiResponse<TimesheetDTO>> approveTimesheet(
            @PathVariable String id,
            @Valid @RequestBody TimesheetApprovalRequest request) {
        TimesheetDTO dto = timesheetService.approveTimesheet(id, request);
        return ResponseEntity.ok(ApiResponse.success(dto, "Timesheet approved successfully"));
    }

    @PostMapping("/{id}/reject")
    @PreAuthorize("hasAuthority('TIME_TRACK_APPROVE') or hasRole('ROLE_ADMIN')")
    @Operation(summary = "Reject Timesheet")
    public ResponseEntity<ApiResponse<TimesheetDTO>> rejectTimesheet(
            @PathVariable String id,
            @Valid @RequestBody TimesheetApprovalRequest request) {
        TimesheetDTO dto = timesheetService.rejectTimesheet(id, request);
        return ResponseEntity.ok(ApiResponse.success(dto, "Timesheet rejected successfully"));
    }

    @GetMapping("/employee/{employeeId}")
    @PreAuthorize("hasAuthority('TIME_TRACK_CREATE') or hasRole('ROLE_ADMIN')")
    @Operation(summary = "Get Timesheets by Employee")
    public ResponseEntity<ApiResponse<List<TimesheetDTO>>> getTimesheetsByEmployee(@PathVariable String employeeId) {
        List<TimesheetDTO> list = timesheetService.getTimesheetsByEmployee(employeeId);
        return ResponseEntity.ok(ApiResponse.success(list, "Employee timesheets retrieved successfully"));
    }

    @GetMapping("/pending")
    @PreAuthorize("hasAuthority('TIME_TRACK_APPROVE') or hasRole('ROLE_ADMIN')")
    @Operation(summary = "Get Pending Timesheets for Approval")
    public ResponseEntity<ApiResponse<List<TimesheetDTO>>> getPendingTimesheets() {
        List<TimesheetDTO> list = timesheetService.getPendingTimesheets();
        return ResponseEntity.ok(ApiResponse.success(list, "Pending timesheets retrieved successfully"));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('TIME_TRACK_CREATE') or hasRole('ROLE_ADMIN')")
    @Operation(summary = "Get Timesheet Details by ID")
    public ResponseEntity<ApiResponse<TimesheetDTO>> getTimesheetById(@PathVariable String id) {
        TimesheetDTO dto = timesheetService.getTimesheetById(id);
        return ResponseEntity.ok(ApiResponse.success(dto, "Timesheet details retrieved successfully"));
    }
}
