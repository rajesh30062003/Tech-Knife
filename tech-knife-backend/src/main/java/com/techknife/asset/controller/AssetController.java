package com.techknife.asset.controller;

import com.techknife.asset.dto.AssetDTO;
import com.techknife.asset.service.AssetService;
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
@RequestMapping("/api/v1/assets")
@RequiredArgsConstructor
@Tag(name = "Asset - Assets", description = "Asset Management API")
@SecurityRequirement(name = "bearerAuth")
public class AssetController {

    private final AssetService assetService;

    @GetMapping
    @PreAuthorize("hasAuthority('ASSET_VIEW') or hasRole('ROLE_ADMIN')")
    @Operation(summary = "Get All Assets")
    public ResponseEntity<ApiResponse<List<AssetDTO>>> getAllAssets(
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String employeeId) {
        if (status != null && !status.isBlank()) {
            return ResponseEntity.ok(ApiResponse.success(assetService.getAssetsByStatus(status), "Fetched assets by status"));
        }
        if (employeeId != null && !employeeId.isBlank()) {
            return ResponseEntity.ok(ApiResponse.success(assetService.getAssetsByEmployee(employeeId), "Fetched assets by employee"));
        }
        return ResponseEntity.ok(ApiResponse.success(assetService.getAllAssets(), "Fetched all assets successfully"));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('ASSET_VIEW') or hasRole('ROLE_ADMIN')")
    @Operation(summary = "Get Asset by ID")
    public ResponseEntity<ApiResponse<AssetDTO>> getAssetById(@PathVariable String id) {
        AssetDTO result = assetService.getAssetById(id);
        return ResponseEntity.ok(ApiResponse.success(result, "Fetched asset successfully"));
    }

    @GetMapping("/code/{assetCode}")
    @PreAuthorize("hasAuthority('ASSET_VIEW') or hasRole('ROLE_ADMIN')")
    @Operation(summary = "Get Asset by Code")
    public ResponseEntity<ApiResponse<AssetDTO>> getAssetByCode(@PathVariable String assetCode) {
        AssetDTO result = assetService.getAssetByCode(assetCode);
        return ResponseEntity.ok(ApiResponse.success(result, "Fetched asset by code successfully"));
    }

    @PostMapping
    @PreAuthorize("hasAuthority('ASSET_CREATE') or hasRole('ROLE_ADMIN')")
    @Auditable(action = AuditAction.CREATE, module = AuditModule.ASSET, entityType = "Asset", description = "Created Asset")
    @Operation(summary = "Create Asset")
    public ResponseEntity<ApiResponse<AssetDTO>> createAsset(@Valid @RequestBody AssetDTO dto) {
        AssetDTO result = assetService.createAsset(dto);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(result, "Created asset successfully"));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('ASSET_CREATE') or hasRole('ROLE_ADMIN')")
    @Auditable(action = AuditAction.UPDATE, module = AuditModule.ASSET, entityType = "Asset", description = "Updated Asset")
    @Operation(summary = "Update Asset")
    public ResponseEntity<ApiResponse<AssetDTO>> updateAsset(@PathVariable String id, @Valid @RequestBody AssetDTO dto) {
        AssetDTO result = assetService.updateAsset(id, dto);
        return ResponseEntity.ok(ApiResponse.success(result, "Updated asset successfully"));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('ASSET_CREATE') or hasRole('ROLE_ADMIN')")
    @Auditable(action = AuditAction.DELETE, module = AuditModule.ASSET, entityType = "Asset", description = "Deleted Asset")
    @Operation(summary = "Delete Asset")
    public ResponseEntity<ApiResponse<Void>> deleteAsset(@PathVariable String id) {
        assetService.deleteAsset(id);
        return ResponseEntity.ok(ApiResponse.success(null, "Deleted asset successfully"));
    }
}
