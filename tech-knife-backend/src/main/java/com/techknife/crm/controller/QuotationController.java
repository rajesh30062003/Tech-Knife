package com.techknife.crm.controller;

import com.techknife.audit.annotation.Auditable;
import com.techknife.audit.entity.AuditAction;
import com.techknife.audit.entity.AuditModule;
import com.techknife.backend.dto.ApiResponse;
import com.techknife.crm.dto.QuotationDTO;
import com.techknife.crm.service.QuotationService;
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
@RequestMapping("/api/v1/crm/quotations")
@RequiredArgsConstructor
@Tag(name = "CRM - Quotation Management", description = "Endpoints for managing CRM Quotations")
@SecurityRequirement(name = "bearerAuth")
public class QuotationController {

    private final QuotationService quotationService;

    @GetMapping
    @PreAuthorize("hasAuthority('QUOTATION_VIEW') or hasAuthority('CUSTOMER_VIEW') or hasRole('ROLE_ADMIN')")
    @Operation(summary = "Get all quotations")
    public ResponseEntity<ApiResponse<List<QuotationDTO>>> getAllQuotations(@RequestParam(required = false) String status) {
        List<QuotationDTO> result = quotationService.getAllQuotations(status);
        return ResponseEntity.ok(ApiResponse.success(result, "Fetched quotations successfully"));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('QUOTATION_VIEW') or hasAuthority('CUSTOMER_VIEW') or hasRole('ROLE_ADMIN')")
    @Operation(summary = "Get quotation by ID")
    public ResponseEntity<ApiResponse<QuotationDTO>> getQuotationById(@PathVariable String id) {
        QuotationDTO result = quotationService.getQuotationById(id);
        return ResponseEntity.ok(ApiResponse.success(result, "Fetched quotation successfully"));
    }

    @PostMapping
    @PreAuthorize("hasAuthority('QUOTATION_CREATE') or hasRole('ROLE_ADMIN')")
    @Auditable(action = AuditAction.CREATE, module = AuditModule.CRM, entityType = "Quotation", description = "Created CRM Quotation")
    @Operation(summary = "Create a new quotation")
    public ResponseEntity<ApiResponse<QuotationDTO>> createQuotation(@Valid @RequestBody QuotationDTO dto) {
        QuotationDTO result = quotationService.createQuotation(dto);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(result, "Created quotation successfully"));
    }

    @PostMapping(value = "/{id}/document", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasAuthority('QUOTATION_CREATE') or hasRole('ROLE_ADMIN')")
    @Auditable(action = AuditAction.UPLOAD, module = AuditModule.CRM, entityType = "Quotation", description = "Uploaded Quotation Document")
    @Operation(summary = "Upload quotation document to Cloudinary")
    public ResponseEntity<ApiResponse<QuotationDTO>> uploadQuotationDocument(
            @PathVariable String id,
            @RequestParam("file") MultipartFile file) {
        QuotationDTO result = quotationService.uploadQuotationDocument(id, file);
        return ResponseEntity.ok(ApiResponse.success(result, "Uploaded quotation document successfully"));
    }

    @PatchMapping("/{id}/approval")
    @PreAuthorize("hasAuthority('QUOTATION_APPROVE') or hasRole('ROLE_ADMIN')")
    @Auditable(action = AuditAction.APPROVE, module = AuditModule.CRM, entityType = "Quotation", description = "Updated Quotation Approval Status")
    @Operation(summary = "Update quotation approval status")
    public ResponseEntity<ApiResponse<QuotationDTO>> updateApprovalStatus(
            @PathVariable String id,
            @RequestParam String status,
            @RequestParam(required = false) String approvedBy) {
        QuotationDTO result = quotationService.updateApprovalStatus(id, status, approvedBy);
        return ResponseEntity.ok(ApiResponse.success(result, "Updated quotation approval status successfully"));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('QUOTATION_DELETE') or hasRole('ROLE_ADMIN')")
    @Auditable(action = AuditAction.DELETE, module = AuditModule.CRM, entityType = "Quotation", description = "Deleted CRM Quotation")
    @Operation(summary = "Delete quotation")
    public ResponseEntity<ApiResponse<Void>> deleteQuotation(@PathVariable String id) {
        quotationService.deleteQuotation(id);
        return ResponseEntity.ok(ApiResponse.success("Deleted quotation successfully"));
    }

}
