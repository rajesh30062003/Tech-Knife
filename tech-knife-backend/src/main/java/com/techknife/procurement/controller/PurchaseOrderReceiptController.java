package com.techknife.procurement.controller;

import com.techknife.audit.annotation.Auditable;
import com.techknife.audit.entity.AuditAction;
import com.techknife.audit.entity.AuditModule;
import com.techknife.backend.dto.ApiResponse;
import com.techknife.procurement.dto.PurchaseOrderReceiptDTO;
import com.techknife.procurement.service.PurchaseOrderReceiptService;
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
@RequestMapping("/api/v1/procurement/goods-receipts")
@RequiredArgsConstructor
@Tag(name = "Procurement - Goods Receipts", description = "Goods Receipt / PO Receipt API")
@SecurityRequirement(name = "bearerAuth")
public class PurchaseOrderReceiptController {

    private final PurchaseOrderReceiptService receiptService;

    @GetMapping
    @PreAuthorize("hasAuthority('PURCHASE_REQUEST_CREATE') or hasRole('ROLE_ADMIN')")
    @Operation(summary = "Get All Goods Receipts")
    public ResponseEntity<ApiResponse<List<PurchaseOrderReceiptDTO>>> getAllReceipts() {
        List<PurchaseOrderReceiptDTO> result = receiptService.getAllReceipts();
        return ResponseEntity.ok(ApiResponse.success(result, "Fetched goods receipts successfully"));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('PURCHASE_REQUEST_CREATE') or hasRole('ROLE_ADMIN')")
    @Operation(summary = "Get Goods Receipt by ID")
    public ResponseEntity<ApiResponse<PurchaseOrderReceiptDTO>> getReceiptById(@PathVariable String id) {
        PurchaseOrderReceiptDTO result = receiptService.getReceiptById(id);
        return ResponseEntity.ok(ApiResponse.success(result, "Fetched goods receipt successfully"));
    }

    @PostMapping
    @PreAuthorize("hasAuthority('PURCHASE_REQUEST_CREATE') or hasRole('ROLE_ADMIN')")
    @Auditable(action = AuditAction.CREATE, module = AuditModule.PROCUREMENT, entityType = "PurchaseOrderReceipt", description = "Created Goods Receipt")
    @Operation(summary = "Create Goods Receipt")
    public ResponseEntity<ApiResponse<PurchaseOrderReceiptDTO>> createReceipt(@Valid @RequestBody PurchaseOrderReceiptDTO dto) {
        PurchaseOrderReceiptDTO result = receiptService.createReceipt(dto);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(result, "Created goods receipt successfully"));
    }

    @GetMapping("/purchase-order/{purchaseOrderId}")
    @PreAuthorize("hasAuthority('PURCHASE_REQUEST_CREATE') or hasRole('ROLE_ADMIN')")
    @Operation(summary = "Get Goods Receipts by Purchase Order")
    public ResponseEntity<ApiResponse<List<PurchaseOrderReceiptDTO>>> getReceiptsByPurchaseOrder(@PathVariable String purchaseOrderId) {
        List<PurchaseOrderReceiptDTO> result = receiptService.getReceiptsByPurchaseOrder(purchaseOrderId);
        return ResponseEntity.ok(ApiResponse.success(result, "Fetched goods receipts by purchase order"));
    }
}
