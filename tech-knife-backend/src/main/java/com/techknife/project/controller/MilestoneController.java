package com.techknife.project.controller;

import com.techknife.backend.audit.Auditable;
import com.techknife.backend.dto.ApiResponse;
import com.techknife.project.dto.MilestoneDTO;
import com.techknife.project.service.MilestoneService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/projects/milestones")
@RequiredArgsConstructor
@Tag(name = "Project Milestones", description = "Endpoints for Milestone management")
@SecurityRequirement(name = "bearerAuth")
public class MilestoneController {

    private final MilestoneService milestoneService;

    @PostMapping
    @PreAuthorize("hasAuthority('PROJECT_UPDATE') or hasRole('ROLE_ADMIN')")
    @Auditable(action = "CREATE_MILESTONE", module = "PROJECT")
    @Operation(summary = "Create a Milestone")
    public ResponseEntity<ApiResponse<MilestoneDTO>> createMilestone(@Valid @RequestBody MilestoneDTO dto) {
        MilestoneDTO response = milestoneService.createMilestone(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success(response, "Milestone created successfully"));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('PROJECT_UPDATE') or hasRole('ROLE_ADMIN')")
    @Auditable(action = "UPDATE_MILESTONE", module = "PROJECT")
    @Operation(summary = "Update a Milestone")
    public ResponseEntity<ApiResponse<MilestoneDTO>> updateMilestone(@PathVariable String id, @Valid @RequestBody MilestoneDTO dto) {
        MilestoneDTO response = milestoneService.updateMilestone(id, dto);
        return ResponseEntity.ok(ApiResponse.success(response, "Milestone updated successfully"));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('PROJECT_VIEW') or hasRole('ROLE_ADMIN')")
    @Operation(summary = "Get Milestone by ID")
    public ResponseEntity<ApiResponse<MilestoneDTO>> getMilestoneById(@PathVariable String id) {
        MilestoneDTO response = milestoneService.getMilestoneById(id);
        return ResponseEntity.ok(ApiResponse.success(response, "Milestone details retrieved successfully"));
    }

    @GetMapping("/project/{projectId}")
    @PreAuthorize("hasAuthority('PROJECT_VIEW') or hasRole('ROLE_ADMIN')")
    @Operation(summary = "Get Milestones by Project ID")
    public ResponseEntity<ApiResponse<List<MilestoneDTO>>> getMilestonesByProject(@PathVariable String projectId) {
        List<MilestoneDTO> milestones = milestoneService.getMilestonesByProject(projectId);
        return ResponseEntity.ok(ApiResponse.success(milestones, "Project milestones retrieved successfully"));
    }

    @PatchMapping("/{id}/complete")
    @PreAuthorize("hasAuthority('PROJECT_UPDATE') or hasRole('ROLE_ADMIN')")
    @Auditable(action = "COMPLETE_MILESTONE", module = "PROJECT")
    @Operation(summary = "Mark Milestone as Complete")
    public ResponseEntity<ApiResponse<MilestoneDTO>> completeMilestone(@PathVariable String id) {
        MilestoneDTO response = milestoneService.completeMilestone(id);
        return ResponseEntity.ok(ApiResponse.success(response, "Milestone marked as complete"));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('PROJECT_UPDATE') or hasRole('ROLE_ADMIN')")
    @Auditable(action = "DELETE_MILESTONE", module = "PROJECT")
    @Operation(summary = "Delete Milestone")
    public ResponseEntity<ApiResponse<Void>> deleteMilestone(@PathVariable String id) {
        milestoneService.deleteMilestone(id);
        return ResponseEntity.ok(ApiResponse.success(null, "Milestone deleted successfully"));
    }
}
