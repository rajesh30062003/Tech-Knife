package com.techknife.payroll.controller;

import com.techknife.audit.annotation.Auditable;
import com.techknife.audit.entity.AuditAction;
import com.techknife.audit.entity.AuditModule;
import com.techknife.backend.dto.ApiResponse;
import com.techknife.payroll.dto.SalaryComponentDTO;
import com.techknife.payroll.service.SalaryComponentService;
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
@RequestMapping("/api/v1/payroll/salary-components")
@RequiredArgsConstructor
@Tag(name = "Payroll - Salary Components", description = "Manage salary earnings and deductions components")
@SecurityRequirement(name = "bearerAuth")
public class SalaryComponentController {

    private final SalaryComponentService salaryComponentService;

    @GetMapping
    @PreAuthorize("hasAuthority('SALARY_STRUCTURE_MANAGE') or hasAuthority('PAYROLL_VIEW') or hasRole('ROLE_ADMIN')")
    @Operation(summary = "Get All Salary Components")
    public ResponseEntity<ApiResponse<List<SalaryComponentDTO>>> getAllComponents() {
        List<SalaryComponentDTO> result = salaryComponentService.getAllComponents();
        return ResponseEntity.ok(ApiResponse.success(result, "Fetched salary components successfully"));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('SALARY_STRUCTURE_MANAGE') or hasAuthority('PAYROLL_VIEW') or hasRole('ROLE_ADMIN')")
    @Operation(summary = "Get Salary Component by ID")
    public ResponseEntity<ApiResponse<SalaryComponentDTO>> getComponentById(@PathVariable String id) {
        SalaryComponentDTO result = salaryComponentService.getComponentById(id);
        return ResponseEntity.ok(ApiResponse.success(result, "Fetched salary component successfully"));
    }

    @PostMapping
    @PreAuthorize("hasAuthority('SALARY_STRUCTURE_MANAGE') or hasRole('ROLE_ADMIN')")
    @Auditable(action = AuditAction.CREATE, module = AuditModule.PAYROLL, entityType = "SalaryComponent", description = "Created Salary Component")
    @Operation(summary = "Create Salary Component")
    public ResponseEntity<ApiResponse<SalaryComponentDTO>> createComponent(@Valid @RequestBody SalaryComponentDTO dto) {
        SalaryComponentDTO result = salaryComponentService.createComponent(dto);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(result, "Created salary component successfully"));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('SALARY_STRUCTURE_MANAGE') or hasRole('ROLE_ADMIN')")
    @Auditable(action = AuditAction.UPDATE, module = AuditModule.PAYROLL, entityType = "SalaryComponent", description = "Updated Salary Component")
    @Operation(summary = "Update Salary Component")
    public ResponseEntity<ApiResponse<SalaryComponentDTO>> updateComponent(@PathVariable String id, @Valid @RequestBody SalaryComponentDTO dto) {
        SalaryComponentDTO result = salaryComponentService.updateComponent(id, dto);
        return ResponseEntity.ok(ApiResponse.success(result, "Updated salary component successfully"));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('SALARY_STRUCTURE_MANAGE') or hasRole('ROLE_ADMIN')")
    @Auditable(action = AuditAction.DELETE, module = AuditModule.PAYROLL, entityType = "SalaryComponent", description = "Deleted Salary Component")
    @Operation(summary = "Delete Salary Component")
    public ResponseEntity<ApiResponse<Void>> deleteComponent(@PathVariable String id) {
        salaryComponentService.deleteComponent(id);
        return ResponseEntity.ok(ApiResponse.success(null, "Deleted salary component successfully"));
    }
}
