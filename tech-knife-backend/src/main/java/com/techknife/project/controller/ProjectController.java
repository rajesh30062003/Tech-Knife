package com.techknife.project.controller;

import com.techknife.backend.audit.Auditable;
import com.techknife.backend.dto.ApiResponse;
import com.techknife.project.dto.*;
import com.techknife.project.entity.ProjectActivity;
import com.techknife.project.entity.ProjectStatus;
import com.techknife.project.entity.ProjectStatusHistory;
import com.techknife.project.service.ProjectService;
import com.techknife.security.UserPrincipal;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Slf4j
@RestController
@RequestMapping({"/api/v1/projects", "/api/projects"})
@RequiredArgsConstructor
@Tag(name = "Projects", description = "Endpoints for Enterprise Project Management & Governance")
@SecurityRequirement(name = "bearerAuth")
public class ProjectController {

    private final ProjectService projectService;

    @PostMapping
    @PreAuthorize("hasAnyAuthority('ROLE_SUPER_ADMIN','SUPER_ADMIN','ROLE_CEO','CEO','ROLE_MD','MD','ROLE_CTO','CTO','ROLE_CMO','CMO','PROJECT_CREATE')")
    @Auditable(action = "CREATE_PROJECT", module = "PROJECT")
    @Operation(summary = "Create a new Enterprise Project")
    public ResponseEntity<ApiResponse<ProjectResponseDTO>> createProject(
            @Valid @RequestBody ProjectRequestDTO request,
            Authentication authentication) {
        logAuthorizationDebug("POST /api/v1/projects", authentication);
        String currentUser = authentication != null ? authentication.getName() : "SYSTEM";
        String currentRole = getPrimaryRole(authentication);
        ProjectResponseDTO response = projectService.createProject(request, currentUser, currentRole);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success(response, "Project created successfully"));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('ROLE_SUPER_ADMIN','SUPER_ADMIN','ROLE_CEO','CEO','ROLE_MD','MD','ROLE_CTO','CTO','ROLE_CMO','CMO','ROLE_MANAGER','MANAGER','ROLE_PROJECT_LEAD','PROJECT_LEAD','PROJECT_UPDATE')")
    @Auditable(action = "UPDATE_PROJECT", module = "PROJECT")
    @Operation(summary = "Update an existing Enterprise Project")
    public ResponseEntity<ApiResponse<ProjectResponseDTO>> updateProject(
            @PathVariable String id,
            @Valid @RequestBody ProjectRequestDTO request,
            Authentication authentication) {
        logAuthorizationDebug("PUT /api/v1/projects/" + id, authentication);
        String currentUser = authentication != null ? authentication.getName() : "SYSTEM";
        String currentRole = getPrimaryRole(authentication);
        ProjectResponseDTO response = projectService.updateProject(id, request, currentUser, currentRole);
        return ResponseEntity.ok(ApiResponse.success(response, "Project updated successfully"));
    }

    @RequestMapping(value = "/{id}/status", method = {org.springframework.web.bind.annotation.RequestMethod.PUT, org.springframework.web.bind.annotation.RequestMethod.PATCH})
    @PreAuthorize("hasAnyAuthority('ROLE_SUPER_ADMIN','SUPER_ADMIN','ROLE_CEO','CEO','ROLE_MD','MD','ROLE_CTO','CTO','ROLE_CMO','CMO','ROLE_MANAGER','MANAGER','ROLE_PROJECT_LEAD','PROJECT_LEAD','PROJECT_STATUS_UPDATE')")
    @Auditable(action = "UPDATE_PROJECT_STATUS", module = "PROJECT")
    @Operation(summary = "Update Project Status with audit reason")
    public ResponseEntity<ApiResponse<ProjectResponseDTO>> updateStatus(
            @PathVariable String id,
            @Valid @RequestBody ProjectStatusUpdateDTO updateDTO,
            Authentication authentication) {
        logAuthorizationDebug("PUT/PATCH /api/v1/projects/" + id + "/status", authentication);
        String currentUser = authentication != null ? authentication.getName() : "SYSTEM";
        String currentRole = getPrimaryRole(authentication);
        ProjectResponseDTO response = projectService.updateStatus(id, updateDTO, currentUser, currentRole);
        return ResponseEntity.ok(ApiResponse.success(response, "Project status updated successfully"));
    }

