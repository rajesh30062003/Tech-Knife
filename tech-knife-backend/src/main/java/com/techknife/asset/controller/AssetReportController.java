package com.techknife.asset.controller;

import com.techknife.asset.service.AssetReportService;
import com.techknife.backend.dto.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/assets/reports")
@RequiredArgsConstructor
@Tag(name = "Asset - Reports", description = "Asset & Procurement Reports API")
@SecurityRequirement(name = "bearerAuth")
public class AssetReportController {

    private final AssetReportService reportService;

    @GetMapping("/register")
    @PreAuthorize("hasAuthority('ASSET_VIEW') or hasRole('ROLE_ADMIN')")
    @Operation(summary = "Asset Register Report")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getAssetRegisterReport() {
        return ResponseEntity.ok(ApiResponse.success(reportService.getAssetRegisterReport(), "Fetched asset register report"));
    }

    @GetMapping("/assigned")
    @PreAuthorize("hasAuthority('ASSET_VIEW') or hasRole('ROLE_ADMIN')")
    @Operation(summary = "Assigned Assets Report")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getAssignedAssetsReport() {
        return ResponseEntity.ok(ApiResponse.success(reportService.getAssignedAssetsReport(), "Fetched assigned assets report"));
    }

    @GetMapping("/utilization")
    @PreAuthorize("hasAuthority('ASSET_VIEW') or hasRole('ROLE_ADMIN')")
    @Operation(summary = "Asset Utilization Report")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getAssetUtilizationReport() {
        return ResponseEntity.ok(ApiResponse.success(reportService.getAssetUtilizationReport(), "Fetched asset utilization report"));
    }

    @GetMapping("/warranty-expiry")
    @PreAuthorize("hasAuthority('ASSET_VIEW') or hasRole('ROLE_ADMIN')")
    @Operation(summary = "Warranty Expiry Report")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getWarrantyExpiryReport() {
        return ResponseEntity.ok(ApiResponse.success(reportService.getWarrantyExpiryReport(), "Fetched warranty expiry report"));
    }

    @GetMapping("/maintenance")
    @PreAuthorize("hasAuthority('ASSET_VIEW') or hasRole('ROLE_ADMIN')")
    @Operation(summary = "Maintenance Report")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getMaintenanceReport() {
        return ResponseEntity.ok(ApiResponse.success(reportService.getMaintenanceReport(), "Fetched maintenance report"));
    }

    @GetMapping("/license-expiry")
    @PreAuthorize("hasAuthority('LICENSE_MANAGE') or hasAuthority('ASSET_VIEW') or hasRole('ROLE_ADMIN')")
    @Operation(summary = "License Expiry Report")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getLicenseExpiryReport() {
        return ResponseEntity.ok(ApiResponse.success(reportService.getLicenseExpiryReport(), "Fetched license expiry report"));
    }

    @GetMapping("/inventory-stock")
    @PreAuthorize("hasAuthority('INVENTORY_MANAGE') or hasRole('ROLE_ADMIN')")
    @Operation(summary = "Inventory Stock Report")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getInventoryStockReport() {
        return ResponseEntity.ok(ApiResponse.success(reportService.getInventoryStockReport(), "Fetched inventory stock report"));
    }

    @GetMapping("/low-stock")
    @PreAuthorize("hasAuthority('INVENTORY_MANAGE') or hasRole('ROLE_ADMIN')")
    @Operation(summary = "Low Stock Report")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getLowStockReport() {
        return ResponseEntity.ok(ApiResponse.success(reportService.getLowStockReport(), "Fetched low stock report"));
    }

    @GetMapping("/purchases")
    @PreAuthorize("hasAuthority('PURCHASE_REQUEST_CREATE') or hasRole('ROLE_ADMIN')")
    @Operation(summary = "Purchases Report")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getPurchaseReport() {
        return ResponseEntity.ok(ApiResponse.success(reportService.getPurchaseReport(), "Fetched purchase report"));
    }

    @GetMapping("/suppliers")
    @PreAuthorize("hasAuthority('PURCHASE_REQUEST_CREATE') or hasRole('ROLE_ADMIN')")
    @Operation(summary = "Suppliers Report")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getSupplierReport() {
        return ResponseEntity.ok(ApiResponse.success(reportService.getSupplierReport(), "Fetched supplier report"));
    }
}
