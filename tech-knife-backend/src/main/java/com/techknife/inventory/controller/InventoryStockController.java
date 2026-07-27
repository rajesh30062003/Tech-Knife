package com.techknife.inventory.controller;

import com.techknife.audit.annotation.Auditable;
import com.techknife.audit.entity.AuditAction;
import com.techknife.audit.entity.AuditModule;
import com.techknife.backend.dto.ApiResponse;
import com.techknife.inventory.dto.InventoryStockDTO;
import com.techknife.inventory.dto.StockAdjustmentRequest;
import com.techknife.inventory.service.InventoryStockService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/inventory/stocks")
@RequiredArgsConstructor
@Tag(name = "Inventory - Stocks", description = "Inventory Stock API")
@SecurityRequirement(name = "bearerAuth")
public class InventoryStockController {

    private final InventoryStockService stockService;

    @GetMapping
    @PreAuthorize("hasAuthority('INVENTORY_MANAGE') or hasRole('ROLE_ADMIN')")
    @Operation(summary = "Get All Stocks")
    public ResponseEntity<ApiResponse<List<InventoryStockDTO>>> getAllStocks() {
        List<InventoryStockDTO> result = stockService.getAllStocks();
        return ResponseEntity.ok(ApiResponse.success(result, "Fetched all stock balances successfully"));
    }

    @GetMapping("/item/{itemId}")
    @PreAuthorize("hasAuthority('INVENTORY_MANAGE') or hasRole('ROLE_ADMIN')")
    @Operation(summary = "Get Stocks by Item")
    public ResponseEntity<ApiResponse<List<InventoryStockDTO>>> getStocksByItem(@PathVariable String itemId) {
        List<InventoryStockDTO> result = stockService.getStocksByItem(itemId);
        return ResponseEntity.ok(ApiResponse.success(result, "Fetched stock balances by item"));
    }

    @GetMapping("/warehouse/{warehouseId}")
    @PreAuthorize("hasAuthority('INVENTORY_MANAGE') or hasRole('ROLE_ADMIN')")
    @Operation(summary = "Get Stocks by Warehouse")
    public ResponseEntity<ApiResponse<List<InventoryStockDTO>>> getStocksByWarehouse(@PathVariable String warehouseId) {
        List<InventoryStockDTO> result = stockService.getStocksByWarehouse(warehouseId);
        return ResponseEntity.ok(ApiResponse.success(result, "Fetched stock balances by warehouse"));
    }

    @PostMapping("/adjust")
    @PreAuthorize("hasAuthority('INVENTORY_MANAGE') or hasRole('ROLE_ADMIN')")
    @Auditable(action = AuditAction.UPDATE, module = AuditModule.INVENTORY, entityType = "InventoryStock", description = "Adjusted Inventory Stock")
    @Operation(summary = "Adjust Inventory Stock Quantity")
    public ResponseEntity<ApiResponse<InventoryStockDTO>> adjustStock(@Valid @RequestBody StockAdjustmentRequest request) {
        InventoryStockDTO result = stockService.adjustStock(request);
        return ResponseEntity.ok(ApiResponse.success(result, "Adjusted inventory stock successfully"));
    }
}
