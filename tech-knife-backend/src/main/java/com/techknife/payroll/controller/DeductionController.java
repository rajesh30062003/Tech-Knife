package com.techknife.payroll.controller;

import com.techknife.audit.annotation.Auditable;
import com.techknife.audit.entity.AuditAction;
import com.techknife.audit.entity.AuditModule;
import com.techknife.backend.dto.ApiResponse;
import com.techknife.payroll.dto.DeductionDTO;
import com.techknife.payroll.service.DeductionService;
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
@RequestMapping("/api/v1/payroll/deductions")
@RequiredArgsConstructor
@Tag(name = "Payroll - Deductions", description = "Manage recurring and one-time payroll deductions")
@SecurityRequirement(name = "bearerAuth")
public class DeductionController {

    private final DeductionService deductionService;

    @GetMapping
    @PreAuthorize("hasAuthority('PAYROLL_VIEW') or hasAuthority('PAYROLL_PROCESS') or hasRole('ROLE_ADMIN')")
    @Operation(summary = "Get All Deductions")
    public ResponseEntity<ApiResponse<List<DeductionDTO>>> getAllDeductions() {
        List<DeductionDTO> result = deductionService.getAllDeductions();
        return ResponseEntity.ok(ApiResponse.success(result, "Fetched deduction records successfully"));
    }

    @GetMapping("/employee/{employeeId}")
    @PreAuthorize("hasAuthority('PAYROLL_VIEW') or hasAuthority('PAYROLL_PROCESS') or hasRole('ROLE_ADMIN')")
    @Operation(summary = "Get Deductions by Employee ID")
    public ResponseEntity<ApiResponse<List<DeductionDTO>>> getDeductionsByEmployeeId(@PathVariable String employeeId) {
        List<DeductionDTO> result = deductionService.getDeductionsByEmployeeId(employeeId);
        return ResponseEntity.ok(ApiResponse.success(result, "Fetched employee deductions successfully"));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('PAYROLL_VIEW') or hasAuthority('PAYROLL_PROCESS') or hasRole('ROLE_ADMIN')")
    @Operation(summary = "Get Deduction by ID")
    public ResponseEntity<ApiResponse<DeductionDTO>> getDeductionById(@PathVariable String id) {
        DeductionDTO result = deductionService.getDeductionById(id);
        return ResponseEntity.ok(ApiResponse.success(result, "Fetched deduction details successfully"));
    }

    @PostMapping
    @PreAuthorize("hasAuthority('PAYROLL_PROCESS') or hasRole('ROLE_ADMIN')")
    @Auditable(action = AuditAction.CREATE, module = AuditModule.PAYROLL, entityType = "Deduction", description = "Created Deduction Record")
    @Operation(summary = "Create Deduction Record")
    public ResponseEntity<ApiResponse<DeductionDTO>> createDeduction(@Valid @RequestBody DeductionDTO dto) {
        DeductionDTO result = deductionService.createDeduction(dto);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(result, "Created deduction record successfully"));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('PAYROLL_PROCESS') or hasRole('ROLE_ADMIN')")
    @Auditable(action = AuditAction.UPDATE, module = AuditModule.PAYROLL, entityType = "Deduction", description = "Updated Deduction Record")
    @Operation(summary = "Update Deduction Record")
    public ResponseEntity<ApiResponse<DeductionDTO>> updateDeduction(@PathVariable String id, @Valid @RequestBody DeductionDTO dto) {
        DeductionDTO result = deductionService.updateDeduction(id, dto);
        return ResponseEntity.ok(ApiResponse.success(result, "Updated deduction record successfully"));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('PAYROLL_PROCESS') or hasRole('ROLE_ADMIN')")
    @Auditable(action = AuditAction.DELETE, module = AuditModule.PAYROLL, entityType = "Deduction", description = "Deleted Deduction Record")
    @Operation(summary = "Delete Deduction Record")
    public ResponseEntity<ApiResponse<Void>> deleteDeduction(@PathVariable String id) {
        deductionService.deleteDeduction(id);
        return ResponseEntity.ok(ApiResponse.success(null, "Deleted deduction record successfully"));
    }
}
