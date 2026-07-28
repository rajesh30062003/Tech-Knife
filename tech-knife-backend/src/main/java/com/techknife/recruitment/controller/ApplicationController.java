package com.techknife.recruitment.controller;

import com.techknife.audit.annotation.Auditable;
import com.techknife.audit.entity.AuditAction;
import com.techknife.audit.entity.AuditModule;
import com.techknife.backend.dto.ApiResponse;
import com.techknife.recruitment.dto.ApplicationDTO;
import com.techknife.recruitment.service.ApplicationService;
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
@RequestMapping("/api/v1/recruitment/applications")
@RequiredArgsConstructor
@Tag(name = "Recruitment - Job Applications", description = "Endpoints for managing candidate job applications")
@SecurityRequirement(name = "bearerAuth")
public class ApplicationController {

    private final ApplicationService applicationService;

    @GetMapping
    @PreAuthorize("hasAuthority('CANDIDATE_VIEW') or hasRole('ROLE_ADMIN')")
    @Operation(summary = "Get all applications")
    public ResponseEntity<ApiResponse<List<ApplicationDTO>>> getAllApplications(
            @RequestParam(required = false) String jobPostingId,
            @RequestParam(required = false) String candidateId,
            @RequestParam(required = false) String status) {
        List<ApplicationDTO> result = applicationService.getAllApplications(jobPostingId, candidateId, status);
        return ResponseEntity.ok(ApiResponse.success(result, "Fetched applications successfully"));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('CANDIDATE_VIEW') or hasRole('ROLE_ADMIN')")
    @Operation(summary = "Get application by ID")
    public ResponseEntity<ApiResponse<ApplicationDTO>> getApplicationById(@PathVariable String id) {
        ApplicationDTO result = applicationService.getApplicationById(id);
        return ResponseEntity.ok(ApiResponse.success(result, "Fetched application successfully"));
    }

    @PostMapping
    @Auditable(action = AuditAction.CREATE, module = AuditModule.RECRUITMENT, entityType = "Application", description = "Submitted Job Application")
    @Operation(summary = "Submit application for a job posting")
    public ResponseEntity<ApiResponse<ApplicationDTO>> applyForJob(@Valid @RequestBody ApplicationDTO dto) {
        ApplicationDTO result = applicationService.applyForJob(dto);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(result, "Application submitted successfully"));
    }

    @PatchMapping("/{id}/status")
    @PreAuthorize("hasAuthority('CANDIDATE_VIEW') or hasRole('ROLE_ADMIN')")
    @Auditable(action = AuditAction.UPDATE, module = AuditModule.RECRUITMENT, entityType = "Application", description = "Updated Application Status")
    @Operation(summary = "Update application status")
    public ResponseEntity<ApiResponse<ApplicationDTO>> updateApplicationStatus(
            @PathVariable String id,
            @RequestParam String status,
            @RequestParam(required = false) String notes) {
        ApplicationDTO result = applicationService.updateApplicationStatus(id, status, notes);
        return ResponseEntity.ok(ApiResponse.success(result, "Application status updated successfully"));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('CANDIDATE_VIEW') or hasRole('ROLE_ADMIN')")
    @Auditable(action = AuditAction.DELETE, module = AuditModule.RECRUITMENT, entityType = "Application", description = "Deleted Application")
    @Operation(summary = "Delete application")
    public ResponseEntity<ApiResponse<Void>> deleteApplication(@PathVariable String id) {
        applicationService.deleteApplication(id);
        return ResponseEntity.ok(ApiResponse.success("Application deleted successfully"));
    }

}
