package com.techknife.procurement.controller;

import com.techknife.audit.annotation.Auditable;
import com.techknife.audit.entity.AuditAction;
import com.techknife.audit.entity.AuditModule;
import com.techknife.backend.dto.ApiResponse;
import com.techknife.procurement.dto.PurchaseOrderDTO;
import com.techknife.procurement.service.PurchaseOrderService;
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
@RequestMapping("/api/v1/procurement/purchase-orders")
@RequiredArgsConstructor
@Tag(name = "Procurement - Purchase Orders", description = "Purchase Order API")
@SecurityRequirement(name = "bearerAuth")
public class PurchaseOrderController {

    private final PurchaseOrderService orderService;

    @GetMapping
    @PreAuthorize("hasAuthority('PURCHASE_REQUEST_CREATE') or hasRole('ROLE_ADMIN')")
    @Operation(summary = "Get All Purchase Orders")
    public ResponseEntity<ApiResponse<List<PurchaseOrderDTO>>> getAllPurchaseOrders() {
        List<PurchaseOrderDTO> result = orderService.getAllPurchaseOrders();
        return ResponseEntity.ok(ApiResponse.success(result, "Fetched purchase orders successfully"));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('PURCHASE_REQUEST_CREATE') or hasRole('ROLE_ADMIN')")
    @Operation(summary = "Get Purchase Order by ID")
    public ResponseEntity<ApiResponse<PurchaseOrderDTO>> getPurchaseOrderById(@PathVariable String id) {
        PurchaseOrderDTO result = orderService.getPurchaseOrderById(id);
        return ResponseEntity.ok(ApiResponse.success(result, "Fetched purchase order successfully"));
    }

    @PostMapping
    @PreAuthorize("hasAuthority('PURCHASE_REQUEST_CREATE') or hasRole('ROLE_ADMIN')")
    @Auditable(action = AuditAction.CREATE, module = AuditModule.PROCUREMENT, entityType = "PurchaseOrder", description = "Created Purchase Order")
    @Operation(summary = "Create Purchase Order")
    public ResponseEntity<ApiResponse<PurchaseOrderDTO>> createPurchaseOrder(@Valid @RequestBody PurchaseOrderDTO dto) {
        PurchaseOrderDTO result = orderService.createPurchaseOrder(dto);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(result, "Created purchase order successfully"));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('PURCHASE_REQUEST_CREATE') or hasRole('ROLE_ADMIN')")
    @Auditable(action = AuditAction.UPDATE, module = AuditModule.PROCUREMENT, entityType = "PurchaseOrder", description = "Updated Purchase Order")
    @Operation(summary = "Update Purchase Order")
    public ResponseEntity<ApiResponse<PurchaseOrderDTO>> updatePurchaseOrder(@PathVariable String id, @Valid @RequestBody PurchaseOrderDTO dto) {
        PurchaseOrderDTO result = orderService.updatePurchaseOrder(id, dto);
        return ResponseEntity.ok(ApiResponse.success(result, "Updated purchase order successfully"));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('PURCHASE_REQUEST_CREATE') or hasRole('ROLE_ADMIN')")
    @Auditable(action = AuditAction.DELETE, module = AuditModule.PROCUREMENT, entityType = "PurchaseOrder", description = "Deleted Purchase Order")
    @Operation(summary = "Delete Purchase Order")
    public ResponseEntity<ApiResponse<Void>> deletePurchaseOrder(@PathVariable String id) {
        orderService.deletePurchaseOrder(id);
        return ResponseEntity.ok(ApiResponse.success(null, "Deleted purchase order successfully"));
    }
}
