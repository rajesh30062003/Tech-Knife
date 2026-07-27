package com.techknife.asset.controller;

import com.techknife.asset.dto.AssetMaintenanceDTO;
import com.techknife.asset.dto.MaintenanceScheduleDTO;
import com.techknife.asset.service.AssetMaintenanceService;
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
@RequestMapping("/api/v1/assets/maintenances")
@RequiredArgsConstructor
@Tag(name = "Asset - Maintenance", description = "Asset Maintenance API")
@SecurityRequirement(name = "bearerAuth")
public class AssetMaintenanceController {

    private final AssetMaintenanceService maintenanceService;

    @GetMapping
    @PreAuthorize("hasAuthority('ASSET_VIEW') or hasRole('ROLE_ADMIN')")
    @Operation(summary = "Get All Maintenances")
    public ResponseEntity<ApiResponse<List<AssetMaintenanceDTO>>> getAllMaintenances() {
        List<AssetMaintenanceDTO> result = maintenanceService.getAllMaintenances();
        return ResponseEntity.ok(ApiResponse.success(result, "Fetched all maintenance records"));
    }

    @GetMapping("/asset/{assetId}")
    @PreAuthorize("hasAuthority('ASSET_VIEW') or hasRole('ROLE_ADMIN')")
    @Operation(summary = "Get Maintenances by Asset")
    public ResponseEntity<ApiResponse<List<AssetMaintenanceDTO>>> getMaintenancesByAsset(@PathVariable String assetId) {
        List<AssetMaintenanceDTO> result = maintenanceService.getMaintenancesByAsset(assetId);
        return ResponseEntity.ok(ApiResponse.success(result, "Fetched maintenance records by asset"));
    }

    @PostMapping
    @PreAuthorize("hasAuthority('ASSET_CREATE') or hasRole('ROLE_ADMIN')")
    @Auditable(action = AuditAction.CREATE, module = AuditModule.ASSET, entityType = "AssetMaintenance", description = "Scheduled Asset Maintenance")
    @Operation(summary = "Schedule Asset Maintenance")
    public ResponseEntity<ApiResponse<AssetMaintenanceDTO>> scheduleMaintenance(@Valid @RequestBody AssetMaintenanceDTO dto) {
        AssetMaintenanceDTO result = maintenanceService.scheduleMaintenance(dto);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(result, "Scheduled maintenance successfully"));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('ASSET_CREATE') or hasRole('ROLE_ADMIN')")
    @Auditable(action = AuditAction.UPDATE, module = AuditModule.ASSET, entityType = "AssetMaintenance", description = "Updated Asset Maintenance")
    @Operation(summary = "Update Asset Maintenance")
    public ResponseEntity<ApiResponse<AssetMaintenanceDTO>> updateMaintenance(@PathVariable String id, @Valid @RequestBody AssetMaintenanceDTO dto) {
        AssetMaintenanceDTO result = maintenanceService.updateMaintenance(id, dto);
        return ResponseEntity.ok(ApiResponse.success(result, "Updated maintenance record successfully"));
    }

    @GetMapping("/schedules")
    @PreAuthorize("hasAuthority('ASSET_VIEW') or hasRole('ROLE_ADMIN')")
    @Operation(summary = "Get All Maintenance Schedules")
    public ResponseEntity<ApiResponse<List<MaintenanceScheduleDTO>>> getAllSchedules() {
        List<MaintenanceScheduleDTO> result = maintenanceService.getAllMaintenanceSchedules();
        return ResponseEntity.ok(ApiResponse.success(result, "Fetched all maintenance schedules"));
    }

    @PostMapping("/schedules")
    @PreAuthorize("hasAuthority('ASSET_CREATE') or hasRole('ROLE_ADMIN')")
    @Auditable(action = AuditAction.CREATE, module = AuditModule.ASSET, entityType = "MaintenanceSchedule", description = "Created Maintenance Schedule")
    @Operation(summary = "Create Maintenance Schedule")
    public ResponseEntity<ApiResponse<MaintenanceScheduleDTO>> createSchedule(@Valid @RequestBody MaintenanceScheduleDTO dto) {
        MaintenanceScheduleDTO result = maintenanceService.createMaintenanceSchedule(dto);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(result, "Created maintenance schedule successfully"));
    }
}
