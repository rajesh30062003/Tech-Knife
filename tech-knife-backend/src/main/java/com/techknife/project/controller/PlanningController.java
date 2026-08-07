package com.techknife.project.controller;

import com.techknife.backend.dto.ApiResponse;
import com.techknife.project.entity.PlanningDocument;
import com.techknife.project.entity.PlanningVersion;
import com.techknife.project.service.PlanningService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping({"/api/v1/projects/{projectId}/planning", "/api/projects/{projectId}/planning", "/projects/{projectId}/planning"})
@RequiredArgsConstructor
@Tag(name = "Planning Workspace", description = "Endpoints for Collaborative Planning & Diagram Persistence")
@SecurityRequirement(name = "bearerAuth")
public class PlanningController {

    private final PlanningService planningService;

    @GetMapping
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Get Planning Document")
    public ResponseEntity<ApiResponse<PlanningDocument>> getPlanningDocument(@PathVariable String projectId) {
        PlanningDocument doc = planningService.getOrCreatePlanningDocument(projectId);
        return ResponseEntity.ok(ApiResponse.success(doc, "Planning document retrieved successfully"));
    }

    @PatchMapping
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Autosave / Update Planning Document & Diagram JSON")
    public ResponseEntity<ApiResponse<PlanningDocument>> savePlanningDocument(
            @PathVariable String projectId,
            @RequestBody PlanningDocument request) {
        PlanningDocument doc = planningService.saveOrAutoSavePlanningDocument(projectId, request);
        return ResponseEntity.ok(ApiResponse.success(doc, "Planning document autosaved successfully"));
    }

    @GetMapping("/versions")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Get Version Snapshots History")
    public ResponseEntity<ApiResponse<List<PlanningVersion>>> getPlanningVersions(@PathVariable String projectId) {
        List<PlanningVersion> versions = planningService.getPlanningVersions(projectId);
        return ResponseEntity.ok(ApiResponse.success(versions, "Planning version history retrieved successfully"));
    }

    @PostMapping("/versions/{versionId}/restore")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Restore Planning Document Version Snapshot")
    public ResponseEntity<ApiResponse<PlanningDocument>> restoreVersion(
            @PathVariable String projectId,
            @PathVariable String versionId) {
        PlanningDocument doc = planningService.restorePlanningVersion(projectId, versionId);
        return ResponseEntity.ok(ApiResponse.success(doc, "Planning version restored successfully"));
    }

    @PostMapping("/lock")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Lock Planning Document")
    public ResponseEntity<ApiResponse<PlanningDocument>> lockPlanning(
            @PathVariable String projectId,
            @RequestParam String userName) {
        PlanningDocument doc = planningService.lockPlanningDocument(projectId, userName);
        return ResponseEntity.ok(ApiResponse.success(doc, "Planning document locked successfully"));
    }

    @PostMapping("/unlock")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Unlock Planning Document")
    public ResponseEntity<ApiResponse<PlanningDocument>> unlockPlanning(@PathVariable String projectId) {
        PlanningDocument doc = planningService.unlockPlanningDocument(projectId);
        return ResponseEntity.ok(ApiResponse.success(doc, "Planning document unlocked successfully"));
    }
}
