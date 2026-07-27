package com.techknife.payroll.controller;

import com.techknife.audit.annotation.Auditable;
import com.techknife.audit.entity.AuditAction;
import com.techknife.audit.entity.AuditModule;
import com.techknife.backend.dto.ApiResponse;
import com.techknife.payroll.dto.TaxConfigurationDTO;
import com.techknife.payroll.service.TaxConfigurationService;
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
@RequestMapping("/api/v1/payroll/tax-configurations")
@RequiredArgsConstructor
@Tag(name = "Payroll - Tax Configurations", description = "Manage financial year tax slabs and rates")
@SecurityRequirement(name = "bearerAuth")
public class TaxConfigurationController {

    private final TaxConfigurationService taxConfigurationService;

    @GetMapping
    @PreAuthorize("hasAuthority('PAYROLL_VIEW') or hasAuthority('SALARY_STRUCTURE_MANAGE') or hasRole('ROLE_ADMIN')")
    @Operation(summary = "Get All Tax Configurations")
    public ResponseEntity<ApiResponse<List<TaxConfigurationDTO>>> getAllTaxConfigurations() {
        List<TaxConfigurationDTO> result = taxConfigurationService.getAllTaxConfigurations();
        return ResponseEntity.ok(ApiResponse.success(result, "Fetched tax configurations successfully"));
    }

    @GetMapping("/year/{year}")
    @PreAuthorize("hasAuthority('PAYROLL_VIEW') or hasAuthority('SALARY_STRUCTURE_MANAGE') or hasRole('ROLE_ADMIN')")
    @Operation(summary = "Get Tax Configurations by Financial Year")
    public ResponseEntity<ApiResponse<List<TaxConfigurationDTO>>> getTaxConfigurationsByYear(@PathVariable String year) {
        List<TaxConfigurationDTO> result = taxConfigurationService.getTaxConfigurationsByYear(year);
        return ResponseEntity.ok(ApiResponse.success(result, "Fetched tax configurations for financial year successfully"));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('PAYROLL_VIEW') or hasAuthority('SALARY_STRUCTURE_MANAGE') or hasRole('ROLE_ADMIN')")
    @Operation(summary = "Get Tax Configuration by ID")
    public ResponseEntity<ApiResponse<TaxConfigurationDTO>> getTaxConfigurationById(@PathVariable String id) {
        TaxConfigurationDTO result = taxConfigurationService.getTaxConfigurationById(id);
        return ResponseEntity.ok(ApiResponse.success(result, "Fetched tax configuration details successfully"));
    }

    @PostMapping
    @PreAuthorize("hasAuthority('SALARY_STRUCTURE_MANAGE') or hasRole('ROLE_ADMIN')")
    @Auditable(action = AuditAction.CREATE, module = AuditModule.PAYROLL, entityType = "TaxConfiguration", description = "Created Tax Configuration")
    @Operation(summary = "Create Tax Configuration")
    public ResponseEntity<ApiResponse<TaxConfigurationDTO>> createTaxConfiguration(@Valid @RequestBody TaxConfigurationDTO dto) {
        TaxConfigurationDTO result = taxConfigurationService.createTaxConfiguration(dto);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(result, "Created tax configuration successfully"));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('SALARY_STRUCTURE_MANAGE') or hasRole('ROLE_ADMIN')")
    @Auditable(action = AuditAction.UPDATE, module = AuditModule.PAYROLL, entityType = "TaxConfiguration", description = "Updated Tax Configuration")
    @Operation(summary = "Update Tax Configuration")
    public ResponseEntity<ApiResponse<TaxConfigurationDTO>> updateTaxConfiguration(@PathVariable String id, @Valid @RequestBody TaxConfigurationDTO dto) {
        TaxConfigurationDTO result = taxConfigurationService.updateTaxConfiguration(id, dto);
        return ResponseEntity.ok(ApiResponse.success(result, "Updated tax configuration successfully"));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('SALARY_STRUCTURE_MANAGE') or hasRole('ROLE_ADMIN')")
    @Auditable(action = AuditAction.DELETE, module = AuditModule.PAYROLL, entityType = "TaxConfiguration", description = "Deleted Tax Configuration")
    @Operation(summary = "Delete Tax Configuration")
    public ResponseEntity<ApiResponse<Void>> deleteTaxConfiguration(@PathVariable String id) {
        taxConfigurationService.deleteTaxConfiguration(id);
        return ResponseEntity.ok(ApiResponse.success(null, "Deleted tax configuration successfully"));
    }
}
