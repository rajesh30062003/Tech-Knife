package com.techknife.inventory.controller;

import com.techknife.audit.annotation.Auditable;
import com.techknife.audit.entity.AuditAction;
import com.techknife.audit.entity.AuditModule;
import com.techknife.backend.dto.ApiResponse;
import com.techknife.inventory.dto.InventoryCategoryDTO;
import com.techknife.inventory.service.InventoryCategoryService;
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
@RequestMapping("/api/v1/inventory/categories")
@RequiredArgsConstructor
@Tag(name = "Inventory - Categories", description = "Inventory Category API")
@SecurityRequirement(name = "bearerAuth")
public class InventoryCategoryController {

    private final InventoryCategoryService categoryService;

    @GetMapping
    @PreAuthorize("hasAuthority('INVENTORY_MANAGE') or hasRole('ROLE_ADMIN')")
    @Operation(summary = "Get All Inventory Categories")
    public ResponseEntity<ApiResponse<List<InventoryCategoryDTO>>> getAllCategories() {
        List<InventoryCategoryDTO> result = categoryService.getAllCategories();
        return ResponseEntity.ok(ApiResponse.success(result, "Fetched inventory categories successfully"));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('INVENTORY_MANAGE') or hasRole('ROLE_ADMIN')")
    @Operation(summary = "Get Inventory Category by ID")
    public ResponseEntity<ApiResponse<InventoryCategoryDTO>> getCategoryById(@PathVariable String id) {
        InventoryCategoryDTO result = categoryService.getCategoryById(id);
        return ResponseEntity.ok(ApiResponse.success(result, "Fetched inventory category successfully"));
    }

    @PostMapping
    @PreAuthorize("hasAuthority('INVENTORY_MANAGE') or hasRole('ROLE_ADMIN')")
    @Auditable(action = AuditAction.CREATE, module = AuditModule.INVENTORY, entityType = "InventoryCategory", description = "Created Inventory Category")
    @Operation(summary = "Create Inventory Category")
    public ResponseEntity<ApiResponse<InventoryCategoryDTO>> createCategory(@Valid @RequestBody InventoryCategoryDTO dto) {
        InventoryCategoryDTO result = categoryService.createCategory(dto);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(result, "Created inventory category successfully"));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('INVENTORY_MANAGE') or hasRole('ROLE_ADMIN')")
    @Auditable(action = AuditAction.UPDATE, module = AuditModule.INVENTORY, entityType = "InventoryCategory", description = "Updated Inventory Category")
    @Operation(summary = "Update Inventory Category")
    public ResponseEntity<ApiResponse<InventoryCategoryDTO>> updateCategory(@PathVariable String id, @Valid @RequestBody InventoryCategoryDTO dto) {
        InventoryCategoryDTO result = categoryService.updateCategory(id, dto);
        return ResponseEntity.ok(ApiResponse.success(result, "Updated inventory category successfully"));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('INVENTORY_MANAGE') or hasRole('ROLE_ADMIN')")
    @Auditable(action = AuditAction.DELETE, module = AuditModule.INVENTORY, entityType = "InventoryCategory", description = "Deleted Inventory Category")
    @Operation(summary = "Delete Inventory Category")
    public ResponseEntity<ApiResponse<Void>> deleteCategory(@PathVariable String id) {
        categoryService.deleteCategory(id);
        return ResponseEntity.ok(ApiResponse.success(null, "Deleted inventory category successfully"));
    }
}
