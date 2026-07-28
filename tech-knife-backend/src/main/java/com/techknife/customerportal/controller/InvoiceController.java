package com.techknife.customerportal.controller;

import com.techknife.audit.annotation.Auditable;
import com.techknife.audit.entity.AuditAction;
import com.techknife.audit.entity.AuditModule;
import com.techknife.backend.dto.ApiResponse;
import com.techknife.customerportal.dto.InvoiceViewDTO;
import com.techknife.customerportal.dto.PaymentHistoryDTO;
import com.techknife.customerportal.service.InvoiceService;
import com.techknife.security.CurrentUser;
import com.techknife.security.UserPrincipal;
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
@RequestMapping("/api/v1/customer/invoices")
@RequiredArgsConstructor
@Tag(name = "Customer Portal - Invoices & Payments", description = "Invoice and Payment History management")
@SecurityRequirement(name = "bearerAuth")
public class InvoiceController {

    private final InvoiceService invoiceService;

    @GetMapping
    @PreAuthorize("hasAuthority('CUSTOMER_INVOICE_VIEW') or hasAuthority('CUSTOMER_PORTAL_ACCESS') or hasRole('ROLE_ADMIN')")
    @Operation(summary = "Get Customer Invoices")
    public ResponseEntity<ApiResponse<List<InvoiceViewDTO>>> getInvoices(
            @CurrentUser UserPrincipal userPrincipal,
            @RequestParam(required = false) String status) {
        List<InvoiceViewDTO> result = invoiceService.getInvoices(userPrincipal.getId(), status);
        return ResponseEntity.ok(ApiResponse.success(result, "Fetched invoices successfully"));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('CUSTOMER_INVOICE_VIEW') or hasAuthority('CUSTOMER_PORTAL_ACCESS') or hasRole('ROLE_ADMIN')")
    @Operation(summary = "Get Invoice Details by ID")
    public ResponseEntity<ApiResponse<InvoiceViewDTO>> getInvoiceById(
            @CurrentUser UserPrincipal userPrincipal,
            @PathVariable String id) {
        InvoiceViewDTO result = invoiceService.getInvoiceById(id, userPrincipal.getId());
        return ResponseEntity.ok(ApiResponse.success(result, "Fetched invoice details successfully"));
    }

    @GetMapping("/payments")
    @PreAuthorize("hasAuthority('CUSTOMER_INVOICE_VIEW') or hasAuthority('CUSTOMER_PORTAL_ACCESS') or hasRole('ROLE_ADMIN')")
    @Operation(summary = "Get Payment History")
    public ResponseEntity<ApiResponse<List<PaymentHistoryDTO>>> getPaymentHistory(
            @CurrentUser UserPrincipal userPrincipal,
            @RequestParam(required = false) String invoiceId) {
        List<PaymentHistoryDTO> result = invoiceService.getPaymentHistory(userPrincipal.getId(), invoiceId);
        return ResponseEntity.ok(ApiResponse.success(result, "Fetched payment history successfully"));
    }

    @PostMapping
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @Auditable(action = AuditAction.CREATE, module = AuditModule.CUSTOMER_PORTAL, entityType = "InvoiceView", description = "Created Invoice")
    @Operation(summary = "Create Invoice View (Admin Endpoint)")
    public ResponseEntity<ApiResponse<InvoiceViewDTO>> createInvoice(@Valid @RequestBody InvoiceViewDTO dto) {
        InvoiceViewDTO result = invoiceService.createInvoice(dto);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(result, "Created invoice successfully"));
    }

    @PostMapping("/payments")
    @PreAuthorize("hasAuthority('CUSTOMER_INVOICE_VIEW') or hasAuthority('CUSTOMER_PORTAL_ACCESS') or hasRole('ROLE_ADMIN')")
    @Auditable(action = AuditAction.CREATE, module = AuditModule.CUSTOMER_PORTAL, entityType = "PaymentHistory", description = "Recorded Payment")
    @Operation(summary = "Record Payment for Invoice")
    public ResponseEntity<ApiResponse<PaymentHistoryDTO>> recordPayment(
            @CurrentUser UserPrincipal userPrincipal,
            @Valid @RequestBody PaymentHistoryDTO dto) {
        dto.setCustomerAccountId(userPrincipal.getId());
        PaymentHistoryDTO result = invoiceService.recordPayment(dto);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(result, "Payment recorded successfully"));
    }

}
