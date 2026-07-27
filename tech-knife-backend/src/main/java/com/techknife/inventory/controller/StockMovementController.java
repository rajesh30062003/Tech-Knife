package com.techknife.inventory.controller;

import com.techknife.audit.annotation.Auditable;
import com.techknife.audit.entity.AuditAction;
import com.techknife.audit.entity.AuditModule;
import com.techknife.backend.dto.ApiResponse;
import com.techknife.inventory.dto.StockMovementDTO;
import com.techknife.inventory.service.StockMovementService;
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
@RequestMapping("/api/v1/inventory/movements")
@RequiredArgsConstructor
@Tag(name = "Inventory - Movements", description = "Stock Movement API")
@SecurityRequirement(name = "bearerAuth")
public class StockMovementController {

    private final StockMovementService movementService;

    @GetMapping
    @PreAuthorize("hasAuthority('INVENTORY_MANAGE') or hasRole('ROLE_ADMIN')")
    @Operation(summary = "Get All Stock Movements")
    public ResponseEntity<ApiResponse<List<StockMovementDTO>>> getAllMovements() {
        List<StockMovementDTO> result = movementService.getAllMovements();
        return ResponseEntity.ok(ApiResponse.success(result, "Fetched all stock movements successfully"));
    }

    @GetMapping("/item/{itemId}")
    @PreAuthorize("hasAuthority('INVENTORY_MANAGE') or hasRole('ROLE_ADMIN')")
    @Operation(summary = "Get Stock Movements by Item")
    public ResponseEntity<ApiResponse<List<StockMovementDTO>>> getMovementsByItem(@PathVariable String itemId) {
        List<StockMovementDTO> result = movementService.getMovementsByItem(itemId);
        return ResponseEntity.ok(ApiResponse.success(result, "Fetched stock movements by item"));
    }

    @PostMapping
    @PreAuthorize("hasAuthority('INVENTORY_MANAGE') or hasRole('ROLE_ADMIN')")
    @Auditable(action = AuditAction.CREATE, module = AuditModule.INVENTORY, entityType = "StockMovement", description = "Recorded Stock Movement")
    @Operation(summary = "Record Stock Movement (Receive, Issue, Transfer, Damage, Return, Adjustment)")
    public ResponseEntity<ApiResponse<StockMovementDTO>> recordMovement(@Valid @RequestBody StockMovementDTO dto) {
        StockMovementDTO result = movementService.recordMovement(dto);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(result, "Recorded stock movement successfully"));
    }
}
