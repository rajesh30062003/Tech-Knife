package com.techknife.customerportal.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;
import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "customer_payments")
public class PaymentHistory {

    @Id
    private String id;

    @Indexed
    private String invoiceId;

    private String invoiceNumber;

    @Indexed
    private String customerAccountId;

    private Double amount;

    private LocalDate paymentDate;

    private String paymentMethod; // CREDIT_CARD, BANK_TRANSFER, STRIPE, PAYPAL, UPI, CHEQUE

    private String referenceNumber;

    @Builder.Default
    private String status = "SUCCESS"; // SUCCESS, PENDING, FAILED

    private String notes;

    @CreatedDate
    private Instant createdAt;
}
