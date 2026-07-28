package com.techknife.recruitment.controller;

import com.techknife.audit.annotation.Auditable;
import com.techknife.audit.entity.AuditAction;
import com.techknife.audit.entity.AuditModule;
import com.techknife.backend.dto.ApiResponse;
import com.techknife.recruitment.dto.OfferLetterDTO;
import com.techknife.recruitment.service.OfferLetterService;
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
@RequestMapping("/api/v1/recruitment/offers")
@RequiredArgsConstructor
@Tag(name = "Recruitment - Offer Letters", description = "Endpoints for generating, uploading, and managing offer letters")
@SecurityRequirement(name = "bearerAuth")
public class OfferLetterController {

    private final OfferLetterService offerLetterService;

    @GetMapping
    @PreAuthorize("hasAuthority('OFFER_MANAGE') or hasRole('ROLE_ADMIN')")
    @Operation(summary = "Get all offer letters")
    public ResponseEntity<ApiResponse<List<OfferLetterDTO>>> getAllOfferLetters(
            @RequestParam(required = false) String acceptanceStatus) {
        List<OfferLetterDTO> list = offerLetterService.getAllOfferLetters(acceptanceStatus);
        return ResponseEntity.ok(ApiResponse.success(list, "Fetched offer letters successfully"));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('OFFER_MANAGE') or hasRole('ROLE_ADMIN')")
    @Operation(summary = "Get offer letter details by ID")
    public ResponseEntity<ApiResponse<OfferLetterDTO>> getOfferLetterById(@PathVariable String id) {
        OfferLetterDTO dto = offerLetterService.getOfferLetterById(id);
        return ResponseEntity.ok(ApiResponse.success(dto, "Fetched offer letter details successfully"));
    }

    @GetMapping("/application/{applicationId}")
    @PreAuthorize("hasAuthority('OFFER_MANAGE') or hasRole('ROLE_ADMIN')")
    @Operation(summary = "Get offer letter details by Application ID")
    public ResponseEntity<ApiResponse<OfferLetterDTO>> getOfferLetterByApplicationId(@PathVariable String applicationId) {
        OfferLetterDTO dto = offerLetterService.getOfferLetterByApplicationId(applicationId);
        return ResponseEntity.ok(ApiResponse.success(dto, "Fetched offer letter details successfully"));
    }

    @PostMapping("/generate")
    @PreAuthorize("hasAuthority('OFFER_MANAGE') or hasRole('ROLE_ADMIN')")
    @Auditable(action = AuditAction.CREATE, module = AuditModule.RECRUITMENT, entityType = "OfferLetter", description = "Generated Job Offer Letter")
    @Operation(summary = "Generate job offer letter")
    public ResponseEntity<ApiResponse<OfferLetterDTO>> generateOfferLetter(@Valid @RequestBody OfferLetterDTO dto) {
        OfferLetterDTO result = offerLetterService.generateOfferLetter(dto);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(result, "Offer letter generated successfully"));
    }

    @PostMapping(value = "/{id}/document", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasAuthority('OFFER_MANAGE') or hasRole('ROLE_ADMIN')")
    @Auditable(action = AuditAction.UPLOAD, module = AuditModule.RECRUITMENT, entityType = "OfferLetter", description = "Uploaded Offer Letter Document")
    @Operation(summary = "Upload signed offer letter document")
    public ResponseEntity<ApiResponse<OfferLetterDTO>> uploadOfferDocument(
            @PathVariable String id,
            @RequestParam("file") MultipartFile file) {
        OfferLetterDTO result = offerLetterService.uploadOfferDocument(id, file);
        return ResponseEntity.ok(ApiResponse.success(result, "Offer document uploaded successfully"));
    }

    @PatchMapping("/{id}/respond")
    @Auditable(action = AuditAction.UPDATE, module = AuditModule.RECRUITMENT, entityType = "OfferLetter", description = "Candidate Responded to Offer")
    @Operation(summary = "Candidate response to offer (ACCEPTED or DECLINED)")
    public ResponseEntity<ApiResponse<OfferLetterDTO>> respondToOffer(
            @PathVariable String id,
            @RequestParam String responseStatus) {
        OfferLetterDTO result = offerLetterService.respondToOffer(id, responseStatus);
        return ResponseEntity.ok(ApiResponse.success(result, "Offer response processed successfully"));
    }

}
