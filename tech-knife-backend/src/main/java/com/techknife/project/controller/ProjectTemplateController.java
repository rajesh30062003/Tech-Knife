package com.techknife.project.controller;

import com.techknife.backend.audit.Auditable;
import com.techknife.backend.dto.ApiResponse;
import com.techknife.project.dto.ProjectRequestDTO;
import com.techknife.project.dto.ProjectResponseDTO;
import com.techknife.project.dto.ProjectTemplateDTO;
import com.techknife.project.service.ProjectTemplateService;
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
@RequestMapping("/api/v1/projects/templates")
@RequiredArgsConstructor
@Tag(name = "Project Templates", description = "Endpoints for Reusable Project Templates and Cloning")
@SecurityRequirement(name = "bearerAuth")
public class ProjectTemplateController {

    private final ProjectTemplateService templateService;

    @PostMapping
    @PreAuthorize("hasAuthority('PROJECT_CREATE') or hasRole('ROLE_ADMIN')")
    @Auditable(action = "CREATE_PROJECT_TEMPLATE", module = "PROJECT")
    @Operation(summary = "Create Project Template")
    public ResponseEntity<ApiResponse<ProjectTemplateDTO>> createTemplate(@Valid @RequestBody ProjectTemplateDTO dto) {
        ProjectTemplateDTO response = templateService.createTemplate(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success(response, "Project template created successfully"));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('PROJECT_UPDATE') or hasRole('ROLE_ADMIN')")
    @Auditable(action = "UPDATE_PROJECT_TEMPLATE", module = "PROJECT")
    @Operation(summary = "Update Project Template")
    public ResponseEntity<ApiResponse<ProjectTemplateDTO>> updateTemplate(
            @PathVariable String id,
            @Valid @RequestBody ProjectTemplateDTO dto) {
        ProjectTemplateDTO response = templateService.updateTemplate(id, dto);
        return ResponseEntity.ok(ApiResponse.success(response, "Project template updated successfully"));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('PROJECT_VIEW') or hasRole('ROLE_ADMIN')")
    @Operation(summary = "Get Template by ID")
    public ResponseEntity<ApiResponse<ProjectTemplateDTO>> getTemplateById(@PathVariable String id) {
        ProjectTemplateDTO response = templateService.getTemplateById(id);
        return ResponseEntity.ok(ApiResponse.success(response, "Template details retrieved successfully"));
    }

    @GetMapping
    @PreAuthorize("hasAuthority('PROJECT_VIEW') or hasRole('ROLE_ADMIN')")
    @Operation(summary = "List All Project Templates")
    public ResponseEntity<ApiResponse<List<ProjectTemplateDTO>>> getAllTemplates() {
        List<ProjectTemplateDTO> templates = templateService.getAllTemplates();
        return ResponseEntity.ok(ApiResponse.success(templates, "Project templates retrieved successfully"));
    }

    @PostMapping("/{id}/instantiate")
    @PreAuthorize("hasAuthority('PROJECT_CREATE') or hasRole('ROLE_ADMIN')")
    @Auditable(action = "CLONE_PROJECT_FROM_TEMPLATE", module = "PROJECT")
    @Operation(summary = "Clone Template into new Project")
    public ResponseEntity<ApiResponse<ProjectResponseDTO>> instantiateProjectFromTemplate(
            @PathVariable String id,
            @Valid @RequestBody ProjectRequestDTO request) {
        ProjectResponseDTO response = templateService.instantiateProjectFromTemplate(id, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success(response, "Project created from template successfully"));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('PROJECT_DELETE') or hasRole('ROLE_ADMIN')")
    @Auditable(action = "DELETE_PROJECT_TEMPLATE", module = "PROJECT")
    @Operation(summary = "Delete Project Template")
    public ResponseEntity<ApiResponse<Void>> deleteTemplate(@PathVariable String id) {
        templateService.deleteTemplate(id);
        return ResponseEntity.ok(ApiResponse.success(null, "Project template deleted successfully"));
    }
}
