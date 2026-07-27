package com.techknife.finance.controller;

import com.techknife.audit.annotation.Auditable;
import com.techknife.audit.entity.AuditAction;
import com.techknife.audit.entity.AuditModule;
import com.techknife.backend.dto.ApiResponse;
import com.techknife.finance.dto.ChartOfAccountDTO;
import com.techknife.finance.service.ChartOfAccountService;
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
@RequestMapping("/api/v1/finance/chart-of-accounts")
@RequiredArgsConstructor
@Tag(name = "Finance - Chart of Accounts", description = "Manage Chart of Accounts")
@SecurityRequirement(name = "bearerAuth")
public class ChartOfAccountController {

    private final ChartOfAccountService chartOfAccountService;

    @GetMapping
    @PreAuthorize("hasAuthority('FINANCE_VIEW') or hasAuthority('FINANCE_MANAGE') or hasRole('ROLE_ADMIN')")
    @Operation(summary = "Get All Chart of Accounts")
    public ResponseEntity<ApiResponse<List<ChartOfAccountDTO>>> getAllAccounts(@RequestParam(required = false) String accountType) {
        List<ChartOfAccountDTO> result = accountType != null && !accountType.isBlank()
                ? chartOfAccountService.getAccountsByType(accountType)
                : chartOfAccountService.getAllAccounts();
        return ResponseEntity.ok(ApiResponse.success(result, "Fetched chart of accounts successfully"));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('FINANCE_VIEW') or hasAuthority('FINANCE_MANAGE') or hasRole('ROLE_ADMIN')")
    @Operation(summary = "Get Account by ID")
    public ResponseEntity<ApiResponse<ChartOfAccountDTO>> getAccountById(@PathVariable String id) {
        ChartOfAccountDTO result = chartOfAccountService.getAccountById(id);
        return ResponseEntity.ok(ApiResponse.success(result, "Fetched account successfully"));
    }

    @PostMapping
    @PreAuthorize("hasAuthority('FINANCE_MANAGE') or hasRole('ROLE_ADMIN')")
    @Auditable(action = AuditAction.CREATE, module = AuditModule.FINANCE, entityType = "ChartOfAccount", description = "Created Account")
    @Operation(summary = "Create Chart of Account")
    public ResponseEntity<ApiResponse<ChartOfAccountDTO>> createAccount(@Valid @RequestBody ChartOfAccountDTO dto) {
        ChartOfAccountDTO result = chartOfAccountService.createAccount(dto);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(result, "Created account successfully"));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('FINANCE_MANAGE') or hasRole('ROLE_ADMIN')")
    @Auditable(action = AuditAction.UPDATE, module = AuditModule.FINANCE, entityType = "ChartOfAccount", description = "Updated Account")
    @Operation(summary = "Update Chart of Account")
    public ResponseEntity<ApiResponse<ChartOfAccountDTO>> updateAccount(@PathVariable String id, @Valid @RequestBody ChartOfAccountDTO dto) {
        ChartOfAccountDTO result = chartOfAccountService.updateAccount(id, dto);
        return ResponseEntity.ok(ApiResponse.success(result, "Updated account successfully"));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('FINANCE_MANAGE') or hasRole('ROLE_ADMIN')")
    @Auditable(action = AuditAction.DELETE, module = AuditModule.FINANCE, entityType = "ChartOfAccount", description = "Deleted Account")
    @Operation(summary = "Delete Chart of Account")
    public ResponseEntity<ApiResponse<Void>> deleteAccount(@PathVariable String id) {
        chartOfAccountService.deleteAccount(id);
        return ResponseEntity.ok(ApiResponse.success(null, "Deleted account successfully"));
    }
}
