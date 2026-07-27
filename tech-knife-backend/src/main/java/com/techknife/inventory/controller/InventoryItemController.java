package com.techknife.inventory.controller;

import com.techknife.audit.annotation.Auditable;
import com.techknife.audit.entity.AuditAction;
import com.techknife.audit.entity.AuditModule;
import com.techknife.backend.dto.ApiResponse;
import com.techknife.inventory.dto.InventoryItemDTO;
import com.techknife.inventory.service.InventoryItemService;
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
@RequestMapping("/api/v1/inventory/items")
@RequiredArgsConstructor
@Tag(name = "Inventory - Items", description = "Inventory Item API")
@SecurityRequirement(name = "bearerAuth")
public class InventoryItemController {

    private final InventoryItemService itemService;

    @GetMapping
    @PreAuthorize("hasAuthority('INVENTORY_MANAGE') or hasRole('ROLE_ADMIN')")
    @Operation(summary = "Get All Inventory Items")
    public ResponseEntity<ApiResponse<List<InventoryItemDTO>>> getAllItems() {
        List<InventoryItemDTO> result = itemService.getAllItems();
        return ResponseEntity.ok(ApiResponse.success(result, "Fetched inventory items successfully"));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('INVENTORY_MANAGE') or hasRole('ROLE_ADMIN')")
    @Operation(summary = "Get Inventory Item by ID")
    public ResponseEntity<ApiResponse<InventoryItemDTO>> getItemById(@PathVariable String id) {
        InventoryItemDTO result = itemService.getItemById(id);
        return ResponseEntity.ok(ApiResponse.success(result, "Fetched inventory item successfully"));
    }

    @GetMapping("/code/{itemCode}")
    @PreAuthorize("hasAuthority('INVENTORY_MANAGE') or hasRole('ROLE_ADMIN')")
    @Operation(summary = "Get Inventory Item by Code")
    public ResponseEntity<ApiResponse<InventoryItemDTO>> getItemByCode(@PathVariable String itemCode) {
        InventoryItemDTO result = itemService.getItemByCode(itemCode);
        return ResponseEntity.ok(ApiResponse.success(result, "Fetched inventory item by code successfully"));
    }

    @PostMapping
    @PreAuthorize("hasAuthority('INVENTORY_MANAGE') or hasRole('ROLE_ADMIN')")
    @Auditable(action = AuditAction.CREATE, module = AuditModule.INVENTORY, entityType = "InventoryItem", description = "Created Inventory Item")
    @Operation(summary = "Create Inventory Item")
    public ResponseEntity<ApiResponse<InventoryItemDTO>> createItem(@Valid @RequestBody InventoryItemDTO dto) {
        InventoryItemDTO result = itemService.createItem(dto);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(result, "Created inventory item successfully"));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('INVENTORY_MANAGE') or hasRole('ROLE_ADMIN')")
    @Auditable(action = AuditAction.UPDATE, module = AuditModule.INVENTORY, entityType = "InventoryItem", description = "Updated Inventory Item")
    @Operation(summary = "Update Inventory Item")
    public ResponseEntity<ApiResponse<InventoryItemDTO>> updateItem(@PathVariable String id, @Valid @RequestBody InventoryItemDTO dto) {
        InventoryItemDTO result = itemService.updateItem(id, dto);
        return ResponseEntity.ok(ApiResponse.success(result, "Updated inventory item successfully"));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('INVENTORY_MANAGE') or hasRole('ROLE_ADMIN')")
    @Auditable(action = AuditAction.DELETE, module = AuditModule.INVENTORY, entityType = "InventoryItem", description = "Deleted Inventory Item")
    @Operation(summary = "Delete Inventory Item")
    public ResponseEntity<ApiResponse<Void>> deleteItem(@PathVariable String id) {
        itemService.deleteItem(id);
        return ResponseEntity.ok(ApiResponse.success(null, "Deleted inventory item successfully"));
    }
}
