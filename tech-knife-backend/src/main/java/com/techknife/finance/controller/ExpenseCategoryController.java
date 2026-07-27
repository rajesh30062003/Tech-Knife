package com.techknife.finance.controller;

import com.techknife.audit.annotation.Auditable;
import com.techknife.audit.entity.AuditAction;
import com.techknife.audit.entity.AuditModule;
import com.techknife.backend.dto.ApiResponse;
import com.techknife.finance.dto.ExpenseCategoryDTO;
import com.techknife.finance.service.ExpenseCategoryService;
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
@RequestMapping("/api/v1/finance/expense-categories")
@RequiredArgsConstructor
@Tag(name = "Finance - Expense Categories", description = "Manage Expense Categories")
@SecurityRequirement(name = "bearerAuth")
public class ExpenseCategoryController {

    private final ExpenseCategoryService expenseCategoryService;

    @GetMapping
    @PreAuthorize("hasAuthority('FINANCE_VIEW') or hasAuthority('FINANCE_MANAGE') or hasRole('ROLE_ADMIN')")
    @Operation(summary = "Get All Expense Categories")
    public ResponseEntity<ApiResponse<List<ExpenseCategoryDTO>>> getAllCategories() {
        List<ExpenseCategoryDTO> result = expenseCategoryService.getAllCategories();
        return ResponseEntity.ok(ApiResponse.success(result, "Fetched expense categories successfully"));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('FINANCE_VIEW') or hasAuthority('FINANCE_MANAGE') or hasRole('ROLE_ADMIN')")
    @Operation(summary = "Get Expense Category by ID")
    public ResponseEntity<ApiResponse<ExpenseCategoryDTO>> getCategoryById(@PathVariable String id) {
        ExpenseCategoryDTO result = expenseCategoryService.getCategoryById(id);
        return ResponseEntity.ok(ApiResponse.success(result, "Fetched expense category successfully"));
    }

    @PostMapping
    @PreAuthorize("hasAuthority('FINANCE_MANAGE') or hasRole('ROLE_ADMIN')")
    @Auditable(action = AuditAction.CREATE, module = AuditModule.FINANCE, entityType = "ExpenseCategory", description = "Created Expense Category")
    @Operation(summary = "Create Expense Category")
    public ResponseEntity<ApiResponse<ExpenseCategoryDTO>> createCategory(@Valid @RequestBody ExpenseCategoryDTO dto) {
        ExpenseCategoryDTO result = expenseCategoryService.createCategory(dto);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(result, "Created expense category successfully"));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('FINANCE_MANAGE') or hasRole('ROLE_ADMIN')")
    @Auditable(action = AuditAction.UPDATE, module = AuditModule.FINANCE, entityType = "ExpenseCategory", description = "Updated Expense Category")
    @Operation(summary = "Update Expense Category")
    public ResponseEntity<ApiResponse<ExpenseCategoryDTO>> updateCategory(@PathVariable String id, @Valid @RequestBody ExpenseCategoryDTO dto) {
        ExpenseCategoryDTO result = expenseCategoryService.updateCategory(id, dto);
        return ResponseEntity.ok(ApiResponse.success(result, "Updated expense category successfully"));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('FINANCE_MANAGE') or hasRole('ROLE_ADMIN')")
    @Auditable(action = AuditAction.DELETE, module = AuditModule.FINANCE, entityType = "ExpenseCategory", description = "Deleted Expense Category")
    @Operation(summary = "Delete Expense Category")
    public ResponseEntity<ApiResponse<Void>> deleteCategory(@PathVariable String id) {
        expenseCategoryService.deleteCategory(id);
        return ResponseEntity.ok(ApiResponse.success(null, "Deleted expense category successfully"));
    }
}
