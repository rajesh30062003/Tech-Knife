package com.techknife.asset.controller;

import com.techknife.asset.service.AssetDashboardService;
import com.techknife.backend.dto.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/assets/dashboard")
@RequiredArgsConstructor
@Tag(name = "Asset - Dashboard", description = "Asset, Inventory & Procurement Dashboard API")
@SecurityRequirement(name = "bearerAuth")
public class AssetDashboardController {

    private final AssetDashboardService dashboardService;

    @GetMapping
    @PreAuthorize("hasAuthority('ASSET_VIEW') or hasAuthority('INVENTORY_MANAGE') or hasRole('ROLE_ADMIN')")
    @Operation(summary = "Get Dashboard Summary Metrics")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getDashboardMetrics() {
        Map<String, Object> result = dashboardService.getDashboardMetrics();
        return ResponseEntity.ok(ApiResponse.success(result, "Fetched dashboard metrics successfully"));
    }
}
