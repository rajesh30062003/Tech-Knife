package com.techknife.finance.controller;

import com.techknife.audit.annotation.Auditable;
import com.techknife.audit.entity.AuditAction;
import com.techknife.audit.entity.AuditModule;
import com.techknife.backend.dto.ApiResponse;
import com.techknife.finance.dto.AccountGroupDTO;
import com.techknife.finance.service.AccountGroupService;
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
@RequestMapping("/api/v1/finance/account-groups")
@RequiredArgsConstructor
@Tag(name = "Finance - Account Groups", description = "Manage Account Groups for Chart of Accounts")
@SecurityRequirement(name = "bearerAuth")
public class AccountGroupController {

    private final AccountGroupService accountGroupService;

    @GetMapping
    @PreAuthorize("hasAuthority('FINANCE_VIEW') or hasAuthority('FINANCE_MANAGE') or hasRole('ROLE_ADMIN')")
    @Operation(summary = "Get All Account Groups")
    public ResponseEntity<ApiResponse<List<AccountGroupDTO>>> getAllAccountGroups(@RequestParam(required = false) String accountType) {
        List<AccountGroupDTO> result = accountType != null && !accountType.isBlank()
                ? accountGroupService.getAccountGroupsByType(accountType)
                : accountGroupService.getAllAccountGroups();
        return ResponseEntity.ok(ApiResponse.success(result, "Fetched account groups successfully"));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('FINANCE_VIEW') or hasAuthority('FINANCE_MANAGE') or hasRole('ROLE_ADMIN')")
    @Operation(summary = "Get Account Group by ID")
    public ResponseEntity<ApiResponse<AccountGroupDTO>> getAccountGroupById(@PathVariable String id) {
        AccountGroupDTO result = accountGroupService.getAccountGroupById(id);
        return ResponseEntity.ok(ApiResponse.success(result, "Fetched account group successfully"));
    }

    @PostMapping
    @PreAuthorize("hasAuthority('FINANCE_MANAGE') or hasRole('ROLE_ADMIN')")
    @Auditable(action = AuditAction.CREATE, module = AuditModule.FINANCE, entityType = "AccountGroup", description = "Created Account Group")
    @Operation(summary = "Create Account Group")
    public ResponseEntity<ApiResponse<AccountGroupDTO>> createAccountGroup(@Valid @RequestBody AccountGroupDTO dto) {
        AccountGroupDTO result = accountGroupService.createAccountGroup(dto);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(result, "Created account group successfully"));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('FINANCE_MANAGE') or hasRole('ROLE_ADMIN')")
    @Auditable(action = AuditAction.UPDATE, module = AuditModule.FINANCE, entityType = "AccountGroup", description = "Updated Account Group")
    @Operation(summary = "Update Account Group")
    public ResponseEntity<ApiResponse<AccountGroupDTO>> updateAccountGroup(@PathVariable String id, @Valid @RequestBody AccountGroupDTO dto) {
        AccountGroupDTO result = accountGroupService.updateAccountGroup(id, dto);
        return ResponseEntity.ok(ApiResponse.success(result, "Updated account group successfully"));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('FINANCE_MANAGE') or hasRole('ROLE_ADMIN')")
    @Auditable(action = AuditAction.DELETE, module = AuditModule.FINANCE, entityType = "AccountGroup", description = "Deleted Account Group")
    @Operation(summary = "Delete Account Group")
    public ResponseEntity<ApiResponse<Void>> deleteAccountGroup(@PathVariable String id) {
        accountGroupService.deleteAccountGroup(id);
        return ResponseEntity.ok(ApiResponse.success(null, "Deleted account group successfully"));
    }
}
