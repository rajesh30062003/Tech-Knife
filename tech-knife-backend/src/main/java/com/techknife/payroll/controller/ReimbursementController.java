package com.techknife.payroll.controller;

import com.techknife.audit.annotation.Auditable;
import com.techknife.audit.entity.AuditAction;
import com.techknife.audit.entity.AuditModule;
import com.techknife.backend.dto.ApiResponse;
import com.techknife.payroll.dto.ReimbursementDTO;
import com.techknife.payroll.service.ReimbursementService;
import com.techknife.security.CurrentUser;
import com.techknife.security.UserPrincipal;
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
@RequestMapping("/api/v1/payroll/reimbursements")
@RequiredArgsConstructor
@Tag(name = "Payroll - Reimbursements", description = "Submit and approve expense reimbursement claims")
@SecurityRequirement(name = "bearerAuth")
public class ReimbursementController {

    private final ReimbursementService reimbursementService;

    @GetMapping
    @PreAuthorize("hasAuthority('BONUS_MANAGE') or hasAuthority('PAYROLL_VIEW') or hasRole('ROLE_ADMIN')")
    @Operation(summary = "Get All Reimbursements")
    public ResponseEntity<ApiResponse<List<ReimbursementDTO>>> getAllReimbursements() {
        List<ReimbursementDTO> result = reimbursementService.getAllReimbursements();
        return ResponseEntity.ok(ApiResponse.success(result, "Fetched reimbursement claims successfully"));
    }

    @GetMapping("/employee/{employeeId}")
    @PreAuthorize("hasAuthority('BONUS_MANAGE') or hasAuthority('PAYROLL_VIEW') or hasRole('ROLE_ADMIN')")
    @Operation(summary = "Get Reimbursements by Employee ID")
    public ResponseEntity<ApiResponse<List<ReimbursementDTO>>> getReimbursementsByEmployeeId(@PathVariable String employeeId) {
        List<ReimbursementDTO> result = reimbursementService.getReimbursementsByEmployeeId(employeeId);
        return ResponseEntity.ok(ApiResponse.success(result, "Fetched employee reimbursements successfully"));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('BONUS_MANAGE') or hasAuthority('PAYROLL_VIEW') or hasRole('ROLE_ADMIN')")
    @Operation(summary = "Get Reimbursement by ID")
    public ResponseEntity<ApiResponse<ReimbursementDTO>> getReimbursementById(@PathVariable String id) {
        ReimbursementDTO result = reimbursementService.getReimbursementById(id);
        return ResponseEntity.ok(ApiResponse.success(result, "Fetched reimbursement details successfully"));
    }

    @PostMapping
    @PreAuthorize("hasAuthority('BONUS_MANAGE') or hasAuthority('PAYROLL_PROCESS') or hasRole('ROLE_ADMIN')")
    @Auditable(action = AuditAction.CREATE, module = AuditModule.PAYROLL, entityType = "Reimbursement", description = "Submitted Expense Reimbursement")
    @Operation(summary = "Submit Reimbursement Claim")
    public ResponseEntity<ApiResponse<ReimbursementDTO>> submitReimbursement(@Valid @RequestBody ReimbursementDTO dto) {
        ReimbursementDTO result = reimbursementService.submitReimbursement(dto);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(result, "Submitted reimbursement claim successfully"));
    }

    @PatchMapping("/{id}/approval")
    @PreAuthorize("hasAuthority('BONUS_MANAGE') or hasRole('ROLE_ADMIN')")
    @Auditable(action = AuditAction.APPROVE, module = AuditModule.PAYROLL, entityType = "Reimbursement", description = "Updated Reimbursement Approval Status")
    @Operation(summary = "Update Reimbursement Approval Status")
    public ResponseEntity<ApiResponse<ReimbursementDTO>> updateApprovalStatus(
            @PathVariable String id,
            @RequestParam String approvalStatus,
            @CurrentUser UserPrincipal currentUser) {
        String approvedBy = currentUser != null ? currentUser.getUsername() : "ADMIN";
        ReimbursementDTO result = reimbursementService.updateApprovalStatus(id, approvalStatus, approvedBy);
        return ResponseEntity.ok(ApiResponse.success(result, "Updated reimbursement approval status successfully"));
    }
}
