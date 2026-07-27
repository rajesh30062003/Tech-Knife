package com.techknife.asset.controller;

import com.techknife.asset.dto.AssetCategoryDTO;
import com.techknife.asset.service.AssetCategoryService;
import com.techknife.audit.annotation.Auditable;
import com.techknife.audit.entity.AuditAction;
import com.techknife.audit.entity.AuditModule;
import com.techknife.backend.dto.ApiResponse;
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
@RequestMapping("/api/v1/assets/categories")
@RequiredArgsConstructor
@Tag(name = "Asset - Categories", description = "Asset Category Management API")
@SecurityRequirement(name = "bearerAuth")
public class AssetCategoryController {

    private final AssetCategoryService categoryService;

    @GetMapping
    @PreAuthorize("hasAuthority('ASSET_VIEW') or hasRole('ROLE_ADMIN')")
    @Operation(summary = "Get All Asset Categories")
    public ResponseEntity<ApiResponse<List<AssetCategoryDTO>>> getAllCategories() {
        List<AssetCategoryDTO> result = categoryService.getAllCategories();
        return ResponseEntity.ok(ApiResponse.success(result, "Fetched asset categories successfully"));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('ASSET_VIEW') or hasRole('ROLE_ADMIN')")
    @Operation(summary = "Get Asset Category by ID")
    public ResponseEntity<ApiResponse<AssetCategoryDTO>> getCategoryById(@PathVariable String id) {
        AssetCategoryDTO result = categoryService.getCategoryById(id);
        return ResponseEntity.ok(ApiResponse.success(result, "Fetched asset category successfully"));
    }

    @PostMapping
    @PreAuthorize("hasAuthority('ASSET_CREATE') or hasRole('ROLE_ADMIN')")
    @Auditable(action = AuditAction.CREATE, module = AuditModule.ASSET, entityType = "AssetCategory", description = "Created Asset Category")
    @Operation(summary = "Create Asset Category")
    public ResponseEntity<ApiResponse<AssetCategoryDTO>> createCategory(@Valid @RequestBody AssetCategoryDTO dto) {
        AssetCategoryDTO result = categoryService.createCategory(dto);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(result, "Created asset category successfully"));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('ASSET_CREATE') or hasRole('ROLE_ADMIN')")
    @Auditable(action = AuditAction.UPDATE, module = AuditModule.ASSET, entityType = "AssetCategory", description = "Updated Asset Category")
    @Operation(summary = "Update Asset Category")
    public ResponseEntity<ApiResponse<AssetCategoryDTO>> updateCategory(@PathVariable String id, @Valid @RequestBody AssetCategoryDTO dto) {
        AssetCategoryDTO result = categoryService.updateCategory(id, dto);
        return ResponseEntity.ok(ApiResponse.success(result, "Updated asset category successfully"));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('ASSET_CREATE') or hasRole('ROLE_ADMIN')")
    @Auditable(action = AuditAction.DELETE, module = AuditModule.ASSET, entityType = "AssetCategory", description = "Deleted Asset Category")
    @Operation(summary = "Delete Asset Category")
    public ResponseEntity<ApiResponse<Void>> deleteCategory(@PathVariable String id) {
        categoryService.deleteCategory(id);
        return ResponseEntity.ok(ApiResponse.success(null, "Deleted asset category successfully"));
    }
}
