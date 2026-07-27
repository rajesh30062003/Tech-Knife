package com.techknife.recruitment.controller;

import com.techknife.audit.annotation.Auditable;
import com.techknife.audit.entity.AuditAction;
import com.techknife.audit.entity.AuditModule;
import com.techknife.backend.dto.ApiResponse;
import com.techknife.recruitment.dto.EmployeeOnboardingDTO;
import com.techknife.recruitment.service.EmployeeOnboardingService;
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
@RequestMapping("/api/v1/recruitment/onboarding")
@RequiredArgsConstructor
@Tag(name = "Recruitment - Employee Onboarding", description = "Endpoints for pre-boarding document collection and conversion to Employee")
@SecurityRequirement(name = "bearerAuth")
public class EmployeeOnboardingController {

    private final EmployeeOnboardingService onboardingService;

    @GetMapping
    @PreAuthorize("hasAuthority('CANDIDATE_VIEW') or hasRole('ROLE_ADMIN')")
    @Operation(summary = "Get all employee onboarding records")
    public ResponseEntity<ApiResponse<List<EmployeeOnboardingDTO>>> getAllOnboardings(
            @RequestParam(required = false) String onboardingStatus) {
        List<EmployeeOnboardingDTO> list = onboardingService.getAllOnboardings(onboardingStatus);
        return ResponseEntity.ok(ApiResponse.success("Fetched onboarding records successfully", list));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('CANDIDATE_VIEW') or hasRole('ROLE_ADMIN')")
    @Operation(summary = "Get onboarding record by ID")
    public ResponseEntity<ApiResponse<EmployeeOnboardingDTO>> getOnboardingById(@PathVariable String id) {
        EmployeeOnboardingDTO dto = onboardingService.getOnboardingById(id);
        return ResponseEntity.ok(ApiResponse.success("Fetched onboarding details successfully", dto));
    }

    @PostMapping("/initiate")
    @PreAuthorize("hasAuthority('OFFER_MANAGE') or hasRole('ROLE_ADMIN')")
    @Auditable(action = AuditAction.CREATE, module = AuditModule.RECRUITMENT, entityType = "EmployeeOnboarding", description = "Initiated Onboarding Workflow")
    @Operation(summary = "Initiate onboarding workflow for a hired candidate")
    public ResponseEntity<ApiResponse<EmployeeOnboardingDTO>> initiateOnboarding(@Valid @RequestBody EmployeeOnboardingDTO dto) {
        EmployeeOnboardingDTO result = onboardingService.initiateOnboarding(dto);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Initiated onboarding workflow successfully", result));
    }

    @PostMapping(value = "/{id}/documents", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Auditable(action = AuditAction.UPLOAD, module = AuditModule.RECRUITMENT, entityType = "EmployeeOnboarding", description = "Uploaded Onboarding Document")
    @Operation(summary = "Upload candidate onboarding document")
    public ResponseEntity<ApiResponse<EmployeeOnboardingDTO>> uploadOnboardingDocument(
            @PathVariable String id,
            @RequestParam("file") MultipartFile file,
            @RequestParam(defaultValue = "ID_PROOF") String documentType) {
        EmployeeOnboardingDTO result = onboardingService.uploadOnboardingDocument(id, file, documentType);
        return ResponseEntity.ok(ApiResponse.success("Document uploaded successfully", result));
    }

    @PatchMapping("/{id}/verification")
    @PreAuthorize("hasAuthority('CANDIDATE_VIEW') or hasRole('ROLE_ADMIN')")
    @Auditable(action = AuditAction.UPDATE, module = AuditModule.RECRUITMENT, entityType = "EmployeeOnboarding", description = "Updated Document Verification Status")
    @Operation(summary = "Update document verification status (VERIFIED or REJECTED)")
    public ResponseEntity<ApiResponse<EmployeeOnboardingDTO>> updateVerificationStatus(
            @PathVariable String id,
            @RequestParam String verificationStatus) {
        EmployeeOnboardingDTO result = onboardingService.updateVerificationStatus(id, verificationStatus);
        return ResponseEntity.ok(ApiResponse.success("Verification status updated successfully", result));
    }

    @PostMapping("/{id}/convert")
    @PreAuthorize("hasAuthority('OFFER_MANAGE') or hasRole('ROLE_ADMIN')")
    @Auditable(action = AuditAction.CREATE, module = AuditModule.RECRUITMENT, entityType = "EmployeeOnboarding", description = "Converted Candidate to Employee")
    @Operation(summary = "Convert onboarded candidate into an active Employee profile")
    public ResponseEntity<ApiResponse<EmployeeOnboardingDTO>> convertToEmployee(@PathVariable String id) {
        EmployeeOnboardingDTO result = onboardingService.convertToEmployee(id);
        return ResponseEntity.ok(ApiResponse.success("Candidate converted to active Employee successfully", result));
    }
}
