package com.techknife.finance.controller;

import com.techknife.audit.annotation.Auditable;
import com.techknife.audit.entity.AuditAction;
import com.techknife.audit.entity.AuditModule;
import com.techknife.backend.dto.ApiResponse;
import com.techknife.finance.dto.ExpenseDTO;
import com.techknife.finance.service.ExpenseService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/api/v1/finance/expenses")
@RequiredArgsConstructor
@Tag(name = "Finance - Expenses", description = "Manage Expenses and Approval Workflows")
@SecurityRequirement(name = "bearerAuth")
public class ExpenseController {

    private final ExpenseService expenseService;

    @GetMapping
    @PreAuthorize("hasAuthority('FINANCE_VIEW') or hasAuthority('EXPENSE_APPROVE') or hasAuthority('FINANCE_MANAGE') or hasRole('ROLE_ADMIN')")
    @Operation(summary = "Get All Expenses")
    public ResponseEntity<ApiResponse<List<ExpenseDTO>>> getAllExpenses(
            @RequestParam(required = false) String categoryId,
            @RequestParam(required = false) String employeeId) {

        List<ExpenseDTO> result;
        if (categoryId != null && !categoryId.isBlank()) {
            result = expenseService.getExpensesByCategory(categoryId);
        } else if (employeeId != null && !employeeId.isBlank()) {
            result = expenseService.getExpensesByEmployee(employeeId);
        } else {
            result = expenseService.getAllExpenses();
        }

        return ResponseEntity.ok(ApiResponse.success(result, "Fetched expenses successfully"));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('FINANCE_VIEW') or hasAuthority('EXPENSE_APPROVE') or hasAuthority('FINANCE_MANAGE') or hasRole('ROLE_ADMIN')")
    @Operation(summary = "Get Expense by ID")
    public ResponseEntity<ApiResponse<ExpenseDTO>> getExpenseById(@PathVariable String id) {
        ExpenseDTO result = expenseService.getExpenseById(id);
        return ResponseEntity.ok(ApiResponse.success(result, "Fetched expense successfully"));
    }

    @PostMapping
    @PreAuthorize("hasAuthority('EXPENSE_APPROVE') or hasAuthority('FINANCE_MANAGE') or hasRole('ROLE_ADMIN')")
    @Auditable(action = AuditAction.CREATE, module = AuditModule.FINANCE, entityType = "Expense", description = "Created Expense")
    @Operation(summary = "Create Expense")
    public ResponseEntity<ApiResponse<ExpenseDTO>> createExpense(@Valid @RequestBody ExpenseDTO dto) {
        ExpenseDTO result = expenseService.createExpense(dto);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(result, "Created expense successfully"));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('EXPENSE_APPROVE') or hasAuthority('FINANCE_MANAGE') or hasRole('ROLE_ADMIN')")
    @Auditable(action = AuditAction.UPDATE, module = AuditModule.FINANCE, entityType = "Expense", description = "Updated Expense")
    @Operation(summary = "Update Expense")
    public ResponseEntity<ApiResponse<ExpenseDTO>> updateExpense(@PathVariable String id, @Valid @RequestBody ExpenseDTO dto) {
        ExpenseDTO result = expenseService.updateExpense(id, dto);
        return ResponseEntity.ok(ApiResponse.success(result, "Updated expense successfully"));
    }

    @PostMapping("/{id}/approve")
    @PreAuthorize("hasAuthority('EXPENSE_APPROVE') or hasAuthority('FINANCE_MANAGE') or hasRole('ROLE_ADMIN')")
    @Auditable(action = AuditAction.APPROVE, module = AuditModule.FINANCE, entityType = "Expense", description = "Approved/Rejected Expense")
    @Operation(summary = "Approve or Reject Expense")
    public ResponseEntity<ApiResponse<ExpenseDTO>> approveExpense(
            @PathVariable String id,
            @RequestParam(defaultValue = "APPROVED") String status,
            Principal principal) {

        String username = principal != null ? principal.getName() : "ADMIN";
        ExpenseDTO result = expenseService.approveExpense(id, status, username);
        return ResponseEntity.ok(ApiResponse.success(result, "Updated expense approval status successfully"));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('FINANCE_MANAGE') or hasRole('ROLE_ADMIN')")
    @Auditable(action = AuditAction.DELETE, module = AuditModule.FINANCE, entityType = "Expense", description = "Deleted Expense")
    @Operation(summary = "Delete Expense")
    public ResponseEntity<ApiResponse<Void>> deleteExpense(@PathVariable String id) {
        expenseService.deleteExpense(id);
        return ResponseEntity.ok(ApiResponse.success(null, "Deleted expense successfully"));
    }
}
