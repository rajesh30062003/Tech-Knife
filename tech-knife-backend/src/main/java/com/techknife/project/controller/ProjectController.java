package com.techknife.project.controller;

import com.techknife.backend.audit.Auditable;
import com.techknife.backend.dto.ApiResponse;
import com.techknife.project.dto.*;
import com.techknife.project.entity.ProjectStatus;
import com.techknife.project.entity.ProjectStatusHistory;
import com.techknife.project.service.ProjectService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/v1/projects")
@RequiredArgsConstructor
@Tag(name = "Projects", description = "Endpoints for Project Management Foundation")
@SecurityRequirement(name = "bearerAuth")
public class ProjectController {

    private final ProjectService projectService;

    @PostMapping
    @PreAuthorize("hasAuthority('PROJECT_CREATE') or hasRole('ROLE_ADMIN')")
    @Auditable(action = "CREATE_PROJECT", module = "PROJECT")
    @Operation(summary = "Create a new Project")
    public ResponseEntity<ApiResponse<ProjectResponseDTO>> createProject(@Valid @RequestBody ProjectRequestDTO request) {
        ProjectResponseDTO response = projectService.createProject(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success(response, "Project created successfully"));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('PROJECT_UPDATE') or hasRole('ROLE_ADMIN')")
    @Auditable(action = "UPDATE_PROJECT", module = "PROJECT")
    @Operation(summary = "Update an existing Project")
    public ResponseEntity<ApiResponse<ProjectResponseDTO>> updateProject(
            @PathVariable String id,
            @Valid @RequestBody ProjectRequestDTO request) {
        ProjectResponseDTO response = projectService.updateProject(id, request);
        return ResponseEntity.ok(ApiResponse.success(response, "Project updated successfully"));
    }

    @PatchMapping("/{id}/status")
    @PreAuthorize("hasAuthority('PROJECT_UPDATE') or hasRole('ROLE_ADMIN')")
    @Auditable(action = "UPDATE_PROJECT_STATUS", module = "PROJECT")
    @Operation(summary = "Update Project Status with reason logging")
    public ResponseEntity<ApiResponse<ProjectResponseDTO>> updateStatus(
            @PathVariable String id,
            @Valid @RequestBody ProjectStatusUpdateDTO updateDTO,
            Authentication authentication) {
        String user = authentication != null ? authentication.getName() : "SYSTEM";
        ProjectResponseDTO response = projectService.updateStatus(id, updateDTO, user);
        return ResponseEntity.ok(ApiResponse.success(response, "Project status updated successfully"));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('PROJECT_VIEW') or hasRole('ROLE_ADMIN')")
    @Operation(summary = "Get Project by ID")
    public ResponseEntity<ApiResponse<ProjectResponseDTO>> getProjectById(@PathVariable String id) {
        ProjectResponseDTO response = projectService.getProjectById(id);
        return ResponseEntity.ok(ApiResponse.success(response, "Project details retrieved successfully"));
    }

    @GetMapping("/code/{code}")
    @PreAuthorize("hasAuthority('PROJECT_VIEW') or hasRole('ROLE_ADMIN')")
    @Operation(summary = "Get Project by Code")
    public ResponseEntity<ApiResponse<ProjectResponseDTO>> getProjectByCode(@PathVariable String code) {
        ProjectResponseDTO response = projectService.getProjectByCode(code);
        return ResponseEntity.ok(ApiResponse.success(response, "Project details retrieved successfully"));
    }

    @GetMapping
    @PreAuthorize("hasAuthority('PROJECT_VIEW') or hasRole('ROLE_ADMIN')")
    @Operation(summary = "List / Search Projects")
    public ResponseEntity<ApiResponse<List<ProjectResponseDTO>>> getAllProjects(
            @RequestParam(required = false) ProjectStatus status,
            @RequestParam(required = false) String managerId,
            @RequestParam(required = false) String employeeId) {
        List<ProjectResponseDTO> projects = projectService.getAllProjects(status, managerId, employeeId);
        return ResponseEntity.ok(ApiResponse.success(projects, "Projects retrieved successfully"));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('PROJECT_DELETE') or hasRole('ROLE_ADMIN')")
    @Auditable(action = "DELETE_PROJECT", module = "PROJECT")
    @Operation(summary = "Delete Project")
    public ResponseEntity<ApiResponse<Void>> deleteProject(@PathVariable String id) {
        projectService.deleteProject(id);
        return ResponseEntity.ok(ApiResponse.success(null, "Project deleted successfully"));
    }

    @PostMapping("/{id}/members")
    @PreAuthorize("hasAuthority('PROJECT_UPDATE') or hasRole('ROLE_ADMIN')")
    @Auditable(action = "ADD_PROJECT_MEMBER", module = "PROJECT")
    @Operation(summary = "Add Project Member")
    public ResponseEntity<ApiResponse<ProjectResponseDTO>> addMember(
            @PathVariable String id,
            @Valid @RequestBody ProjectMemberDTO memberDTO) {
        ProjectResponseDTO response = projectService.addMember(id, memberDTO);
        return ResponseEntity.ok(ApiResponse.success(response, "Project member added successfully"));
    }

    @DeleteMapping("/{id}/members/{employeeId}")
    @PreAuthorize("hasAuthority('PROJECT_UPDATE') or hasRole('ROLE_ADMIN')")
    @Auditable(action = "REMOVE_PROJECT_MEMBER", module = "PROJECT")
    @Operation(summary = "Remove Project Member")
    public ResponseEntity<ApiResponse<ProjectResponseDTO>> removeMember(
            @PathVariable String id,
            @PathVariable String employeeId) {
        ProjectResponseDTO response = projectService.removeMember(id, employeeId);
        return ResponseEntity.ok(ApiResponse.success(response, "Project member removed successfully"));
    }

    @PostMapping("/{id}/teams")
    @PreAuthorize("hasAuthority('PROJECT_UPDATE') or hasRole('ROLE_ADMIN')")
    @Auditable(action = "ADD_PROJECT_TEAM", module = "PROJECT")
    @Operation(summary = "Add Project Team")
    public ResponseEntity<ApiResponse<ProjectResponseDTO>> addTeam(
            @PathVariable String id,
            @Valid @RequestBody ProjectTeamDTO teamDTO) {
        ProjectResponseDTO response = projectService.addTeam(id, teamDTO);
        return ResponseEntity.ok(ApiResponse.success(response, "Project team added successfully"));
    }

    @DeleteMapping("/{id}/teams/{teamId}")
    @PreAuthorize("hasAuthority('PROJECT_UPDATE') or hasRole('ROLE_ADMIN')")
    @Auditable(action = "REMOVE_PROJECT_TEAM", module = "PROJECT")
    @Operation(summary = "Remove Project Team")
    public ResponseEntity<ApiResponse<ProjectResponseDTO>> removeTeam(
            @PathVariable String id,
            @PathVariable String teamId) {
        ProjectResponseDTO response = projectService.removeTeam(id, teamId);
        return ResponseEntity.ok(ApiResponse.success(response, "Project team removed successfully"));
    }

    @PostMapping(value = "/{id}/documents", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasAuthority('PROJECT_UPDATE') or hasRole('ROLE_ADMIN')")
    @Auditable(action = "UPLOAD_PROJECT_DOCUMENT", module = "PROJECT")
    @Operation(summary = "Upload Project Document")
    public ResponseEntity<ApiResponse<ProjectResponseDTO>> uploadDocument(
            @PathVariable String id,
            @RequestParam("file") MultipartFile file,
            Authentication authentication) {
        String uploader = authentication != null ? authentication.getName() : "SYSTEM";
        ProjectResponseDTO response = projectService.uploadDocument(id, file, uploader);
        return ResponseEntity.ok(ApiResponse.success(response, "Project document uploaded successfully"));
    }

    @GetMapping("/{id}/status-history")
    @PreAuthorize("hasAuthority('PROJECT_VIEW') or hasRole('ROLE_ADMIN')")
    @Operation(summary = "Get Project Status Change History")
    public ResponseEntity<ApiResponse<List<ProjectStatusHistory>>> getStatusHistory(@PathVariable String id) {
        List<ProjectStatusHistory> history = projectService.getStatusHistory(id);
        return ResponseEntity.ok(ApiResponse.success(history, "Project status history retrieved successfully"));
    }
}
