package com.techknife.payroll.controller;

import com.techknife.audit.annotation.Auditable;
import com.techknife.audit.entity.AuditAction;
import com.techknife.audit.entity.AuditModule;
import com.techknife.backend.dto.ApiResponse;
import com.techknife.payroll.dto.PayrollCycleDTO;
import com.techknife.payroll.service.PayrollCycleService;
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
@RequestMapping("/api/v1/payroll/payroll-cycles")
@RequiredArgsConstructor
@Tag(name = "Payroll - Payroll Cycles", description = "Manage monthly/periodic payroll cycles")
@SecurityRequirement(name = "bearerAuth")
public class PayrollCycleController {

    private final PayrollCycleService payrollCycleService;

    @GetMapping
    @PreAuthorize("hasAuthority('PAYROLL_VIEW') or hasAuthority('PAYROLL_PROCESS') or hasRole('ROLE_ADMIN')")
    @Operation(summary = "Get All Payroll Cycles")
    public ResponseEntity<ApiResponse<List<PayrollCycleDTO>>> getAllCycles() {
        List<PayrollCycleDTO> result = payrollCycleService.getAllCycles();
        return ResponseEntity.ok(ApiResponse.success(result, "Fetched payroll cycles successfully"));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('PAYROLL_VIEW') or hasAuthority('PAYROLL_PROCESS') or hasRole('ROLE_ADMIN')")
    @Operation(summary = "Get Payroll Cycle by ID")
    public ResponseEntity<ApiResponse<PayrollCycleDTO>> getCycleById(@PathVariable String id) {
        PayrollCycleDTO result = payrollCycleService.getCycleById(id);
        return ResponseEntity.ok(ApiResponse.success(result, "Fetched payroll cycle successfully"));
    }

    @PostMapping
    @PreAuthorize("hasAuthority('PAYROLL_PROCESS') or hasRole('ROLE_ADMIN')")
    @Auditable(action = AuditAction.CREATE, module = AuditModule.PAYROLL, entityType = "PayrollCycle", description = "Created Payroll Cycle")
    @Operation(summary = "Create Payroll Cycle")
    public ResponseEntity<ApiResponse<PayrollCycleDTO>> createCycle(@Valid @RequestBody PayrollCycleDTO dto) {
        PayrollCycleDTO result = payrollCycleService.createCycle(dto);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(result, "Created payroll cycle successfully"));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('PAYROLL_PROCESS') or hasRole('ROLE_ADMIN')")
    @Auditable(action = AuditAction.UPDATE, module = AuditModule.PAYROLL, entityType = "PayrollCycle", description = "Updated Payroll Cycle")
    @Operation(summary = "Update Payroll Cycle")
    public ResponseEntity<ApiResponse<PayrollCycleDTO>> updateCycle(@PathVariable String id, @Valid @RequestBody PayrollCycleDTO dto) {
        PayrollCycleDTO result = payrollCycleService.updateCycle(id, dto);
        return ResponseEntity.ok(ApiResponse.success(result, "Updated payroll cycle successfully"));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('PAYROLL_PROCESS') or hasRole('ROLE_ADMIN')")
    @Auditable(action = AuditAction.DELETE, module = AuditModule.PAYROLL, entityType = "PayrollCycle", description = "Deleted Payroll Cycle")
    @Operation(summary = "Delete Payroll Cycle")
    public ResponseEntity<ApiResponse<Void>> deleteCycle(@PathVariable String id) {
        payrollCycleService.deleteCycle(id);
        return ResponseEntity.ok(ApiResponse.success(null, "Deleted payroll cycle successfully"));
    }
}
