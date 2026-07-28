package com.techknife.crm.controller;

import com.techknife.audit.annotation.Auditable;
import com.techknife.audit.entity.AuditAction;
import com.techknife.audit.entity.AuditModule;
import com.techknife.backend.dto.ApiResponse;
import com.techknife.crm.dto.ProposalDTO;
import com.techknife.crm.service.ProposalService;
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
@RequestMapping("/api/v1/crm/proposals")
@RequiredArgsConstructor
@Tag(name = "CRM - Proposal Management", description = "Endpoints for managing CRM Proposals")
@SecurityRequirement(name = "bearerAuth")
public class ProposalController {

    private final ProposalService proposalService;

    @GetMapping
    @PreAuthorize("hasAuthority('PROPOSAL_VIEW') or hasAuthority('CUSTOMER_VIEW') or hasRole('ROLE_ADMIN')")
    @Operation(summary = "Get all proposals")
    public ResponseEntity<ApiResponse<List<ProposalDTO>>> getAllProposals(@RequestParam(required = false) String status) {
        List<ProposalDTO> result = proposalService.getAllProposals(status);
        return ResponseEntity.ok(ApiResponse.success(result, "Fetched proposals successfully"));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('PROPOSAL_VIEW') or hasAuthority('CUSTOMER_VIEW') or hasRole('ROLE_ADMIN')")
    @Operation(summary = "Get proposal by ID")
    public ResponseEntity<ApiResponse<ProposalDTO>> getProposalById(@PathVariable String id) {
        ProposalDTO result = proposalService.getProposalById(id);
        return ResponseEntity.ok(ApiResponse.success(result, "Fetched proposal successfully"));
    }

    @PostMapping
    @PreAuthorize("hasAuthority('PROPOSAL_CREATE') or hasRole('ROLE_ADMIN')")
    @Auditable(action = AuditAction.CREATE, module = AuditModule.CRM, entityType = "Proposal", description = "Created CRM Proposal")
    @Operation(summary = "Create a new proposal")
    public ResponseEntity<ApiResponse<ProposalDTO>> createProposal(@Valid @RequestBody ProposalDTO dto) {
        ProposalDTO result = proposalService.createProposal(dto);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(result, "Created proposal successfully"));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('PROPOSAL_UPDATE') or hasRole('ROLE_ADMIN')")
    @Auditable(action = AuditAction.UPDATE, module = AuditModule.CRM, entityType = "Proposal", description = "Updated CRM Proposal")
    @Operation(summary = "Update proposal details and create version entry")
    public ResponseEntity<ApiResponse<ProposalDTO>> updateProposal(
            @PathVariable String id,
            @Valid @RequestBody ProposalDTO dto,
            @RequestParam(required = false) String modifiedBy,
            @RequestParam(required = false) String changeSummary) {
        ProposalDTO result = proposalService.updateProposal(id, dto, modifiedBy, changeSummary);
        return ResponseEntity.ok(ApiResponse.success(result, "Updated proposal successfully"));
    }

    @PostMapping(value = "/{id}/attachments", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasAuthority('PROPOSAL_CREATE') or hasAuthority('PROPOSAL_UPDATE') or hasRole('ROLE_ADMIN')")
    @Auditable(action = AuditAction.UPLOAD, module = AuditModule.CRM, entityType = "Proposal", description = "Uploaded Proposal Attachment")
    @Operation(summary = "Upload proposal attachment document")
    public ResponseEntity<ApiResponse<ProposalDTO>> uploadAttachment(
            @PathVariable String id,
            @RequestParam("file") MultipartFile file) {
        ProposalDTO result = proposalService.uploadAttachment(id, file);
        return ResponseEntity.ok(ApiResponse.success(result, "Uploaded attachment successfully"));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('PROPOSAL_DELETE') or hasRole('ROLE_ADMIN')")
    @Auditable(action = AuditAction.DELETE, module = AuditModule.CRM, entityType = "Proposal", description = "Deleted CRM Proposal")
    @Operation(summary = "Delete proposal")
    public ResponseEntity<ApiResponse<Void>> deleteProposal(@PathVariable String id) {
        proposalService.deleteProposal(id);
        return ResponseEntity.ok(ApiResponse.success("Deleted proposal successfully"));
    }

}
