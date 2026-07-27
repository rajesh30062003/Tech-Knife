package com.techknife.timetracking.controller;

import com.techknife.backend.dto.ApiResponse;
import com.techknife.timetracking.dto.StartTimerRequest;
import com.techknife.timetracking.dto.TimeEntryDTO;
import com.techknife.timetracking.service.TimeTrackingService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/timetracking/entries")
@RequiredArgsConstructor
@Tag(name = "Time Tracking", description = "Endpoints for Timer Start, Pause, Resume, Stop and Time Log Management")
@SecurityRequirement(name = "bearerAuth")
public class TimeEntryController {

    private final TimeTrackingService timeTrackingService;

    @PostMapping("/timer/start")
    @PreAuthorize("hasAuthority('TIME_TRACK_CREATE') or hasRole('ROLE_ADMIN')")
    @Operation(summary = "Start Timer")
    public ResponseEntity<ApiResponse<TimeEntryDTO>> startTimer(@Valid @RequestBody StartTimerRequest request) {
        TimeEntryDTO dto = timeTrackingService.startTimer(request);
        return ResponseEntity.ok(ApiResponse.success(dto, "Timer started successfully"));
    }

    @PostMapping("/timer/{id}/pause")
    @PreAuthorize("hasAuthority('TIME_TRACK_CREATE') or hasRole('ROLE_ADMIN')")
    @Operation(summary = "Pause Timer")
    public ResponseEntity<ApiResponse<TimeEntryDTO>> pauseTimer(@PathVariable String id) {
        TimeEntryDTO dto = timeTrackingService.pauseTimer(id);
        return ResponseEntity.ok(ApiResponse.success(dto, "Timer paused successfully"));
    }

    @PostMapping("/timer/{id}/resume")
    @PreAuthorize("hasAuthority('TIME_TRACK_CREATE') or hasRole('ROLE_ADMIN')")
    @Operation(summary = "Resume Timer")
    public ResponseEntity<ApiResponse<TimeEntryDTO>> resumeTimer(@PathVariable String id) {
        TimeEntryDTO dto = timeTrackingService.resumeTimer(id);
        return ResponseEntity.ok(ApiResponse.success(dto, "Timer resumed successfully"));
    }

    @PostMapping("/timer/{id}/stop")
    @PreAuthorize("hasAuthority('TIME_TRACK_CREATE') or hasRole('ROLE_ADMIN')")
    @Operation(summary = "Stop Timer")
    public ResponseEntity<ApiResponse<TimeEntryDTO>> stopTimer(@PathVariable String id) {
        TimeEntryDTO dto = timeTrackingService.stopTimer(id);
        return ResponseEntity.ok(ApiResponse.success(dto, "Timer stopped successfully"));
    }

    @PostMapping("/manual")
    @PreAuthorize("hasAuthority('TIME_TRACK_CREATE') or hasRole('ROLE_ADMIN')")
    @Operation(summary = "Add Manual Time Entry")
    public ResponseEntity<ApiResponse<TimeEntryDTO>> addManualTimeEntry(@RequestBody TimeEntryDTO request) {
        TimeEntryDTO dto = timeTrackingService.addManualTimeEntry(request);
        return ResponseEntity.ok(ApiResponse.success(dto, "Manual time entry added successfully"));
    }

    @GetMapping("/active")
    @PreAuthorize("hasAuthority('TIME_TRACK_CREATE') or hasRole('ROLE_ADMIN')")
    @Operation(summary = "Get Active Running Timer for Employee")
    public ResponseEntity<ApiResponse<TimeEntryDTO>> getActiveTimer(@RequestParam String employeeId) {
        TimeEntryDTO dto = timeTrackingService.getActiveTimer(employeeId);
        return ResponseEntity.ok(ApiResponse.success(dto, "Active timer retrieved successfully"));
    }

    @GetMapping("/employee/{employeeId}")
    @PreAuthorize("hasAuthority('TIME_TRACK_CREATE') or hasRole('ROLE_ADMIN')")
    @Operation(summary = "Get Time Entries by Employee")
    public ResponseEntity<ApiResponse<List<TimeEntryDTO>>> getTimeEntriesByEmployee(@PathVariable String employeeId) {
        List<TimeEntryDTO> list = timeTrackingService.getTimeEntriesByEmployee(employeeId);
        return ResponseEntity.ok(ApiResponse.success(list, "Time entries retrieved successfully"));
    }

    @GetMapping("/project/{projectId}")
    @PreAuthorize("hasAuthority('TIME_TRACK_CREATE') or hasRole('ROLE_ADMIN')")
    @Operation(summary = "Get Time Entries by Project")
    public ResponseEntity<ApiResponse<List<TimeEntryDTO>>> getTimeEntriesByProject(@PathVariable String projectId) {
        List<TimeEntryDTO> list = timeTrackingService.getTimeEntriesByProject(projectId);
        return ResponseEntity.ok(ApiResponse.success(list, "Project time entries retrieved successfully"));
    }
}
