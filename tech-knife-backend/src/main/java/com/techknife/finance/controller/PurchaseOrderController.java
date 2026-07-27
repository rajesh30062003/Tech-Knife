package com.techknife.finance.controller;

import com.techknife.audit.annotation.Auditable;
import com.techknife.audit.entity.AuditAction;
import com.techknife.audit.entity.AuditModule;
import com.techknife.backend.dto.ApiResponse;
import com.techknife.finance.dto.PurchaseOrderDTO;
import com.techknife.finance.service.PurchaseOrderService;
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
@RequestMapping("/api/v1/finance/purchase-orders")
@RequiredArgsConstructor
@Tag(name = "Finance - Purchase Orders", description = "Manage Purchase Orders and Vendor Procurement")
@SecurityRequirement(name = "bearerAuth")
public class PurchaseOrderController {

    private final PurchaseOrderService purchaseOrderService;

    @GetMapping
    @PreAuthorize("hasAuthority('FINANCE_VIEW') or hasAuthority('FINANCE_MANAGE') or hasRole('ROLE_ADMIN')")
    @Operation(summary = "Get All Purchase Orders")
    public ResponseEntity<ApiResponse<List<PurchaseOrderDTO>>> getAllPurchaseOrders(@RequestParam(required = false) String vendorId) {
        List<PurchaseOrderDTO> result = vendorId != null && !vendorId.isBlank()
                ? purchaseOrderService.getPurchaseOrdersByVendor(vendorId)
                : purchaseOrderService.getAllPurchaseOrders();
        return ResponseEntity.ok(ApiResponse.success(result, "Fetched purchase orders successfully"));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('FINANCE_VIEW') or hasAuthority('FINANCE_MANAGE') or hasRole('ROLE_ADMIN')")
    @Operation(summary = "Get Purchase Order by ID")
    public ResponseEntity<ApiResponse<PurchaseOrderDTO>> getPurchaseOrderById(@PathVariable String id) {
        PurchaseOrderDTO result = purchaseOrderService.getPurchaseOrderById(id);
        return ResponseEntity.ok(ApiResponse.success(result, "Fetched purchase order successfully"));
    }

    @PostMapping
    @PreAuthorize("hasAuthority('FINANCE_MANAGE') or hasRole('ROLE_ADMIN')")
    @Auditable(action = AuditAction.CREATE, module = AuditModule.FINANCE, entityType = "PurchaseOrder", description = "Created Purchase Order")
    @Operation(summary = "Create Purchase Order")
    public ResponseEntity<ApiResponse<PurchaseOrderDTO>> createPurchaseOrder(@Valid @RequestBody PurchaseOrderDTO dto) {
        PurchaseOrderDTO result = purchaseOrderService.createPurchaseOrder(dto);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(result, "Created purchase order successfully"));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('FINANCE_MANAGE') or hasRole('ROLE_ADMIN')")
    @Auditable(action = AuditAction.UPDATE, module = AuditModule.FINANCE, entityType = "PurchaseOrder", description = "Updated Purchase Order")
    @Operation(summary = "Update Purchase Order")
    public ResponseEntity<ApiResponse<PurchaseOrderDTO>> updatePurchaseOrder(@PathVariable String id, @Valid @RequestBody PurchaseOrderDTO dto) {
        PurchaseOrderDTO result = purchaseOrderService.updatePurchaseOrder(id, dto);
        return ResponseEntity.ok(ApiResponse.success(result, "Updated purchase order successfully"));
    }

    @PatchMapping("/{id}/status")
    @PreAuthorize("hasAuthority('FINANCE_MANAGE') or hasRole('ROLE_ADMIN')")
    @Auditable(action = AuditAction.UPDATE, module = AuditModule.FINANCE, entityType = "PurchaseOrder", description = "Updated Purchase Order Status")
    @Operation(summary = "Update Purchase Order Status")
    public ResponseEntity<ApiResponse<PurchaseOrderDTO>> updatePurchaseOrderStatus(@PathVariable String id, @RequestParam String status) {
        PurchaseOrderDTO result = purchaseOrderService.updatePurchaseOrderStatus(id, status);
        return ResponseEntity.ok(ApiResponse.success(result, "Updated purchase order status successfully"));
    }
}
