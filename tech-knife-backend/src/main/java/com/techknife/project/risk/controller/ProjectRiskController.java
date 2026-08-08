package com.techknife.project.risk.controller;

import com.techknife.backend.dto.ApiResponse;
import com.techknife.project.risk.dto.ProjectRiskDTO;
import com.techknife.project.risk.service.ProjectRiskService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping
@RequiredArgsConstructor
@Tag(name = "Project Risk Management", description = "Endpoints for Tracking, Assessing, and Mitigating Project Risks")
@SecurityRequirement(name = "bearerAuth")
public class ProjectRiskController {

    private final ProjectRiskService projectRiskService;

    @PostMapping({"/api/v1/project/risks", "/api/v1/projects/{projectId}/risks", "/api/projects/{projectId}/risks"})
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Identify New Project Risk")
    public ResponseEntity<ApiResponse<ProjectRiskDTO>> createRisk(
            @PathVariable(required = false) String projectId,
            @RequestBody ProjectRiskDTO request) {
        if (projectId != null && (request.getProjectId() == null || request.getProjectId().isBlank())) {
            request.setProjectId(projectId);
        }
        ProjectRiskDTO dto = projectRiskService.createRisk(request);
        return ResponseEntity.ok(ApiResponse.success(dto, "Project risk identified successfully"));
    }

    @PutMapping({"/api/v1/project/risks/{id}", "/api/v1/projects/risks/{id}", "/api/v1/projects/{projectId}/risks/{id}"})
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Update Project Risk Status or Mitigation Plan")
    public ResponseEntity<ApiResponse<ProjectRiskDTO>> updateRisk(
            @PathVariable(required = false) String projectId,
            @PathVariable(required = false) String id,
            @RequestBody ProjectRiskDTO request) {
        String targetId = id != null ? id : projectId;
        ProjectRiskDTO dto = projectRiskService.updateRisk(targetId, request);
        return ResponseEntity.ok(ApiResponse.success(dto, "Project risk updated successfully"));
    }

    @GetMapping({"/api/v1/projects/{projectId}/risks", "/api/v1/project/risks/project/{projectId}", "/api/v1/project/risks/{projectId}"})
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Get Project Risks by Project ID")
    public ResponseEntity<ApiResponse<List<ProjectRiskDTO>>> getRisksByProject(@PathVariable String projectId) {
        List<ProjectRiskDTO> list = projectRiskService.getRisksByProject(projectId);
        return ResponseEntity.ok(ApiResponse.success(list, "Project risks retrieved successfully"));
    }

    @DeleteMapping({"/api/v1/project/risks/{id}", "/api/v1/projects/risks/{id}"})
    @PreAuthorize("hasAuthority('SPRINT_MANAGE') or hasRole('ROLE_ADMIN')")
    @Operation(summary = "Delete Project Risk Record")
    public ResponseEntity<ApiResponse<Void>> deleteRisk(@PathVariable String id) {
        projectRiskService.deleteRisk(id);
        return ResponseEntity.ok(ApiResponse.success(null, "Project risk deleted successfully"));
    }
}
