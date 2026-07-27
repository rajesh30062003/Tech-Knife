package com.techknife.payroll.controller;

import com.techknife.audit.annotation.Auditable;
import com.techknife.audit.entity.AuditAction;
import com.techknife.audit.entity.AuditModule;
import com.techknife.backend.dto.ApiResponse;
import com.techknife.payroll.dto.LoanDTO;
import com.techknife.payroll.dto.LoanRepaymentDTO;
import com.techknife.payroll.service.LoanService;
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
@RequestMapping("/api/v1/payroll/loans")
@RequiredArgsConstructor
@Tag(name = "Payroll - Loans & Repayments", description = "Manage employee loans and repayment schedules")
@SecurityRequirement(name = "bearerAuth")
public class LoanController {

    private final LoanService loanService;

    @GetMapping
    @PreAuthorize("hasAuthority('LOAN_MANAGE') or hasAuthority('PAYROLL_VIEW') or hasRole('ROLE_ADMIN')")
    @Operation(summary = "Get All Loans")
    public ResponseEntity<ApiResponse<List<LoanDTO>>> getAllLoans() {
        List<LoanDTO> result = loanService.getAllLoans();
        return ResponseEntity.ok(ApiResponse.success(result, "Fetched loan records successfully"));
    }

    @GetMapping("/employee/{employeeId}")
    @PreAuthorize("hasAuthority('LOAN_MANAGE') or hasAuthority('PAYROLL_VIEW') or hasRole('ROLE_ADMIN')")
    @Operation(summary = "Get Loans by Employee ID")
    public ResponseEntity<ApiResponse<List<LoanDTO>>> getLoansByEmployeeId(@PathVariable String employeeId) {
        List<LoanDTO> result = loanService.getLoansByEmployeeId(employeeId);
        return ResponseEntity.ok(ApiResponse.success(result, "Fetched employee loans successfully"));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('LOAN_MANAGE') or hasAuthority('PAYROLL_VIEW') or hasRole('ROLE_ADMIN')")
    @Operation(summary = "Get Loan Details by ID")
    public ResponseEntity<ApiResponse<LoanDTO>> getLoanById(@PathVariable String id) {
        LoanDTO result = loanService.getLoanById(id);
        return ResponseEntity.ok(ApiResponse.success(result, "Fetched loan details successfully"));
    }

    @PostMapping("/apply")
    @PreAuthorize("hasAuthority('LOAN_MANAGE') or hasAuthority('PAYROLL_PROCESS') or hasRole('ROLE_ADMIN')")
    @Auditable(action = AuditAction.CREATE, module = AuditModule.PAYROLL, entityType = "Loan", description = "Applied for Employee Loan")
    @Operation(summary = "Apply for Loan")
    public ResponseEntity<ApiResponse<LoanDTO>> applyForLoan(@Valid @RequestBody LoanDTO dto) {
        LoanDTO result = loanService.applyForLoan(dto);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(result, "Applied for loan successfully"));
    }

    @PatchMapping("/{id}/status")
    @PreAuthorize("hasAuthority('LOAN_MANAGE') or hasRole('ROLE_ADMIN')")
    @Auditable(action = AuditAction.UPDATE, module = AuditModule.PAYROLL, entityType = "Loan", description = "Updated Loan Status")
    @Operation(summary = "Update Loan Status")
    public ResponseEntity<ApiResponse<LoanDTO>> updateLoanStatus(@PathVariable String id, @RequestParam String status) {
        LoanDTO result = loanService.updateLoanStatus(id, status);
        return ResponseEntity.ok(ApiResponse.success(result, "Updated loan status successfully"));
    }

    @GetMapping("/{loanId}/repayments")
    @PreAuthorize("hasAuthority('LOAN_MANAGE') or hasAuthority('PAYROLL_VIEW') or hasRole('ROLE_ADMIN')")
    @Operation(summary = "Get Repayments by Loan ID")
    public ResponseEntity<ApiResponse<List<LoanRepaymentDTO>>> getRepaymentsByLoanId(@PathVariable String loanId) {
        List<LoanRepaymentDTO> result = loanService.getRepaymentsByLoanId(loanId);
        return ResponseEntity.ok(ApiResponse.success(result, "Fetched loan repayment history successfully"));
    }

    @PostMapping("/repayments")
    @PreAuthorize("hasAuthority('LOAN_MANAGE') or hasAuthority('PAYROLL_PROCESS') or hasRole('ROLE_ADMIN')")
    @Auditable(action = AuditAction.CREATE, module = AuditModule.PAYROLL, entityType = "LoanRepayment", description = "Recorded Loan Repayment")
    @Operation(summary = "Record Loan Repayment")
    public ResponseEntity<ApiResponse<LoanRepaymentDTO>> recordRepayment(@Valid @RequestBody LoanRepaymentDTO dto) {
        LoanRepaymentDTO result = loanService.recordRepayment(dto);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(result, "Recorded loan repayment successfully"));
    }
}
