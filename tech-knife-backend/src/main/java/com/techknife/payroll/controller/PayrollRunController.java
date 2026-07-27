package com.techknife.payroll.controller;

import com.techknife.audit.annotation.Auditable;
import com.techknife.audit.entity.AuditAction;
import com.techknife.audit.entity.AuditModule;
import com.techknife.backend.dto.ApiResponse;
import com.techknife.payroll.dto.PayrollRunDTO;
import com.techknife.payroll.service.PayrollRunService;
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
@RequestMapping("/api/v1/payroll/payroll-runs")
@RequiredArgsConstructor
@Tag(name = "Payroll - Payroll Runs", description = "Process and disburse payroll runs")
@SecurityRequirement(name = "bearerAuth")
public class PayrollRunController {

    private final PayrollRunService payrollRunService;

    @GetMapping
    @PreAuthorize("hasAuthority('PAYROLL_VIEW') or hasAuthority('PAYROLL_PROCESS') or hasRole('ROLE_ADMIN')")
    @Operation(summary = "Get All Payroll Runs")
    public ResponseEntity<ApiResponse<List<PayrollRunDTO>>> getAllRuns() {
        List<PayrollRunDTO> result = payrollRunService.getAllRuns();
        return ResponseEntity.ok(ApiResponse.success(result, "Fetched payroll runs successfully"));
    }

    @GetMapping("/cycle/{cycleId}")
    @PreAuthorize("hasAuthority('PAYROLL_VIEW') or hasAuthority('PAYROLL_PROCESS') or hasRole('ROLE_ADMIN')")
    @Operation(summary = "Get Payroll Runs by Cycle ID")
    public ResponseEntity<ApiResponse<List<PayrollRunDTO>>> getRunsByCycleId(@PathVariable String cycleId) {
        List<PayrollRunDTO> result = payrollRunService.getRunsByCycleId(cycleId);
        return ResponseEntity.ok(ApiResponse.success(result, "Fetched payroll runs for cycle successfully"));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('PAYROLL_VIEW') or hasAuthority('PAYROLL_PROCESS') or hasRole('ROLE_ADMIN')")
    @Operation(summary = "Get Payroll Run by ID")
    public ResponseEntity<ApiResponse<PayrollRunDTO>> getRunById(@PathVariable String id) {
        PayrollRunDTO result = payrollRunService.getRunById(id);
        return ResponseEntity.ok(ApiResponse.success(result, "Fetched payroll run details successfully"));
    }

    @PostMapping("/process")
    @PreAuthorize("hasAuthority('PAYROLL_PROCESS') or hasRole('ROLE_ADMIN')")
    @Auditable(action = AuditAction.CREATE, module = AuditModule.PAYROLL, entityType = "PayrollRun", description = "Processed Payroll Run")
    @Operation(summary = "Process Payroll Run")
    public ResponseEntity<ApiResponse<PayrollRunDTO>> processPayrollRun(@Valid @RequestBody PayrollRunDTO dto) {
        PayrollRunDTO result = payrollRunService.processPayrollRun(dto);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(result, "Payroll run processed successfully"));
    }

    @PatchMapping("/{id}/status")
    @PreAuthorize("hasAuthority('PAYROLL_PROCESS') or hasRole('ROLE_ADMIN')")
    @Auditable(action = AuditAction.UPDATE, module = AuditModule.PAYROLL, entityType = "PayrollRun", description = "Updated Payroll Run Status")
    @Operation(summary = "Update Payroll Run Status")
    public ResponseEntity<ApiResponse<PayrollRunDTO>> updateRunStatus(@PathVariable String id, @RequestParam String status) {
        PayrollRunDTO result = payrollRunService.updateRunStatus(id, status);
        return ResponseEntity.ok(ApiResponse.success(result, "Updated payroll run status successfully"));
    }
}
