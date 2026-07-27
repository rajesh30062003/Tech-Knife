package com.techknife.asset.controller;

import com.techknife.asset.dto.AssetWarrantyDTO;
import com.techknife.asset.service.AssetWarrantyService;
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
@RequestMapping("/api/v1/assets/warranties")
@RequiredArgsConstructor
@Tag(name = "Asset - Warranties", description = "Asset Warranty Management API")
@SecurityRequirement(name = "bearerAuth")
public class AssetWarrantyController {

    private final AssetWarrantyService warrantyService;

    @GetMapping
    @PreAuthorize("hasAuthority('ASSET_VIEW') or hasRole('ROLE_ADMIN')")
    @Operation(summary = "Get All Asset Warranties")
    public ResponseEntity<ApiResponse<List<AssetWarrantyDTO>>> getAllWarranties() {
        List<AssetWarrantyDTO> result = warrantyService.getAllWarranties();
        return ResponseEntity.ok(ApiResponse.success(result, "Fetched all warranties successfully"));
    }

    @GetMapping("/asset/{assetId}")
    @PreAuthorize("hasAuthority('ASSET_VIEW') or hasRole('ROLE_ADMIN')")
    @Operation(summary = "Get Asset Warranty by Asset")
    public ResponseEntity<ApiResponse<AssetWarrantyDTO>> getWarrantyByAsset(@PathVariable String assetId) {
        AssetWarrantyDTO result = warrantyService.getWarrantyByAsset(assetId);
        return ResponseEntity.ok(ApiResponse.success(result, "Fetched warranty by asset"));
    }

    @PostMapping
    @PreAuthorize("hasAuthority('ASSET_CREATE') or hasRole('ROLE_ADMIN')")
    @Auditable(action = AuditAction.CREATE, module = AuditModule.ASSET, entityType = "AssetWarranty", description = "Created Asset Warranty")
    @Operation(summary = "Create Asset Warranty")
    public ResponseEntity<ApiResponse<AssetWarrantyDTO>> createWarranty(@Valid @RequestBody AssetWarrantyDTO dto) {
        AssetWarrantyDTO result = warrantyService.createWarranty(dto);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(result, "Created asset warranty successfully"));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('ASSET_CREATE') or hasRole('ROLE_ADMIN')")
    @Auditable(action = AuditAction.UPDATE, module = AuditModule.ASSET, entityType = "AssetWarranty", description = "Updated Asset Warranty")
    @Operation(summary = "Update Asset Warranty")
    public ResponseEntity<ApiResponse<AssetWarrantyDTO>> updateWarranty(@PathVariable String id, @Valid @RequestBody AssetWarrantyDTO dto) {
        AssetWarrantyDTO result = warrantyService.updateWarranty(id, dto);
        return ResponseEntity.ok(ApiResponse.success(result, "Updated asset warranty successfully"));
    }
}