    @RequestMapping(value = "/{id}/progress", method = {org.springframework.web.bind.annotation.RequestMethod.PUT, org.springframework.web.bind.annotation.RequestMethod.PATCH})
    @PreAuthorize("hasAnyAuthority('ROLE_SUPER_ADMIN','SUPER_ADMIN','ROLE_CEO','CEO','ROLE_MD','MD','ROLE_CTO','CTO','ROLE_CMO','CMO','ROLE_MANAGER','MANAGER','ROLE_PROJECT_LEAD','PROJECT_LEAD','PROJECT_UPDATE')")
    @Auditable(action = "UPDATE_PROJECT_PROGRESS", module = "PROJECT")
    @Operation(summary = "Update Project Progress Percentage")
    public ResponseEntity<ApiResponse<ProjectResponseDTO>> updateProgress(
            @PathVariable String id,
            @RequestParam("progress") Double progress,
            Authentication authentication) {
        logAuthorizationDebug("PUT/PATCH /api/v1/projects/" + id + "/progress", authentication);
        String currentUser = authentication != null ? authentication.getName() : "SYSTEM";
        String currentRole = getPrimaryRole(authentication);
        ProjectStatusUpdateDTO dto = ProjectStatusUpdateDTO.builder()
                .progressPercentage(progress)
                .reason("Progress updated")
                .build();
        ProjectResponseDTO response = projectService.updateStatus(id, dto, currentUser, currentRole);
        return ResponseEntity.ok(ApiResponse.success(response, "Project progress updated successfully"));
    }

    @GetMapping("/{id}/history")
    @Operation(summary = "Get Project Audit History")
    public ResponseEntity<ApiResponse<List<com.techknife.project.entity.ProjectStatusHistory>>> getProjectHistory(@PathVariable String id) {
        List<com.techknife.project.entity.ProjectStatusHistory> history = projectService.getStatusHistory(id);
        return ResponseEntity.ok(ApiResponse.success(history, "Project history retrieved successfully"));
    }

    @PutMapping({"/{id}/assign", "/{id}/members"})
    @PreAuthorize("hasAnyAuthority('ROLE_SUPER_ADMIN','SUPER_ADMIN','ROLE_CEO','CEO','ROLE_MD','MD','ROLE_CTO','CTO','ROLE_CMO','CMO','ROLE_MANAGER','MANAGER','ROLE_PROJECT_LEAD','PROJECT_LEAD','PROJECT_ASSIGN')")
    @Auditable(action = "ASSIGN_PROJECT_MEMBERS", module = "PROJECT")
    @Operation(summary = "Assign Project Manager, Lead, Employees, and Interns")
    public ResponseEntity<ApiResponse<ProjectResponseDTO>> assignMembers(
            @PathVariable String id,
            @RequestBody ProjectAssignDTO assignDTO,
            Authentication authentication) {
        logAuthorizationDebug("PUT /api/v1/projects/" + id + "/assign-or-members", authentication);
        String currentUser = authentication != null ? authentication.getName() : "SYSTEM";
        String currentRole = getPrimaryRole(authentication);
        ProjectResponseDTO response = projectService.assignMembers(id, assignDTO, currentUser, currentRole);
        return ResponseEntity.ok(ApiResponse.success(response, "Project members assigned successfully"));
    }

