package com.techknife.finance.controller;

import com.techknife.audit.annotation.Auditable;
import com.techknife.audit.entity.AuditAction;
import com.techknife.audit.entity.AuditModule;
import com.techknife.backend.dto.ApiResponse;
import com.techknife.finance.dto.FinancialYearDTO;
import com.techknife.finance.service.FinancialYearService;
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
@RequestMapping("/api/v1/finance/financial-years")
@RequiredArgsConstructor
@Tag(name = "Finance - Financial Years", description = "Manage Financial Year Lifecycles (Create, Open, Close, Lock, Archive)")
@SecurityRequirement(name = "bearerAuth")
public class FinancialYearController {

    private final FinancialYearService financialYearService;

    @GetMapping
    @PreAuthorize("hasAuthority('FINANCE_VIEW') or hasAuthority('FINANCE_MANAGE') or hasRole('ROLE_ADMIN')")
    @Operation(summary = "Get All Financial Years")
    public ResponseEntity<ApiResponse<List<FinancialYearDTO>>> getAllFinancialYears() {
        List<FinancialYearDTO> result = financialYearService.getAllFinancialYears();
        return ResponseEntity.ok(ApiResponse.success(result, "Fetched financial years successfully"));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('FINANCE_VIEW') or hasAuthority('FINANCE_MANAGE') or hasRole('ROLE_ADMIN')")
    @Operation(summary = "Get Financial Year by ID")
    public ResponseEntity<ApiResponse<FinancialYearDTO>> getFinancialYearById(@PathVariable String id) {
        FinancialYearDTO result = financialYearService.getFinancialYearById(id);
        return ResponseEntity.ok(ApiResponse.success(result, "Fetched financial year successfully"));
    }

    @PostMapping
    @PreAuthorize("hasAuthority('FINANCE_MANAGE') or hasRole('ROLE_ADMIN')")
    @Auditable(action = AuditAction.CREATE, module = AuditModule.FINANCE, entityType = "FinancialYear", description = "Created Financial Year")
    @Operation(summary = "Create Financial Year")
    public ResponseEntity<ApiResponse<FinancialYearDTO>> createFinancialYear(@Valid @RequestBody FinancialYearDTO dto) {
        FinancialYearDTO result = financialYearService.createFinancialYear(dto);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(result, "Created financial year successfully"));
    }

    @PostMapping("/{id}/close")
    @PreAuthorize("hasAuthority('FINANCE_MANAGE') or hasRole('ROLE_ADMIN')")
    @Auditable(action = AuditAction.CLOSE, module = AuditModule.FINANCE, entityType = "FinancialYear", description = "Closed Financial Year")
    @Operation(summary = "Close Financial Year")
    public ResponseEntity<ApiResponse<FinancialYearDTO>> closeFinancialYear(@PathVariable String id) {
        FinancialYearDTO result = financialYearService.updateFinancialYearStatus(id, "CLOSED", true);
        return ResponseEntity.ok(ApiResponse.success(result, "Closed financial year successfully"));
    }

    @PostMapping("/{id}/lock")
    @PreAuthorize("hasAuthority('FINANCE_MANAGE') or hasRole('ROLE_ADMIN')")
    @Auditable(action = AuditAction.LOCK, module = AuditModule.FINANCE, entityType = "FinancialYear", description = "Locked Financial Year")
    @Operation(summary = "Lock Financial Year")
    public ResponseEntity<ApiResponse<FinancialYearDTO>> lockFinancialYear(@PathVariable String id) {
        FinancialYearDTO result = financialYearService.updateFinancialYearStatus(id, "LOCKED", true);
        return ResponseEntity.ok(ApiResponse.success(result, "Locked financial year successfully"));
    }
}
