package com.techknife.payroll.controller;

import com.techknife.audit.annotation.Auditable;
import com.techknife.audit.entity.AuditAction;
import com.techknife.audit.entity.AuditModule;
import com.techknife.backend.dto.ApiResponse;
import com.techknife.payroll.dto.PayslipDTO;
import com.techknife.payroll.service.PayslipService;
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
@RequestMapping("/api/v1/payroll/payslips")
@RequiredArgsConstructor
@Tag(name = "Payroll - Payslips", description = "View and generate employee payslips")
@SecurityRequirement(name = "bearerAuth")
public class PayslipController {

    private final PayslipService payslipService;

    @GetMapping
    @PreAuthorize("hasAuthority('PAYSLIP_VIEW') or hasAuthority('PAYROLL_VIEW') or hasRole('ROLE_ADMIN')")
    @Operation(summary = "Get All Payslips")
    public ResponseEntity<ApiResponse<List<PayslipDTO>>> getAllPayslips() {
        List<PayslipDTO> result = payslipService.getAllPayslips();
        return ResponseEntity.ok(ApiResponse.success(result, "Fetched payslips successfully"));
    }

    @GetMapping("/employee/{employeeId}")
    @PreAuthorize("hasAuthority('PAYSLIP_VIEW') or hasAuthority('PAYROLL_VIEW') or hasRole('ROLE_ADMIN')")
    @Operation(summary = "Get Payslips by Employee ID")
    public ResponseEntity<ApiResponse<List<PayslipDTO>>> getPayslipsByEmployeeId(@PathVariable String employeeId) {
        List<PayslipDTO> result = payslipService.getPayslipsByEmployeeId(employeeId);
        return ResponseEntity.ok(ApiResponse.success(result, "Fetched employee payslips successfully"));
    }

    @GetMapping("/run/{runId}")
    @PreAuthorize("hasAuthority('PAYSLIP_VIEW') or hasAuthority('PAYROLL_VIEW') or hasRole('ROLE_ADMIN')")
    @Operation(summary = "Get Payslips by Payroll Run ID")
    public ResponseEntity<ApiResponse<List<PayslipDTO>>> getPayslipsByRunId(@PathVariable String runId) {
        List<PayslipDTO> result = payslipService.getPayslipsByRunId(runId);
        return ResponseEntity.ok(ApiResponse.success(result, "Fetched payslips for payroll run successfully"));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('PAYSLIP_VIEW') or hasAuthority('PAYROLL_VIEW') or hasRole('ROLE_ADMIN')")
    @Operation(summary = "Get Payslip Details by ID")
    public ResponseEntity<ApiResponse<PayslipDTO>> getPayslipById(@PathVariable String id) {
        PayslipDTO result = payslipService.getPayslipById(id);
        return ResponseEntity.ok(ApiResponse.success(result, "Fetched payslip details successfully"));
    }

    @PostMapping("/generate")
    @PreAuthorize("hasAuthority('PAYROLL_PROCESS') or hasRole('ROLE_ADMIN')")
    @Auditable(action = AuditAction.CREATE, module = AuditModule.PAYROLL, entityType = "Payslip", description = "Generated Payslip")
    @Operation(summary = "Generate Payslip")
    public ResponseEntity<ApiResponse<PayslipDTO>> generatePayslip(@Valid @RequestBody PayslipDTO dto) {
        PayslipDTO result = payslipService.generatePayslip(dto);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(result, "Generated payslip successfully"));
    }
}
