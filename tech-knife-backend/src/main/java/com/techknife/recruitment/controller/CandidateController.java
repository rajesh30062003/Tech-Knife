package com.techknife.recruitment.controller;

import com.techknife.audit.annotation.Auditable;
import com.techknife.audit.entity.AuditAction;
import com.techknife.audit.entity.AuditModule;
import com.techknife.backend.dto.ApiResponse;
import com.techknife.recruitment.dto.CandidateDTO;
import com.techknife.recruitment.service.CandidateService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/v1/recruitment/candidates")
@RequiredArgsConstructor
@Tag(name = "Recruitment - Candidates", description = "Endpoints for managing candidate profiles")
@SecurityRequirement(name = "bearerAuth")
public class CandidateController {

    private final CandidateService candidateService;

    @GetMapping
    @PreAuthorize("hasAuthority('CANDIDATE_VIEW') or hasRole('ROLE_ADMIN')")
    @Operation(summary = "Get all candidates")
    public ResponseEntity<ApiResponse<List<CandidateDTO>>> getAllCandidates(
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String skill) {
        List<CandidateDTO> result = candidateService.getAllCandidates(status, skill);
        return ResponseEntity.ok(ApiResponse.success(result, "Fetched candidates successfully"));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('CANDIDATE_VIEW') or hasRole('ROLE_ADMIN')")
    @Operation(summary = "Get candidate by ID")
    public ResponseEntity<ApiResponse<CandidateDTO>> getCandidateById(@PathVariable String id) {
        CandidateDTO result = candidateService.getCandidateById(id);
        return ResponseEntity.ok(ApiResponse.success(result, "Fetched candidate successfully"));
    }

    @PostMapping
    @Auditable(action = AuditAction.CREATE, module = AuditModule.RECRUITMENT, entityType = "Candidate", description = "Created Candidate Profile")
    @Operation(summary = "Register a candidate profile")
    public ResponseEntity<ApiResponse<CandidateDTO>> createCandidate(@Valid @RequestBody CandidateDTO dto) {
        CandidateDTO result = candidateService.createCandidate(dto);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(result, "Candidate created successfully"));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('CANDIDATE_VIEW') or hasRole('ROLE_ADMIN')")
    @Auditable(action = AuditAction.UPDATE, module = AuditModule.RECRUITMENT, entityType = "Candidate", description = "Updated Candidate Profile")
    @Operation(summary = "Update candidate profile")
    public ResponseEntity<ApiResponse<CandidateDTO>> updateCandidate(
            @PathVariable String id,
            @Valid @RequestBody CandidateDTO dto) {
        CandidateDTO result = candidateService.updateCandidate(id, dto);
        return ResponseEntity.ok(ApiResponse.success(result, "Candidate updated successfully"));
    }

    @PostMapping(value = "/{id}/resume", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Auditable(action = AuditAction.UPLOAD, module = AuditModule.RECRUITMENT, entityType = "Candidate", description = "Uploaded Candidate Resume")
    @Operation(summary = "Upload candidate resume file to Cloudinary")
    public ResponseEntity<ApiResponse<CandidateDTO>> uploadResume(
            @PathVariable String id,
            @RequestParam("file") MultipartFile file) {
        CandidateDTO result = candidateService.uploadResume(id, file);
        return ResponseEntity.ok(ApiResponse.success(result, "Resume uploaded successfully"));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('CANDIDATE_VIEW') or hasRole('ROLE_ADMIN')")
    @Auditable(action = AuditAction.DELETE, module = AuditModule.RECRUITMENT, entityType = "Candidate", description = "Deleted Candidate Profile")
    @Operation(summary = "Delete candidate profile")
    public ResponseEntity<ApiResponse<Void>> deleteCandidate(@PathVariable String id) {
        candidateService.deleteCandidate(id);
        return ResponseEntity.ok(ApiResponse.success("Candidate deleted successfully"));
    }

}
