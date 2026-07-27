package com.techknife.asset.controller;

import com.techknife.asset.service.AssetSearchService;
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
@RequestMapping("/api/v1/assets/search")
@RequiredArgsConstructor
@Tag(name = "Asset - Search", description = "Global Asset & Inventory Search API")
@SecurityRequirement(name = "bearerAuth")
public class AssetSearchController {

    private final AssetSearchService searchService;

    @GetMapping
    @PreAuthorize("hasAuthority('ASSET_VIEW') or hasAuthority('INVENTORY_MANAGE') or hasRole('ROLE_ADMIN')")
    @Operation(summary = "Global Search Assets, Licenses, Inventory, Warehouses, Suppliers")
    public ResponseEntity<ApiResponse<Map<String, Object>>> globalSearch(@RequestParam String q) {
        Map<String, Object> result = searchService.globalSearch(q);
        return ResponseEntity.ok(ApiResponse.success(result, "Search completed successfully"));
    }
}
