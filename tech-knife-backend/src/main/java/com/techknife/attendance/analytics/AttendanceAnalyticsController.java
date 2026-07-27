package com.techknife.attendance.analytics;

import com.techknife.backend.audit.Auditable;
import com.techknife.backend.dto.ApiResponse;
import com.techknife.attendance.dto.AttendanceAnalyticsDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/attendance/analytics")
@RequiredArgsConstructor
@Tag(name = "Attendance Analytics", description = "Endpoints for Attendance Trends, Working Hours & Shift Utilization")
@SecurityRequirement(name = "bearerAuth")
public class AttendanceAnalyticsController {

    private final AttendanceAnalyticsService analyticsService;

    @GetMapping
    @PreAuthorize("hasAuthority('ATTENDANCE_ANALYTICS_VIEW') or hasRole('ROLE_ADMIN')")
    @Auditable(action = "VIEW_ATTENDANCE_ANALYTICS", module = "ATTENDANCE")
    @Operation(summary = "Get Attendance Analytics & Trends")
    public ResponseEntity<ApiResponse<AttendanceAnalyticsDTO>> getAnalytics(
            @RequestParam(required = false) Integer year,
            @RequestParam(required = false) Integer month) {
        AttendanceAnalyticsDTO result = analyticsService.getAttendanceAnalytics(year, month);
        return ResponseEntity.ok(ApiResponse.success(result, "Attendance analytics retrieved successfully"));
    }
}
