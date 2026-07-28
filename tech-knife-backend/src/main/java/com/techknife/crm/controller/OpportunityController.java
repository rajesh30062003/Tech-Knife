package com.techknife.crm.controller;

import com.techknife.audit.annotation.Auditable;
import com.techknife.audit.entity.AuditAction;
import com.techknife.audit.entity.AuditModule;
import com.techknife.backend.dto.ApiResponse;
import com.techknife.crm.dto.OpportunityDTO;
import com.techknife.crm.service.OpportunityService;
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
@RequestMapping("/api/v1/crm/opportunities")
@RequiredArgsConstructor
@Tag(name = "CRM - Opportunity Management", description = "Endpoints for managing CRM Sales Opportunities")
@SecurityRequirement(name = "bearerAuth")
public class OpportunityController {

    private final OpportunityService opportunityService;

    @GetMapping
    @PreAuthorize("hasAuthority('LEAD_VIEW') or hasAuthority('CUSTOMER_VIEW') or hasRole('ROLE_ADMIN')")
    @Operation(summary = "Get all opportunities")
    public ResponseEntity<ApiResponse<List<OpportunityDTO>>> getAllOpportunities(
            @RequestParam(required = false) String stage,
            @RequestParam(required = false) String status) {
        List<OpportunityDTO> result = opportunityService.getAllOpportunities(stage, status);
        return ResponseEntity.ok(ApiResponse.success(result, "Fetched opportunities successfully"));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('LEAD_VIEW') or hasAuthority('CUSTOMER_VIEW') or hasRole('ROLE_ADMIN')")
    @Operation(summary = "Get opportunity by ID")
    public ResponseEntity<ApiResponse<OpportunityDTO>> getOpportunityById(@PathVariable String id) {
        OpportunityDTO result = opportunityService.getOpportunityById(id);
        return ResponseEntity.ok(ApiResponse.success(result, "Fetched opportunity successfully"));
    }

    @PostMapping
    @PreAuthorize("hasAuthority('LEAD_CREATE') or hasAuthority('CUSTOMER_CREATE') or hasRole('ROLE_ADMIN')")
    @Auditable(action = AuditAction.CREATE, module = AuditModule.CRM, entityType = "Opportunity", description = "Created CRM Opportunity")
    @Operation(summary = "Create a new opportunity")
    public ResponseEntity<ApiResponse<OpportunityDTO>> createOpportunity(@Valid @RequestBody OpportunityDTO dto) {
        OpportunityDTO result = opportunityService.createOpportunity(dto);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(result, "Created opportunity successfully"));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('LEAD_UPDATE') or hasAuthority('CUSTOMER_UPDATE') or hasRole('ROLE_ADMIN')")
    @Auditable(action = AuditAction.UPDATE, module = AuditModule.CRM, entityType = "Opportunity", description = "Updated CRM Opportunity")
    @Operation(summary = "Update an existing opportunity")
    public ResponseEntity<ApiResponse<OpportunityDTO>> updateOpportunity(
            @PathVariable String id,
            @Valid @RequestBody OpportunityDTO dto) {
        OpportunityDTO result = opportunityService.updateOpportunity(id, dto);
        return ResponseEntity.ok(ApiResponse.success(result, "Updated opportunity successfully"));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('LEAD_DELETE') or hasRole('ROLE_ADMIN')")
    @Auditable(action = AuditAction.DELETE, module = AuditModule.CRM, entityType = "Opportunity", description = "Deleted CRM Opportunity")
    @Operation(summary = "Delete opportunity")
    public ResponseEntity<ApiResponse<Void>> deleteOpportunity(@PathVariable String id) {
        opportunityService.deleteOpportunity(id);
        return ResponseEntity.ok(ApiResponse.success("Deleted opportunity successfully"));
    }

}
