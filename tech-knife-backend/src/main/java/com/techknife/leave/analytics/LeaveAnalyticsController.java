package com.techknife.leave.analytics;

import com.techknife.backend.audit.Auditable;
import com.techknife.backend.dto.ApiResponse;
import com.techknife.leave.dto.LeaveAnalyticsDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/leaves/analytics")
@RequiredArgsConstructor
@Tag(name = "Leave Analytics", description = "Endpoints for Leave Consumption, Department Trends & Type Distribution")
@SecurityRequirement(name = "bearerAuth")
public class LeaveAnalyticsController {

    private final LeaveAnalyticsService analyticsService;

    @GetMapping
    @PreAuthorize("hasAuthority('LEAVE_ANALYTICS_VIEW') or hasRole('ROLE_ADMIN')")
    @Auditable(action = "VIEW_LEAVE_ANALYTICS", module = "LEAVE")
    @Operation(summary = "Get Leave Analytics & Trends")
    public ResponseEntity<ApiResponse<LeaveAnalyticsDTO>> getAnalytics(@RequestParam(required = false) Integer year) {
        LeaveAnalyticsDTO result = analyticsService.getLeaveAnalytics(year);
        return ResponseEntity.ok(ApiResponse.success(result, "Leave analytics retrieved successfully"));
    }
}
