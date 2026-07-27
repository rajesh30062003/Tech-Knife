package com.techknife.attendance.dashboard;

import com.techknife.backend.audit.Auditable;
import com.techknife.backend.dto.ApiResponse;
import com.techknife.attendance.dto.AttendanceDashboardDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@RestController
@RequestMapping("/api/v1/attendance/dashboard")
@RequiredArgsConstructor
@Tag(name = "Attendance Dashboard", description = "Endpoints for Attendance Dashboard Stats & Realtime Monitoring")
@SecurityRequirement(name = "bearerAuth")
public class AttendanceDashboardController {

    private final AttendanceDashboardService dashboardService;

    @GetMapping
    @PreAuthorize("hasAuthority('ATTENDANCE_VIEW') or hasAuthority('ATTENDANCE_REPORT_VIEW') or hasRole('ROLE_ADMIN')")
    @Auditable(action = "VIEW_ATTENDANCE_DASHBOARD", module = "ATTENDANCE")
    @Operation(summary = "Get Today's Attendance Dashboard Metrics")
    public ResponseEntity<ApiResponse<AttendanceDashboardDTO>> getDashboard(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        AttendanceDashboardDTO result = dashboardService.getTodayDashboard(date);
        return ResponseEntity.ok(ApiResponse.success(result, "Attendance dashboard data retrieved successfully"));
    }
}
