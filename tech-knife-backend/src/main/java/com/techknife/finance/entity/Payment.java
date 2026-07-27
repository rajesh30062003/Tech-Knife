package com.techknife.finance.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "fin_payments")
public class Payment {

    @Id
    private String id;

    @Indexed(unique = true)
    private String paymentNumber;

    private String invoiceId;

    private String vendorId;

    private String expenseId;

    private String entityName; // Customer or Vendor or Employee name

    private LocalDate paymentDate;

    @Builder.Default
    private BigDecimal amount = BigDecimal.ZERO;

    private String paymentMethod; // ONLINE, BANK_TRANSFER, CASH, CHEQUE, UPI

    private String referenceNumber;

    @Builder.Default
    private String status = "COMPLETED"; // PENDING, COMPLETED, FAILED, CANCELLED

    private String notes;

    @CreatedDate
    private Instant createdAt;

    @LastModifiedDate
    private Instant updatedAt;

    @CreatedBy
    private String createdBy;

    @LastModifiedBy
    private String updatedBy;
}
