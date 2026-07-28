package com.techknife.crm.controller;

import com.techknife.audit.annotation.Auditable;
import com.techknife.audit.entity.AuditAction;
import com.techknife.audit.entity.AuditModule;
import com.techknife.backend.dto.ApiResponse;
import com.techknife.crm.dto.LeadDTO;
import com.techknife.crm.service.LeadService;
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
@RequestMapping("/api/v1/crm/leads")
@RequiredArgsConstructor
@Tag(name = "CRM - Lead Management", description = "Endpoints for managing CRM Leads")
@SecurityRequirement(name = "bearerAuth")
public class LeadController {

    private final LeadService leadService;

    @GetMapping
    @PreAuthorize("hasAuthority('LEAD_VIEW') or hasAuthority('CUSTOMER_VIEW') or hasRole('ROLE_ADMIN')")
    @Operation(summary = "Get all leads")
    public ResponseEntity<ApiResponse<List<LeadDTO>>> getAllLeads(@RequestParam(required = false) String status) {
        List<LeadDTO> result = leadService.getAllLeads(status);
        return ResponseEntity.ok(ApiResponse.success(result, "Fetched leads successfully"));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('LEAD_VIEW') or hasAuthority('CUSTOMER_VIEW') or hasRole('ROLE_ADMIN')")
    @Operation(summary = "Get lead by ID")
    public ResponseEntity<ApiResponse<LeadDTO>> getLeadById(@PathVariable String id) {
        LeadDTO result = leadService.getLeadById(id);
        return ResponseEntity.ok(ApiResponse.success(result, "Fetched lead successfully"));
    }

    @PostMapping
    @PreAuthorize("hasAuthority('LEAD_CREATE') or hasRole('ROLE_ADMIN')")
    @Auditable(action = AuditAction.CREATE, module = AuditModule.CRM, entityType = "Lead", description = "Created CRM Lead")
    @Operation(summary = "Create a new lead")
    public ResponseEntity<ApiResponse<LeadDTO>> createLead(@Valid @RequestBody LeadDTO dto) {
        LeadDTO result = leadService.createLead(dto);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(result, "Created lead successfully"));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('LEAD_UPDATE') or hasRole('ROLE_ADMIN')")
    @Auditable(action = AuditAction.UPDATE, module = AuditModule.CRM, entityType = "Lead", description = "Updated CRM Lead")
    @Operation(summary = "Update an existing lead")
    public ResponseEntity<ApiResponse<LeadDTO>> updateLead(
            @PathVariable String id,
            @Valid @RequestBody LeadDTO dto) {
        LeadDTO result = leadService.updateLead(id, dto);
        return ResponseEntity.ok(ApiResponse.success(result, "Updated lead successfully"));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('LEAD_DELETE') or hasRole('ROLE_ADMIN')")
    @Auditable(action = AuditAction.DELETE, module = AuditModule.CRM, entityType = "Lead", description = "Deleted CRM Lead")
    @Operation(summary = "Delete lead")
    public ResponseEntity<ApiResponse<Void>> deleteLead(@PathVariable String id) {
        leadService.deleteLead(id);
        return ResponseEntity.ok(ApiResponse.success("Deleted lead successfully"));
    }

}
