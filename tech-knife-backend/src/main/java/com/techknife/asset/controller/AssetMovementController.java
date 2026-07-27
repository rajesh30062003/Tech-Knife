package com.techknife.asset.controller;

import com.techknife.asset.dto.AssetMovementDTO;
import com.techknife.asset.service.AssetMovementService;
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
@RequestMapping("/api/v1/assets/movements")
@RequiredArgsConstructor
@Tag(name = "Asset - Movements", description = "Asset Movement API")
@SecurityRequirement(name = "bearerAuth")
public class AssetMovementController {

    private final AssetMovementService movementService;

    @GetMapping
    @PreAuthorize("hasAuthority('ASSET_VIEW') or hasRole('ROLE_ADMIN')")
    @Operation(summary = "Get All Asset Movements")
    public ResponseEntity<ApiResponse<List<AssetMovementDTO>>> getAllMovements() {
        List<AssetMovementDTO> result = movementService.getAllMovements();
        return ResponseEntity.ok(ApiResponse.success(result, "Fetched all movement records"));
    }

    @GetMapping("/asset/{assetId}")
    @PreAuthorize("hasAuthority('ASSET_VIEW') or hasRole('ROLE_ADMIN')")
    @Operation(summary = "Get Asset Movements by Asset")
    public ResponseEntity<ApiResponse<List<AssetMovementDTO>>> getMovementsByAsset(@PathVariable String assetId) {
        List<AssetMovementDTO> result = movementService.getMovementsByAsset(assetId);
        return ResponseEntity.ok(ApiResponse.success(result, "Fetched movement records by asset"));
    }

    @PostMapping
    @PreAuthorize("hasAuthority('ASSET_TRANSFER') or hasRole('ROLE_ADMIN')")
    @Auditable(action = AuditAction.CREATE, module = AuditModule.ASSET, entityType = "AssetMovement", description = "Recorded Asset Movement")
    @Operation(summary = "Record Asset Movement")
    public ResponseEntity<ApiResponse<AssetMovementDTO>> recordMovement(@Valid @RequestBody AssetMovementDTO dto) {
        AssetMovementDTO result = movementService.recordMovement(dto);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(result, "Recorded asset movement successfully"));
    }
}
