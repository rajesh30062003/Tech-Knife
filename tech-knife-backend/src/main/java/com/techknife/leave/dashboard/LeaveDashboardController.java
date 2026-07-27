package com.techknife.leave.dashboard;

import com.techknife.backend.audit.Auditable;
import com.techknife.backend.dto.ApiResponse;
import com.techknife.leave.dto.LeaveDashboardDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/leaves/dashboard")
@RequiredArgsConstructor
@Tag(name = "Leave Dashboard", description = "Endpoints for Leave Dashboard Metrics & Approval Pipelines")
@SecurityRequirement(name = "bearerAuth")
public class LeaveDashboardController {

    private final LeaveDashboardService leaveDashboardService;

    @GetMapping
    @PreAuthorize("hasAuthority('LEAVE_VIEW') or hasAuthority('LEAVE_REPORT_VIEW') or hasRole('ROLE_ADMIN')")
    @Auditable(action = "VIEW_LEAVE_DASHBOARD", module = "LEAVE")
    @Operation(summary = "Get Leave Dashboard Metrics")
    public ResponseEntity<ApiResponse<LeaveDashboardDTO>> getDashboard() {
        LeaveDashboardDTO result = leaveDashboardService.getLeaveDashboard();
        return ResponseEntity.ok(ApiResponse.success(result, "Leave dashboard data retrieved successfully"));
    }
}
