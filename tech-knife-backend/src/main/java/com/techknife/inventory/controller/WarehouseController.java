package com.techknife.inventory.controller;

import com.techknife.audit.annotation.Auditable;
import com.techknife.audit.entity.AuditAction;
import com.techknife.audit.entity.AuditModule;
import com.techknife.backend.dto.ApiResponse;
import com.techknife.inventory.dto.WarehouseDTO;
import com.techknife.inventory.service.WarehouseService;
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
@RequestMapping("/api/v1/inventory/warehouses")
@RequiredArgsConstructor
@Tag(name = "Inventory - Warehouses", description = "Warehouse Management API")
@SecurityRequirement(name = "bearerAuth")
public class WarehouseController {

    private final WarehouseService warehouseService;

    @GetMapping
    @PreAuthorize("hasAuthority('INVENTORY_MANAGE') or hasRole('ROLE_ADMIN')")
    @Operation(summary = "Get All Warehouses")
    public ResponseEntity<ApiResponse<List<WarehouseDTO>>> getAllWarehouses() {
        List<WarehouseDTO> result = warehouseService.getAllWarehouses();
        return ResponseEntity.ok(ApiResponse.success(result, "Fetched warehouses successfully"));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('INVENTORY_MANAGE') or hasRole('ROLE_ADMIN')")
    @Operation(summary = "Get Warehouse by ID")
    public ResponseEntity<ApiResponse<WarehouseDTO>> getWarehouseById(@PathVariable String id) {
        WarehouseDTO result = warehouseService.getWarehouseById(id);
        return ResponseEntity.ok(ApiResponse.success(result, "Fetched warehouse successfully"));
    }

    @PostMapping
    @PreAuthorize("hasAuthority('INVENTORY_MANAGE') or hasRole('ROLE_ADMIN')")
    @Auditable(action = AuditAction.CREATE, module = AuditModule.INVENTORY, entityType = "Warehouse", description = "Created Warehouse")
    @Operation(summary = "Create Warehouse")
    public ResponseEntity<ApiResponse<WarehouseDTO>> createWarehouse(@Valid @RequestBody WarehouseDTO dto) {
        WarehouseDTO result = warehouseService.createWarehouse(dto);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(result, "Created warehouse successfully"));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('INVENTORY_MANAGE') or hasRole('ROLE_ADMIN')")
    @Auditable(action = AuditAction.UPDATE, module = AuditModule.INVENTORY, entityType = "Warehouse", description = "Updated Warehouse")
    @Operation(summary = "Update Warehouse")
    public ResponseEntity<ApiResponse<WarehouseDTO>> updateWarehouse(@PathVariable String id, @Valid @RequestBody WarehouseDTO dto) {
        WarehouseDTO result = warehouseService.updateWarehouse(id, dto);
        return ResponseEntity.ok(ApiResponse.success(result, "Updated warehouse successfully"));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('INVENTORY_MANAGE') or hasRole('ROLE_ADMIN')")
    @Auditable(action = AuditAction.DELETE, module = AuditModule.INVENTORY, entityType = "Warehouse", description = "Deleted Warehouse")
    @Operation(summary = "Delete Warehouse")
    public ResponseEntity<ApiResponse<Void>> deleteWarehouse(@PathVariable String id) {
        warehouseService.deleteWarehouse(id);
        return ResponseEntity.ok(ApiResponse.success(null, "Deleted warehouse successfully"));
    }
}
