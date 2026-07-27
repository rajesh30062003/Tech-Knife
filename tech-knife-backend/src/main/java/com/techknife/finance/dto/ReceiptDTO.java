package com.techknife.finance.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReceiptDTO {

    private String id;

    private String receiptNumber;

    private String receiptType; // CUSTOMER_RECEIPT, ADVANCE_RECEIPT, REFUND_RECEIPT

    private String customerId;

    private String customerName;

    private String invoiceId;

    private LocalDate receiptDate;

    @NotNull(message = "Receipt amount is required")
    private BigDecimal amount;

    private String paymentMethod;

    private String referenceNumber;

    private String status;

    private String notes;

    private Instant createdAt;

    private Instant updatedAt;

    private String createdBy;

    private String updatedBy;
}
