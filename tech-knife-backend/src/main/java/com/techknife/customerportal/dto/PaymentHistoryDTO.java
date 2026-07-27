package com.techknife.customerportal.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PaymentHistoryDTO {

    private String id;
    private String invoiceId;
    private String invoiceNumber;
    private String customerAccountId;
    private Double amount;
    private LocalDate paymentDate;
    private String paymentMethod;
    private String referenceNumber;
    private String status;
    private String notes;
    private Instant createdAt;
}
