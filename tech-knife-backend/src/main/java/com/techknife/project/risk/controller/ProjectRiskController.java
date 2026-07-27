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
@RequestMapping("/api/v1/project/risks")
@RequiredArgsConstructor
@Tag(name = "Project Risk Management", description = "Endpoints for Tracking, Assessing, and Mitigating Project Risks")
@SecurityRequirement(name = "bearerAuth")
public class ProjectRiskController {

    private final ProjectRiskService projectRiskService;

    @PostMapping
    @PreAuthorize("hasAuthority('SPRINT_MANAGE') or hasRole('ROLE_ADMIN')")
    @Operation(summary = "Identify New Project Risk")
    public ResponseEntity<ApiResponse<ProjectRiskDTO>> createRisk(@RequestBody ProjectRiskDTO request) {
        ProjectRiskDTO dto = projectRiskService.createRisk(request);
        return ResponseEntity.ok(ApiResponse.success(dto, "Project risk identified successfully"));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('SPRINT_MANAGE') or hasRole('ROLE_ADMIN')")
    @Operation(summary = "Update Project Risk Status or Mitigation Plan")
    public ResponseEntity<ApiResponse<ProjectRiskDTO>> updateRisk(@PathVariable String id, @RequestBody ProjectRiskDTO request) {
        ProjectRiskDTO dto = projectRiskService.updateRisk(id, request);
        return ResponseEntity.ok(ApiResponse.success(dto, "Project risk updated successfully"));
    }

    @GetMapping("/project/{projectId}")
    @PreAuthorize("hasAuthority('SPRINT_MANAGE') or hasRole('ROLE_ADMIN')")
    @Operation(summary = "Get Project Risks by Project ID")
    public ResponseEntity<ApiResponse<List<ProjectRiskDTO>>> getRisksByProject(@PathVariable String projectId) {
        List<ProjectRiskDTO> list = projectRiskService.getRisksByProject(projectId);
        return ResponseEntity.ok(ApiResponse.success(list, "Project risks retrieved successfully"));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('SPRINT_MANAGE') or hasRole('ROLE_ADMIN')")
    @Operation(summary = "Delete Project Risk Record")
    public ResponseEntity<ApiResponse<Void>> deleteRisk(@PathVariable String id) {
        projectRiskService.deleteRisk(id);
        return ResponseEntity.ok(ApiResponse.success(null, "Project risk deleted successfully"));
    }
}
