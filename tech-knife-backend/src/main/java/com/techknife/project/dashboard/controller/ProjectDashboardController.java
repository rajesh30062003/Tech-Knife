package com.techknife.project.dashboard.controller;

import com.techknife.backend.dto.ApiResponse;
import com.techknife.project.dashboard.dto.ProjectDashboardSummaryDTO;
import com.techknife.project.dashboard.service.ProjectDashboardService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/project/dashboard")
@RequiredArgsConstructor
@Tag(name = "Project Executive Dashboard", description = "Endpoints for Unified Project Health, Budget Usage, Progress, Milestones, GitHub Activity, and Sprints")
@SecurityRequirement(name = "bearerAuth")
public class ProjectDashboardController {

    private final ProjectDashboardService projectDashboardService;

    @GetMapping("/project/{projectId}")
    @PreAuthorize("hasAuthority('PROJECT_ANALYTICS_VIEW') or hasRole('ROLE_ADMIN')")
    @Operation(summary = "Get Consolidated Project Dashboard Summary")
    public ResponseEntity<ApiResponse<ProjectDashboardSummaryDTO>> getProjectDashboardSummary(@PathVariable String projectId) {
        ProjectDashboardSummaryDTO dto = projectDashboardService.getProjectDashboardSummary(projectId);
        return ResponseEntity.ok(ApiResponse.success(dto, "Project dashboard summary retrieved successfully"));
    }
}
