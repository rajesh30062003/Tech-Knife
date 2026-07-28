package com.techknife.customerportal.controller;

import com.techknife.audit.annotation.Auditable;
import com.techknife.audit.entity.AuditAction;
import com.techknife.audit.entity.AuditModule;
import com.techknife.backend.dto.ApiResponse;
import com.techknife.customerportal.dto.CustomerMilestoneDTO;
import com.techknife.customerportal.dto.CustomerProjectDTO;
import com.techknife.customerportal.dto.CustomerTaskDTO;
import com.techknife.customerportal.service.CustomerProjectService;
import com.techknife.security.CurrentUser;
import com.techknife.security.UserPrincipal;
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
@RequestMapping("/api/v1/customer/projects")
@RequiredArgsConstructor
@Tag(name = "Customer Portal - Projects", description = "Endpoints for Customer Project, Milestone, and Task tracking")
@SecurityRequirement(name = "bearerAuth")
public class CustomerProjectController {

    private final CustomerProjectService customerProjectService;

    @GetMapping
    @PreAuthorize("hasAuthority('CUSTOMER_PROJECT_VIEW') or hasAuthority('CUSTOMER_PORTAL_ACCESS') or hasRole('ROLE_ADMIN')")
    @Operation(summary = "Get All Customer Projects")
    public ResponseEntity<ApiResponse<List<CustomerProjectDTO>>> getProjects(
            @CurrentUser UserPrincipal userPrincipal,
            @RequestParam(required = false) String status) {
        List<CustomerProjectDTO> result = customerProjectService.getProjects(userPrincipal.getId(), status);
        return ResponseEntity.ok(ApiResponse.success(result, "Fetched projects successfully"));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('CUSTOMER_PROJECT_VIEW') or hasAuthority('CUSTOMER_PORTAL_ACCESS') or hasRole('ROLE_ADMIN')")
    @Operation(summary = "Get Customer Project Details by ID")
    public ResponseEntity<ApiResponse<CustomerProjectDTO>> getProjectById(
            @CurrentUser UserPrincipal userPrincipal,
            @PathVariable String id) {
        CustomerProjectDTO result = customerProjectService.getProjectById(id, userPrincipal.getId());
        return ResponseEntity.ok(ApiResponse.success(result, "Fetched project details successfully"));
    }

    @GetMapping("/{id}/milestones")
    @PreAuthorize("hasAuthority('CUSTOMER_PROJECT_VIEW') or hasAuthority('CUSTOMER_PORTAL_ACCESS') or hasRole('ROLE_ADMIN')")
    @Operation(summary = "Get Milestones for a Project")
    public ResponseEntity<ApiResponse<List<CustomerMilestoneDTO>>> getProjectMilestones(
            @CurrentUser UserPrincipal userPrincipal,
            @PathVariable String id) {
        List<CustomerMilestoneDTO> result = customerProjectService.getProjectMilestones(id, userPrincipal.getId());
        return ResponseEntity.ok(ApiResponse.success(result, "Fetched project milestones successfully"));
    }

    @GetMapping("/{id}/tasks")
    @PreAuthorize("hasAuthority('CUSTOMER_PROJECT_VIEW') or hasAuthority('CUSTOMER_PORTAL_ACCESS') or hasRole('ROLE_ADMIN')")
    @Operation(summary = "Get Tasks for a Project")
    public ResponseEntity<ApiResponse<List<CustomerTaskDTO>>> getProjectTasks(
            @CurrentUser UserPrincipal userPrincipal,
            @PathVariable String id) {
        List<CustomerTaskDTO> result = customerProjectService.getProjectTasks(id, userPrincipal.getId());
        return ResponseEntity.ok(ApiResponse.success(result, "Fetched project tasks successfully"));
    }


    @PostMapping
    @PreAuthorize("hasRole('ROLE_ADMIN') or hasAuthority('PROJECT_CREATE')")
    @Auditable(action = AuditAction.CREATE, module = AuditModule.CUSTOMER_PORTAL, entityType = "CustomerProject", description = "Created Customer Project View")
    @Operation(summary = "Create a Customer Project (Admin/System Endpoint)")
    public ResponseEntity<ApiResponse<CustomerProjectDTO>> createProject(@Valid @RequestBody CustomerProjectDTO dto) {
        CustomerProjectDTO result = customerProjectService.createProject(dto);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(result, "Created customer project view successfully"));
    }
}
