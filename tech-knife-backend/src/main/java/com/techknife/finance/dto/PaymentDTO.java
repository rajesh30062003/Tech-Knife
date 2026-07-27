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
public class PaymentDTO {

    private String id;

    private String paymentNumber;

    private String invoiceId;

    private String vendorId;

    private String expenseId;

    private String entityName;

    private LocalDate paymentDate;

    @NotNull(message = "Payment amount is required")
    private BigDecimal amount;

    private String paymentMethod; // ONLINE, BANK_TRANSFER, CASH, CHEQUE, UPI

    private String referenceNumber;

    private String status;

    private String notes;

    private Instant createdAt;

    private Instant updatedAt;

    private String createdBy;

    private String updatedBy;
}
