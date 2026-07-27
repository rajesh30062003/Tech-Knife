package com.techknife.finance.controller;

import com.techknife.audit.annotation.Auditable;
import com.techknife.audit.entity.AuditAction;
import com.techknife.audit.entity.AuditModule;
import com.techknife.backend.dto.ApiResponse;
import com.techknife.finance.dto.CostCenterDTO;
import com.techknife.finance.service.CostCenterService;
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
@RequestMapping("/api/v1/finance/cost-centers")
@RequiredArgsConstructor
@Tag(name = "Finance - Cost Centers", description = "Manage Cost Centers across departments, projects, and branches")
@SecurityRequirement(name = "bearerAuth")
public class CostCenterController {

    private final CostCenterService costCenterService;

    @GetMapping
    @PreAuthorize("hasAuthority('FINANCE_VIEW') or hasAuthority('FINANCE_MANAGE') or hasRole('ROLE_ADMIN')")
    @Operation(summary = "Get All Cost Centers")
    public ResponseEntity<ApiResponse<List<CostCenterDTO>>> getAllCostCenters(@RequestParam(required = false) String type) {
        List<CostCenterDTO> result = type != null && !type.isBlank()
                ? costCenterService.getCostCentersByType(type)
                : costCenterService.getAllCostCenters();
        return ResponseEntity.ok(ApiResponse.success(result, "Fetched cost centers successfully"));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('FINANCE_VIEW') or hasAuthority('FINANCE_MANAGE') or hasRole('ROLE_ADMIN')")
    @Operation(summary = "Get Cost Center by ID")
    public ResponseEntity<ApiResponse<CostCenterDTO>> getCostCenterById(@PathVariable String id) {
        CostCenterDTO result = costCenterService.getCostCenterById(id);
        return ResponseEntity.ok(ApiResponse.success(result, "Fetched cost center successfully"));
    }

    @PostMapping
    @PreAuthorize("hasAuthority('FINANCE_MANAGE') or hasRole('ROLE_ADMIN')")
    @Auditable(action = AuditAction.CREATE, module = AuditModule.FINANCE, entityType = "CostCenter", description = "Created Cost Center")
    @Operation(summary = "Create Cost Center")
    public ResponseEntity<ApiResponse<CostCenterDTO>> createCostCenter(@Valid @RequestBody CostCenterDTO dto) {
        CostCenterDTO result = costCenterService.createCostCenter(dto);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(result, "Created cost center successfully"));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('FINANCE_MANAGE') or hasRole('ROLE_ADMIN')")
    @Auditable(action = AuditAction.UPDATE, module = AuditModule.FINANCE, entityType = "CostCenter", description = "Updated Cost Center")
    @Operation(summary = "Update Cost Center")
    public ResponseEntity<ApiResponse<CostCenterDTO>> updateCostCenter(@PathVariable String id, @Valid @RequestBody CostCenterDTO dto) {
        CostCenterDTO result = costCenterService.updateCostCenter(id, dto);
        return ResponseEntity.ok(ApiResponse.success(result, "Updated cost center successfully"));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('FINANCE_MANAGE') or hasRole('ROLE_ADMIN')")
    @Auditable(action = AuditAction.DELETE, module = AuditModule.FINANCE, entityType = "CostCenter", description = "Deleted Cost Center")
    @Operation(summary = "Delete Cost Center")
    public ResponseEntity<ApiResponse<Void>> deleteCostCenter(@PathVariable String id) {
        costCenterService.deleteCostCenter(id);
        return ResponseEntity.ok(ApiResponse.success(null, "Deleted cost center successfully"));
    }
}
