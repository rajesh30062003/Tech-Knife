package com.techknife.widgets;

import com.techknife.backend.audit.Auditable;
import com.techknife.backend.dto.ApiResponse;
import com.techknife.widgets.dto.HrWidgetsDTO;
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
@RequestMapping("/api/v1/hr/widgets")
@RequiredArgsConstructor
@Tag(name = "HR Widgets", description = "Endpoints for HR Overview Widgets (Heatmap, Leave Calendar, Late Employees, Pending Approvals, Expiring Comp-Off, Shift Summary)")
@SecurityRequirement(name = "bearerAuth")
public class HrWidgetsController {

    private final HrWidgetsService hrWidgetsService;

    @GetMapping
    @PreAuthorize("hasAuthority('ATTENDANCE_VIEW') or hasAuthority('LEAVE_VIEW') or hasRole('ROLE_ADMIN')")
    @Auditable(action = "VIEW_HR_WIDGETS", module = "HR_WIDGETS")
    @Operation(summary = "Get HR Dashboard Widgets Overview")
    public ResponseEntity<ApiResponse<HrWidgetsDTO>> getWidgetsData() {
        HrWidgetsDTO data = hrWidgetsService.getHrWidgetsData();
        return ResponseEntity.ok(ApiResponse.success(data, "HR widgets data retrieved successfully"));
    }
}
