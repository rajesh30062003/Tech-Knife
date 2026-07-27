package com.techknife.finance.controller;

import com.techknife.audit.annotation.Auditable;
import com.techknife.audit.entity.AuditAction;
import com.techknife.audit.entity.AuditModule;
import com.techknife.backend.dto.ApiResponse;
import com.techknife.finance.dto.TaxRuleDTO;
import com.techknife.finance.service.TaxRuleService;
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
@RequestMapping("/api/v1/finance/tax-rules")
@RequiredArgsConstructor
@Tag(name = "Finance - Tax Rules", description = "Manage Tax Rules (GST, TDS, Professional Tax)")
@SecurityRequirement(name = "bearerAuth")
public class TaxRuleController {

    private final TaxRuleService taxRuleService;

    @GetMapping
    @PreAuthorize("hasAuthority('FINANCE_VIEW') or hasAuthority('FINANCE_MANAGE') or hasRole('ROLE_ADMIN')")
    @Operation(summary = "Get All Tax Rules")
    public ResponseEntity<ApiResponse<List<TaxRuleDTO>>> getAllTaxRules(@RequestParam(required = false) String taxType) {
        List<TaxRuleDTO> result = taxType != null && !taxType.isBlank()
                ? taxRuleService.getTaxRulesByType(taxType)
                : taxRuleService.getAllTaxRules();
        return ResponseEntity.ok(ApiResponse.success(result, "Fetched tax rules successfully"));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('FINANCE_VIEW') or hasAuthority('FINANCE_MANAGE') or hasRole('ROLE_ADMIN')")
    @Operation(summary = "Get Tax Rule by ID")
    public ResponseEntity<ApiResponse<TaxRuleDTO>> getTaxRuleById(@PathVariable String id) {
        TaxRuleDTO result = taxRuleService.getTaxRuleById(id);
        return ResponseEntity.ok(ApiResponse.success(result, "Fetched tax rule successfully"));
    }

    @PostMapping
    @PreAuthorize("hasAuthority('FINANCE_MANAGE') or hasRole('ROLE_ADMIN')")
    @Auditable(action = AuditAction.CREATE, module = AuditModule.FINANCE, entityType = "TaxRule", description = "Created Tax Rule")
    @Operation(summary = "Create Tax Rule")
    public ResponseEntity<ApiResponse<TaxRuleDTO>> createTaxRule(@Valid @RequestBody TaxRuleDTO dto) {
        TaxRuleDTO result = taxRuleService.createTaxRule(dto);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(result, "Created tax rule successfully"));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('FINANCE_MANAGE') or hasRole('ROLE_ADMIN')")
    @Auditable(action = AuditAction.UPDATE, module = AuditModule.FINANCE, entityType = "TaxRule", description = "Updated Tax Rule")
    @Operation(summary = "Update Tax Rule")
    public ResponseEntity<ApiResponse<TaxRuleDTO>> updateTaxRule(@PathVariable String id, @Valid @RequestBody TaxRuleDTO dto) {
        TaxRuleDTO result = taxRuleService.updateTaxRule(id, dto);
        return ResponseEntity.ok(ApiResponse.success(result, "Updated tax rule successfully"));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('FINANCE_MANAGE') or hasRole('ROLE_ADMIN')")
    @Auditable(action = AuditAction.DELETE, module = AuditModule.FINANCE, entityType = "TaxRule", description = "Deleted Tax Rule")
    @Operation(summary = "Delete Tax Rule")
    public ResponseEntity<ApiResponse<Void>> deleteTaxRule(@PathVariable String id) {
        taxRuleService.deleteTaxRule(id);
        return ResponseEntity.ok(ApiResponse.success(null, "Deleted tax rule successfully"));
    }
}
