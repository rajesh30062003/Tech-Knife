package com.techknife.finance.controller;

import com.techknife.audit.annotation.Auditable;
import com.techknife.audit.entity.AuditAction;
import com.techknife.audit.entity.AuditModule;
import com.techknife.backend.dto.ApiResponse;
import com.techknife.finance.dto.PaymentDTO;
import com.techknife.finance.service.PaymentService;
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
@RequestMapping("/api/v1/finance/payments")
@RequiredArgsConstructor
@Tag(name = "Finance - Payments", description = "Manage and record outgoing Payments")
@SecurityRequirement(name = "bearerAuth")
public class PaymentController {

    private final PaymentService paymentService;

    @GetMapping
    @PreAuthorize("hasAuthority('FINANCE_VIEW') or hasAuthority('PAYMENT_RECORD') or hasAuthority('FINANCE_MANAGE') or hasRole('ROLE_ADMIN')")
    @Operation(summary = "Get All Payments")
    public ResponseEntity<ApiResponse<List<PaymentDTO>>> getAllPayments(
            @RequestParam(required = false) String invoiceId,
            @RequestParam(required = false) String vendorId) {

        List<PaymentDTO> result;
        if (invoiceId != null && !invoiceId.isBlank()) {
            result = paymentService.getPaymentsByInvoice(invoiceId);
        } else if (vendorId != null && !vendorId.isBlank()) {
            result = paymentService.getPaymentsByVendor(vendorId);
        } else {
            result = paymentService.getAllPayments();
        }

        return ResponseEntity.ok(ApiResponse.success(result, "Fetched payments successfully"));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('FINANCE_VIEW') or hasAuthority('PAYMENT_RECORD') or hasAuthority('FINANCE_MANAGE') or hasRole('ROLE_ADMIN')")
    @Operation(summary = "Get Payment by ID")
    public ResponseEntity<ApiResponse<PaymentDTO>> getPaymentById(@PathVariable String id) {
        PaymentDTO result = paymentService.getPaymentById(id);
        return ResponseEntity.ok(ApiResponse.success(result, "Fetched payment successfully"));
    }

    @PostMapping
    @PreAuthorize("hasAuthority('PAYMENT_RECORD') or hasAuthority('FINANCE_MANAGE') or hasRole('ROLE_ADMIN')")
    @Auditable(action = AuditAction.CREATE, module = AuditModule.FINANCE, entityType = "Payment", description = "Recorded Payment")
    @Operation(summary = "Record Payment")
    public ResponseEntity<ApiResponse<PaymentDTO>> recordPayment(@Valid @RequestBody PaymentDTO dto) {
        PaymentDTO result = paymentService.recordPayment(dto);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(result, "Recorded payment successfully"));
    }
}
