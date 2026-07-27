package com.techknife.finance.controller;

import com.techknife.audit.annotation.Auditable;
import com.techknife.audit.entity.AuditAction;
import com.techknife.audit.entity.AuditModule;
import com.techknife.backend.dto.ApiResponse;
import com.techknife.finance.dto.ReceiptDTO;
import com.techknife.finance.service.ReceiptService;
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
@RequestMapping("/api/v1/finance/receipts")
@RequiredArgsConstructor
@Tag(name = "Finance - Receipts", description = "Manage and issue Receipts")
@SecurityRequirement(name = "bearerAuth")
public class ReceiptController {

    private final ReceiptService receiptService;

    @GetMapping
    @PreAuthorize("hasAuthority('FINANCE_VIEW') or hasAuthority('PAYMENT_RECORD') or hasAuthority('FINANCE_MANAGE') or hasRole('ROLE_ADMIN')")
    @Operation(summary = "Get All Receipts")
    public ResponseEntity<ApiResponse<List<ReceiptDTO>>> getAllReceipts(
            @RequestParam(required = false) String customerId,
            @RequestParam(required = false) String invoiceId) {

        List<ReceiptDTO> result;
        if (customerId != null && !customerId.isBlank()) {
            result = receiptService.getReceiptsByCustomer(customerId);
        } else if (invoiceId != null && !invoiceId.isBlank()) {
            result = receiptService.getReceiptsByInvoice(invoiceId);
        } else {
            result = receiptService.getAllReceipts();
        }

        return ResponseEntity.ok(ApiResponse.success(result, "Fetched receipts successfully"));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('FINANCE_VIEW') or hasAuthority('PAYMENT_RECORD') or hasAuthority('FINANCE_MANAGE') or hasRole('ROLE_ADMIN')")
    @Operation(summary = "Get Receipt by ID")
    public ResponseEntity<ApiResponse<ReceiptDTO>> getReceiptById(@PathVariable String id) {
        ReceiptDTO result = receiptService.getReceiptById(id);
        return ResponseEntity.ok(ApiResponse.success(result, "Fetched receipt successfully"));
    }

    @PostMapping
    @PreAuthorize("hasAuthority('PAYMENT_RECORD') or hasAuthority('FINANCE_MANAGE') or hasRole('ROLE_ADMIN')")
    @Auditable(action = AuditAction.CREATE, module = AuditModule.FINANCE, entityType = "Receipt", description = "Issued Receipt")
    @Operation(summary = "Issue Receipt")
    public ResponseEntity<ApiResponse<ReceiptDTO>> issueReceipt(@Valid @RequestBody ReceiptDTO dto) {
        ReceiptDTO result = receiptService.issueReceipt(dto);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(result, "Issued receipt successfully"));
    }
}
