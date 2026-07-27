package com.techknife.procurement.controller;

import com.techknife.audit.annotation.Auditable;
import com.techknife.audit.entity.AuditAction;
import com.techknife.audit.entity.AuditModule;
import com.techknife.backend.dto.ApiResponse;
import com.techknife.procurement.dto.SupplierDTO;
import com.techknife.procurement.service.SupplierService;
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
@RequestMapping("/api/v1/procurement/suppliers")
@RequiredArgsConstructor
@Tag(name = "Procurement - Suppliers", description = "Supplier Management API")
@SecurityRequirement(name = "bearerAuth")
public class SupplierController {

    private final SupplierService supplierService;

    @GetMapping
    @PreAuthorize("hasAuthority('PURCHASE_REQUEST_CREATE') or hasRole('ROLE_ADMIN')")
    @Operation(summary = "Get All Suppliers")
    public ResponseEntity<ApiResponse<List<SupplierDTO>>> getAllSuppliers() {
        List<SupplierDTO> result = supplierService.getAllSuppliers();
        return ResponseEntity.ok(ApiResponse.success(result, "Fetched suppliers successfully"));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('PURCHASE_REQUEST_CREATE') or hasRole('ROLE_ADMIN')")
    @Operation(summary = "Get Supplier by ID")
    public ResponseEntity<ApiResponse<SupplierDTO>> getSupplierById(@PathVariable String id) {
        SupplierDTO result = supplierService.getSupplierById(id);
        return ResponseEntity.ok(ApiResponse.success(result, "Fetched supplier successfully"));
    }

    @PostMapping
    @PreAuthorize("hasAuthority('PURCHASE_REQUEST_CREATE') or hasRole('ROLE_ADMIN')")
    @Auditable(action = AuditAction.CREATE, module = AuditModule.PROCUREMENT, entityType = "Supplier", description = "Created Supplier")
    @Operation(summary = "Create Supplier")
    public ResponseEntity<ApiResponse<SupplierDTO>> createSupplier(@Valid @RequestBody SupplierDTO dto) {
        SupplierDTO result = supplierService.createSupplier(dto);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(result, "Created supplier successfully"));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('PURCHASE_REQUEST_CREATE') or hasRole('ROLE_ADMIN')")
    @Auditable(action = AuditAction.UPDATE, module = AuditModule.PROCUREMENT, entityType = "Supplier", description = "Updated Supplier")
    @Operation(summary = "Update Supplier")
    public ResponseEntity<ApiResponse<SupplierDTO>> updateSupplier(@PathVariable String id, @Valid @RequestBody SupplierDTO dto) {
        SupplierDTO result = supplierService.updateSupplier(id, dto);
        return ResponseEntity.ok(ApiResponse.success(result, "Updated supplier successfully"));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('PURCHASE_REQUEST_CREATE') or hasRole('ROLE_ADMIN')")
    @Auditable(action = AuditAction.DELETE, module = AuditModule.PROCUREMENT, entityType = "Supplier", description = "Deleted Supplier")
    @Operation(summary = "Delete Supplier")
    public ResponseEntity<ApiResponse<Void>> deleteSupplier(@PathVariable String id) {
        supplierService.deleteSupplier(id);
        return ResponseEntity.ok(ApiResponse.success(null, "Deleted supplier successfully"));
    }
}
