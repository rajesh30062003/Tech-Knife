package com.techknife.finance.controller;

import com.techknife.audit.annotation.Auditable;
import com.techknife.audit.entity.AuditAction;
import com.techknife.audit.entity.AuditModule;
import com.techknife.backend.dto.ApiResponse;
import com.techknife.finance.dto.BudgetDTO;
import com.techknife.finance.service.BudgetService;
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
@RequestMapping("/api/v1/finance/budgets")
@RequiredArgsConstructor
@Tag(name = "Finance - Budgets", description = "Manage Company, Department, and Project Budgets")
@SecurityRequirement(name = "bearerAuth")
public class BudgetController {

    private final BudgetService budgetService;

    @GetMapping
    @PreAuthorize("hasAuthority('FINANCE_VIEW') or hasAuthority('BUDGET_MANAGE') or hasAuthority('FINANCE_MANAGE') or hasRole('ROLE_ADMIN')")
    @Operation(summary = "Get All Budgets")
    public ResponseEntity<ApiResponse<List<BudgetDTO>>> getAllBudgets(@RequestParam(required = false) String financialYearId) {
        List<BudgetDTO> result = financialYearId != null && !financialYearId.isBlank()
                ? budgetService.getBudgetsByFinancialYear(financialYearId)
                : budgetService.getAllBudgets();
        return ResponseEntity.ok(ApiResponse.success(result, "Fetched budgets successfully"));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('FINANCE_VIEW') or hasAuthority('BUDGET_MANAGE') or hasAuthority('FINANCE_MANAGE') or hasRole('ROLE_ADMIN')")
    @Operation(summary = "Get Budget by ID")
    public ResponseEntity<ApiResponse<BudgetDTO>> getBudgetById(@PathVariable String id) {
        BudgetDTO result = budgetService.getBudgetById(id);
        return ResponseEntity.ok(ApiResponse.success(result, "Fetched budget successfully"));
    }

    @PostMapping
    @PreAuthorize("hasAuthority('BUDGET_MANAGE') or hasAuthority('FINANCE_MANAGE') or hasRole('ROLE_ADMIN')")
    @Auditable(action = AuditAction.CREATE, module = AuditModule.FINANCE, entityType = "Budget", description = "Created Budget")
    @Operation(summary = "Create Budget")
    public ResponseEntity<ApiResponse<BudgetDTO>> createBudget(@Valid @RequestBody BudgetDTO dto) {
        BudgetDTO result = budgetService.createBudget(dto);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(result, "Created budget successfully"));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('BUDGET_MANAGE') or hasAuthority('FINANCE_MANAGE') or hasRole('ROLE_ADMIN')")
    @Auditable(action = AuditAction.UPDATE, module = AuditModule.FINANCE, entityType = "Budget", description = "Updated Budget")
    @Operation(summary = "Update Budget")
    public ResponseEntity<ApiResponse<BudgetDTO>> updateBudget(@PathVariable String id, @Valid @RequestBody BudgetDTO dto) {
        BudgetDTO result = budgetService.updateBudget(id, dto);
        return ResponseEntity.ok(ApiResponse.success(result, "Updated budget successfully"));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('BUDGET_MANAGE') or hasAuthority('FINANCE_MANAGE') or hasRole('ROLE_ADMIN')")
    @Auditable(action = AuditAction.DELETE, module = AuditModule.FINANCE, entityType = "Budget", description = "Deleted Budget")
    @Operation(summary = "Delete Budget")
    public ResponseEntity<ApiResponse<Void>> deleteBudget(@PathVariable String id) {
        budgetService.deleteBudget(id);
        return ResponseEntity.ok(ApiResponse.success(null, "Deleted budget successfully"));
    }
}