    @PutMapping("/{id}/links")
    @PreAuthorize("hasAnyAuthority('ROLE_SUPER_ADMIN','SUPER_ADMIN','ROLE_CEO','CEO','ROLE_MD','MD','ROLE_CTO','CTO','ROLE_CMO','CMO','ROLE_MANAGER','MANAGER','ROLE_PROJECT_LEAD','PROJECT_LEAD','PROJECT_LINK_UPDATE')")
    @Auditable(action = "UPDATE_PROJECT_LINKS", module = "PROJECT")
    @Operation(summary = "Update Project Repositories, Deployments, and Resource URLs")
    public ResponseEntity<ApiResponse<ProjectResponseDTO>> updateLinks(
            @PathVariable String id,
            @RequestBody ProjectLinksUpdateDTO linksDTO,
            Authentication authentication) {
        logAuthorizationDebug("PUT /api/v1/projects/" + id + "/links", authentication);
        String currentUser = authentication != null ? authentication.getName() : "SYSTEM";
        String currentRole = getPrimaryRole(authentication);
        ProjectResponseDTO response = projectService.updateLinks(id, linksDTO, currentUser, currentRole);
        return ResponseEntity.ok(ApiResponse.success(response, "Project links updated successfully"));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get Project by ID")
    public ResponseEntity<ApiResponse<ProjectResponseDTO>> getProjectById(@PathVariable String id) {
        ProjectResponseDTO response = projectService.getProjectById(id);
        return ResponseEntity.ok(ApiResponse.success(response, "Project details retrieved successfully"));
    }

    @GetMapping("/code/{code}")
    @Operation(summary = "Get Project by Code")
    public ResponseEntity<ApiResponse<ProjectResponseDTO>> getProjectByCode(@PathVariable String code) {
        ProjectResponseDTO response = projectService.getProjectByCode(code);
        return ResponseEntity.ok(ApiResponse.success(response, "Project details retrieved successfully"));
    }

    @GetMapping
    @Operation(summary = "List Projects filtered by JWT Role & Assignment")
    public ResponseEntity<ApiResponse<List<ProjectResponseDTO>>> getAllProjects(
            @AuthenticationPrincipal UserPrincipal principal,
            @RequestParam(required = false) ProjectStatus status,
            @RequestParam(required = false) String category) {
        List<ProjectResponseDTO> projects = projectService.getAllProjects(principal, status, category);
        return ResponseEntity.ok(ApiResponse.success(projects, "Projects retrieved successfully"));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('ROLE_SUPER_ADMIN','SUPER_ADMIN','ROLE_CEO','CEO','ROLE_MD','MD','PROJECT_DELETE')")
    @Auditable(action = "DELETE_PROJECT", module = "PROJECT")
    @Operation(summary = "Delete Project")
    public ResponseEntity<ApiResponse<Void>> deleteProject(
            @PathVariable String id,
            Authentication authentication) {
        logAuthorizationDebug("DELETE /api/v1/projects/" + id, authentication);
        String currentUser = authentication != null ? authentication.getName() : "SYSTEM";
        String currentRole = getPrimaryRole(authentication);
        projectService.deleteProject(id, currentUser, currentRole);
        return ResponseEntity.ok(ApiResponse.success(null, "Project deleted successfully"));
    }

    @PostMapping("/{id}/members")
    @PreAuthorize("hasAnyAuthority('ROLE_SUPER_ADMIN','SUPER_ADMIN','ROLE_CEO','CEO','ROLE_MD','MD','ROLE_CTO','CTO','ROLE_CMO','CMO','ROLE_MANAGER','MANAGER','ROLE_PROJECT_LEAD','PROJECT_LEAD','PROJECT_UPDATE')")
    @Auditable(action = "ADD_PROJECT_MEMBER", module = "PROJECT")
    @Operation(summary = "Add Project Member")
    public ResponseEntity<ApiResponse<ProjectResponseDTO>> addMember(
            @PathVariable String id,
            @Valid @RequestBody ProjectMemberDTO memberDTO) {
        ProjectResponseDTO response = projectService.addMember(id, memberDTO);
        return ResponseEntity.ok(ApiResponse.success(response, "Project member added successfully"));
    }

    @DeleteMapping("/{id}/members/{employeeId}")
    @PreAuthorize("hasAnyAuthority('ROLE_SUPER_ADMIN','SUPER_ADMIN','ROLE_CEO','CEO','ROLE_MD','MD','ROLE_CTO','CTO','ROLE_CMO','CMO','ROLE_MANAGER','MANAGER','ROLE_PROJECT_LEAD','PROJECT_LEAD','PROJECT_UPDATE')")
    @Auditable(action = "REMOVE_PROJECT_MEMBER", module = "PROJECT")
    @Operation(summary = "Remove Project Member")
    public ResponseEntity<ApiResponse<ProjectResponseDTO>> removeMember(
            @PathVariable String id,
            @PathVariable String employeeId) {
        ProjectResponseDTO response = projectService.removeMember(id, employeeId);
        return ResponseEntity.ok(ApiResponse.success(response, "Project member removed successfully"));
    }

    @PostMapping("/{id}/teams")
    @PreAuthorize("hasAnyAuthority('ROLE_SUPER_ADMIN','SUPER_ADMIN','ROLE_CEO','CEO','ROLE_MD','MD','ROLE_CTO','CTO','ROLE_CMO','CMO','ROLE_MANAGER','MANAGER','ROLE_PROJECT_LEAD','PROJECT_LEAD','PROJECT_UPDATE')")
    @Auditable(action = "ADD_PROJECT_TEAM", module = "PROJECT")
    @Operation(summary = "Add Project Team")
    public ResponseEntity<ApiResponse<ProjectResponseDTO>> addTeam(
            @PathVariable String id,
            @Valid @RequestBody ProjectTeamDTO teamDTO) {
        ProjectResponseDTO response = projectService.addTeam(id, teamDTO);
        return ResponseEntity.ok(ApiResponse.success(response, "Project team added successfully"));
    }

    @DeleteMapping("/{id}/teams/{teamId}")
    @PreAuthorize("hasAnyAuthority('ROLE_SUPER_ADMIN','SUPER_ADMIN','ROLE_CEO','CEO','ROLE_MD','MD','ROLE_CTO','CTO','ROLE_CMO','CMO','ROLE_MANAGER','MANAGER','ROLE_PROJECT_LEAD','PROJECT_LEAD','PROJECT_UPDATE')")
    @Auditable(action = "REMOVE_PROJECT_TEAM", module = "PROJECT")
    @Operation(summary = "Remove Project Team")
    public ResponseEntity<ApiResponse<ProjectResponseDTO>> removeTeam(
            @PathVariable String id,
            @PathVariable String teamId) {
        ProjectResponseDTO response = projectService.removeTeam(id, teamId);
        return ResponseEntity.ok(ApiResponse.success(response, "Project team removed successfully"));
    }

    @PostMapping(value = "/{id}/documents", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasAnyAuthority('ROLE_SUPER_ADMIN','SUPER_ADMIN','ROLE_CEO','CEO','ROLE_MD','MD','ROLE_CTO','CTO','ROLE_CMO','CMO','ROLE_MANAGER','MANAGER','ROLE_PROJECT_LEAD','PROJECT_LEAD','PROJECT_UPDATE')")
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
    @Operation(summary = "Get Project Status Change History")
    public ResponseEntity<ApiResponse<List<ProjectStatusHistory>>> getStatusHistory(@PathVariable String id) {
        List<ProjectStatusHistory> history = projectService.getStatusHistory(id);
        return ResponseEntity.ok(ApiResponse.success(history, "Project status history retrieved successfully"));
    }

    @GetMapping("/{id}/activities")
    @Operation(summary = "Get Project Audit Trail Activities")
    public ResponseEntity<ApiResponse<List<ProjectActivity>>> getActivities(@PathVariable String id) {
        List<ProjectActivity> activities = projectService.getActivities(id);
        return ResponseEntity.ok(ApiResponse.success(activities, "Project activities retrieved successfully"));
    }

    private void logAuthorizationDebug(String endpoint, Authentication authentication) {
        if (authentication == null) {
            log.warn("AUTHORIZATION DEBUG: Endpoint '{}' called with NULL Authentication context", endpoint);
            return;
        }
        log.info("AUTHORIZATION DEBUG: Endpoint '{}' accessed by User='{}', Authorities={}",
                endpoint, authentication.getName(), authentication.getAuthorities());
    }

    private String getPrimaryRole(Authentication authentication) {
        if (authentication == null || authentication.getAuthorities() == null) return "ROLE_EMPLOYEE";
        return authentication.getAuthorities().stream()
                .map(a -> a.getAuthority())
                .filter(a -> a.startsWith("ROLE_"))
                .findFirst()
                .orElse("ROLE_EMPLOYEE");
    }
}
