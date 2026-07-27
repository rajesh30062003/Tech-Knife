package com.techknife.payroll.controller;

import com.techknife.audit.annotation.Auditable;
import com.techknife.audit.entity.AuditAction;
import com.techknife.audit.entity.AuditModule;
import com.techknife.backend.dto.ApiResponse;
import com.techknife.payroll.dto.PayrollAdjustmentDTO;
import com.techknife.payroll.service.PayrollAdjustmentService;
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
@RequestMapping("/api/v1/payroll/payroll-adjustments")
@RequiredArgsConstructor
@Tag(name = "Payroll - Payroll Adjustments", description = "Manage bonuses, overtime, penalties, and expense adjustments")
@SecurityRequirement(name = "bearerAuth")
public class PayrollAdjustmentController {

    private final PayrollAdjustmentService payrollAdjustmentService;

    @GetMapping
    @PreAuthorize("hasAuthority('PAYROLL_VIEW') or hasAuthority('PAYROLL_PROCESS') or hasRole('ROLE_ADMIN')")
    @Operation(summary = "Get All Payroll Adjustments")
    public ResponseEntity<ApiResponse<List<PayrollAdjustmentDTO>>> getAllAdjustments() {
        List<PayrollAdjustmentDTO> result = payrollAdjustmentService.getAllAdjustments();
        return ResponseEntity.ok(ApiResponse.success(result, "Fetched payroll adjustments successfully"));
    }

    @GetMapping("/employee/{employeeId}")
    @PreAuthorize("hasAuthority('PAYROLL_VIEW') or hasAuthority('PAYROLL_PROCESS') or hasRole('ROLE_ADMIN')")
    @Operation(summary = "Get Adjustments by Employee ID")
    public ResponseEntity<ApiResponse<List<PayrollAdjustmentDTO>>> getAdjustmentsByEmployeeId(@PathVariable String employeeId) {
        List<PayrollAdjustmentDTO> result = payrollAdjustmentService.getAdjustmentsByEmployeeId(employeeId);
        return ResponseEntity.ok(ApiResponse.success(result, "Fetched adjustments for employee successfully"));
    }

    @GetMapping("/cycle/{cycleId}")
    @PreAuthorize("hasAuthority('PAYROLL_VIEW') or hasAuthority('PAYROLL_PROCESS') or hasRole('ROLE_ADMIN')")
    @Operation(summary = "Get Adjustments by Payroll Cycle ID")
    public ResponseEntity<ApiResponse<List<PayrollAdjustmentDTO>>> getAdjustmentsByCycleId(@PathVariable String cycleId) {
        List<PayrollAdjustmentDTO> result = payrollAdjustmentService.getAdjustmentsByCycleId(cycleId);
        return ResponseEntity.ok(ApiResponse.success(result, "Fetched adjustments for cycle successfully"));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('PAYROLL_VIEW') or hasAuthority('PAYROLL_PROCESS') or hasRole('ROLE_ADMIN')")
    @Operation(summary = "Get Adjustment by ID")
    public ResponseEntity<ApiResponse<PayrollAdjustmentDTO>> getAdjustmentById(@PathVariable String id) {
        PayrollAdjustmentDTO result = payrollAdjustmentService.getAdjustmentById(id);
        return ResponseEntity.ok(ApiResponse.success(result, "Fetched adjustment details successfully"));
    }

    @PostMapping
    @PreAuthorize("hasAuthority('PAYROLL_PROCESS') or hasRole('ROLE_ADMIN')")
    @Auditable(action = AuditAction.CREATE, module = AuditModule.PAYROLL, entityType = "PayrollAdjustment", description = "Created Payroll Adjustment")
    @Operation(summary = "Create Payroll Adjustment")
    public ResponseEntity<ApiResponse<PayrollAdjustmentDTO>> createAdjustment(@Valid @RequestBody PayrollAdjustmentDTO dto) {
        PayrollAdjustmentDTO result = payrollAdjustmentService.createAdjustment(dto);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(result, "Created payroll adjustment successfully"));
    }

    @PatchMapping("/{id}/status")
    @PreAuthorize("hasAuthority('PAYROLL_PROCESS') or hasRole('ROLE_ADMIN')")
    @Auditable(action = AuditAction.UPDATE, module = AuditModule.PAYROLL, entityType = "PayrollAdjustment", description = "Updated Adjustment Status")
    @Operation(summary = "Update Adjustment Status")
    public ResponseEntity<ApiResponse<PayrollAdjustmentDTO>> updateAdjustmentStatus(@PathVariable String id, @RequestParam String status) {
        PayrollAdjustmentDTO result = payrollAdjustmentService.updateAdjustmentStatus(id, status);
        return ResponseEntity.ok(ApiResponse.success(result, "Updated adjustment status successfully"));
    }
}
