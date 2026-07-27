package com.techknife.finance.controller;

import com.techknife.audit.annotation.Auditable;
import com.techknife.audit.entity.AuditAction;
import com.techknife.audit.entity.AuditModule;
import com.techknife.backend.dto.ApiResponse;
import com.techknife.finance.dto.InvoiceDTO;
import com.techknife.finance.service.InvoiceService;
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
@RequestMapping("/api/v1/finance/invoices")
@RequiredArgsConstructor
@Tag(name = "Finance - Invoices", description = "Manage Customer & Recurring Invoices")
@SecurityRequirement(name = "bearerAuth")
public class InvoiceController {

    private final InvoiceService invoiceService;

    @GetMapping
    @PreAuthorize("hasAuthority('FINANCE_VIEW') or hasAuthority('INVOICE_CREATE') or hasAuthority('FINANCE_MANAGE') or hasRole('ROLE_ADMIN')")
    @Operation(summary = "Get All Invoices")
    public ResponseEntity<ApiResponse<List<InvoiceDTO>>> getAllInvoices(@RequestParam(required = false) String customerId) {
        List<InvoiceDTO> result = customerId != null && !customerId.isBlank()
                ? invoiceService.getInvoicesByCustomer(customerId)
                : invoiceService.getAllInvoices();
        return ResponseEntity.ok(ApiResponse.success(result, "Fetched invoices successfully"));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('FINANCE_VIEW') or hasAuthority('INVOICE_CREATE') or hasAuthority('FINANCE_MANAGE') or hasRole('ROLE_ADMIN')")
    @Operation(summary = "Get Invoice by ID")
    public ResponseEntity<ApiResponse<InvoiceDTO>> getInvoiceById(@PathVariable String id) {
        InvoiceDTO result = invoiceService.getInvoiceById(id);
        return ResponseEntity.ok(ApiResponse.success(result, "Fetched invoice successfully"));
    }

    @PostMapping
    @PreAuthorize("hasAuthority('INVOICE_CREATE') or hasAuthority('FINANCE_MANAGE') or hasRole('ROLE_ADMIN')")
    @Auditable(action = AuditAction.CREATE, module = AuditModule.FINANCE, entityType = "Invoice", description = "Created Invoice")
    @Operation(summary = "Create Invoice")
    public ResponseEntity<ApiResponse<InvoiceDTO>> createInvoice(@Valid @RequestBody InvoiceDTO dto) {
        InvoiceDTO result = invoiceService.createInvoice(dto);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(result, "Created invoice successfully"));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('INVOICE_CREATE') or hasAuthority('FINANCE_MANAGE') or hasRole('ROLE_ADMIN')")
    @Auditable(action = AuditAction.UPDATE, module = AuditModule.FINANCE, entityType = "Invoice", description = "Updated Invoice")
    @Operation(summary = "Update Invoice")
    public ResponseEntity<ApiResponse<InvoiceDTO>> updateInvoice(@PathVariable String id, @Valid @RequestBody InvoiceDTO dto) {
        InvoiceDTO result = invoiceService.updateInvoice(id, dto);
        return ResponseEntity.ok(ApiResponse.success(result, "Updated invoice successfully"));
    }

    @PatchMapping("/{id}/status")
    @PreAuthorize("hasAuthority('INVOICE_CREATE') or hasAuthority('FINANCE_MANAGE') or hasRole('ROLE_ADMIN')")
    @Auditable(action = AuditAction.UPDATE, module = AuditModule.FINANCE, entityType = "Invoice", description = "Updated Invoice Status")
    @Operation(summary = "Update Invoice Status")
    public ResponseEntity<ApiResponse<InvoiceDTO>> updateInvoiceStatus(@PathVariable String id, @RequestParam String status) {
        InvoiceDTO result = invoiceService.updateInvoiceStatus(id, status);
        return ResponseEntity.ok(ApiResponse.success(result, "Updated invoice status successfully"));
    }

    @PostMapping("/{id}/cancel")
    @PreAuthorize("hasAuthority('INVOICE_CREATE') or hasAuthority('FINANCE_MANAGE') or hasRole('ROLE_ADMIN')")
    @Auditable(action = AuditAction.UPDATE, module = AuditModule.FINANCE, entityType = "Invoice", description = "Cancelled Invoice")
    @Operation(summary = "Cancel Invoice")
    public ResponseEntity<ApiResponse<Void>> cancelInvoice(@PathVariable String id) {
        invoiceService.cancelInvoice(id);
        return ResponseEntity.ok(ApiResponse.success(null, "Cancelled invoice successfully"));
    }
}
