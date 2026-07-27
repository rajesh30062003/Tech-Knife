package com.techknife.analytics.controller;

import com.techknife.analytics.dto.ExecutiveScorecardDTO;
import com.techknife.analytics.entity.ExecutiveRole;
import com.techknife.analytics.service.ExecutiveScorecardService;
import com.techknife.audit.annotation.Auditable;
import com.techknife.audit.entity.AuditAction;
import com.techknife.audit.entity.AuditModule;
import com.techknife.backend.dto.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/analytics/executive-scorecards")
@RequiredArgsConstructor
@Tag(name = "Analytics - Executive Scorecards", description = "Role-based Executive Scorecards API")
@SecurityRequirement(name = "bearerAuth")
public class ExecutiveScorecardController {

    private final ExecutiveScorecardService scorecardService;

    @GetMapping
    @PreAuthorize("hasAnyAuthority('EXECUTIVE_DASHBOARD_VIEW', 'ANALYTICS_VIEW') or hasRole('ROLE_ADMIN')")
    @Operation(summary = "Get All Executive Scorecards")
    public ResponseEntity<ApiResponse<List<ExecutiveScorecardDTO>>> getAllScorecards() {
        List<ExecutiveScorecardDTO> list = scorecardService.getAllScorecards();
        return ResponseEntity.ok(ApiResponse.success(list, "Scorecards retrieved successfully"));
    }

    @GetMapping("/role/{role}")
    @PreAuthorize("hasAnyAuthority('EXECUTIVE_DASHBOARD_VIEW', 'ANALYTICS_VIEW') or hasRole('ROLE_ADMIN')")
    @Auditable(action = AuditAction.EXPORT, module = AuditModule.ANALYTICS, entityType = "ExecutiveScorecard", description = "Executive Scorecard View")
    @Operation(summary = "Get Executive Scorecard by Role & Period")
    public ResponseEntity<ApiResponse<ExecutiveScorecardDTO>> getScorecardByRole(
            @PathVariable ExecutiveRole role,
            @RequestParam(required = false, defaultValue = "Q3 2026") String period) {
        ExecutiveScorecardDTO scorecard = scorecardService.getScorecardByRoleAndPeriod(role, period);
        return ResponseEntity.ok(ApiResponse.success(scorecard, "Scorecard retrieved successfully"));
    }

    @PostMapping("/generate")
    @PreAuthorize("hasAuthority('ANALYTICS_MANAGE') or hasRole('ROLE_ADMIN')")
    @Auditable(action = AuditAction.CREATE, module = AuditModule.ANALYTICS, entityType = "ExecutiveScorecard", description = "Executive Scorecard Generated")
    @Operation(summary = "Generate Executive Scorecard")
    public ResponseEntity<ApiResponse<ExecutiveScorecardDTO>> generateScorecard(
            @RequestParam ExecutiveRole role,
            @RequestParam(required = false, defaultValue = "Q3 2026") String period) {
        ExecutiveScorecardDTO scorecard = scorecardService.generateExecutiveScorecard(role, period);
        return ResponseEntity.ok(ApiResponse.success(scorecard, "Scorecard generated successfully"));
    }
}
