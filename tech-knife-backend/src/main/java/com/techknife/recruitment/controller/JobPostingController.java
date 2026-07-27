package com.techknife.recruitment.controller;

import com.techknife.audit.annotation.Auditable;
import com.techknife.audit.entity.AuditAction;
import com.techknife.audit.entity.AuditModule;
import com.techknife.backend.dto.ApiResponse;
import com.techknife.recruitment.dto.JobPostingDTO;
import com.techknife.recruitment.service.JobPostingService;
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
@RequestMapping("/api/v1/recruitment/jobs")
@RequiredArgsConstructor
@Tag(name = "Recruitment - Job Postings", description = "Endpoints for managing Job Postings")
@SecurityRequirement(name = "bearerAuth")
public class JobPostingController {

    private final JobPostingService jobPostingService;

    @GetMapping
    @Operation(summary = "Get all job postings")
    public ResponseEntity<ApiResponse<List<JobPostingDTO>>> getAllJobPostings(
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String department) {
        List<JobPostingDTO> result = jobPostingService.getAllJobPostings(status, department);
        return ResponseEntity.ok(ApiResponse.success("Fetched job postings successfully", result));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get job posting by ID")
    public ResponseEntity<ApiResponse<JobPostingDTO>> getJobPostingById(@PathVariable String id) {
        JobPostingDTO result = jobPostingService.getJobPostingById(id);
        return ResponseEntity.ok(ApiResponse.success("Fetched job posting successfully", result));
    }

    @PostMapping
    @PreAuthorize("hasAuthority('JOB_CREATE') or hasRole('ROLE_ADMIN')")
    @Auditable(action = AuditAction.CREATE, module = AuditModule.RECRUITMENT, entityType = "JobPosting", description = "Created Job Posting")
    @Operation(summary = "Create a new job posting")
    public ResponseEntity<ApiResponse<JobPostingDTO>> createJobPosting(@Valid @RequestBody JobPostingDTO dto) {
        JobPostingDTO result = jobPostingService.createJobPosting(dto);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Created job posting successfully", result));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('JOB_UPDATE') or hasRole('ROLE_ADMIN')")
    @Auditable(action = AuditAction.UPDATE, module = AuditModule.RECRUITMENT, entityType = "JobPosting", description = "Updated Job Posting")
    @Operation(summary = "Update an existing job posting")
    public ResponseEntity<ApiResponse<JobPostingDTO>> updateJobPosting(
            @PathVariable String id,
            @Valid @RequestBody JobPostingDTO dto) {
        JobPostingDTO result = jobPostingService.updateJobPosting(id, dto);
        return ResponseEntity.ok(ApiResponse.success("Updated job posting successfully", result));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('JOB_UPDATE') or hasRole('ROLE_ADMIN')")
    @Auditable(action = AuditAction.DELETE, module = AuditModule.RECRUITMENT, entityType = "JobPosting", description = "Deleted Job Posting")
    @Operation(summary = "Delete job posting")
    public ResponseEntity<ApiResponse<Void>> deleteJobPosting(@PathVariable String id) {
        jobPostingService.deleteJobPosting(id);
        return ResponseEntity.ok(ApiResponse.success("Deleted job posting successfully", null));
    }
}
