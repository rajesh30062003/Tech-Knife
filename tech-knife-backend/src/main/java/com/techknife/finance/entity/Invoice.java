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
import java.util.ArrayList;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "fin_invoices")
public class Invoice {

    @Id
    private String id;

    @Indexed(unique = true)
    private String invoiceNumber;

    private String customerId;

    private String customerName;

    private String customerEmail;

    private String invoiceType; // CUSTOMER_INVOICE, RECURRING_INVOICE

    private LocalDate issueDate;

    private LocalDate dueDate;

    private String financialYearId;

    private String costCenterId;

    @Builder.Default
    private List<InvoiceItem> items = new ArrayList<>();

    @Builder.Default
    private BigDecimal subtotal = BigDecimal.ZERO;

    @Builder.Default
    private BigDecimal taxTotal = BigDecimal.ZERO;

    @Builder.Default
    private BigDecimal totalAmount = BigDecimal.ZERO;

    @Builder.Default
    private BigDecimal paidAmount = BigDecimal.ZERO;

    @Builder.Default
    private BigDecimal balanceDue = BigDecimal.ZERO;

    @Builder.Default
    private String status = "DRAFT"; // DRAFT, APPROVED, SENT, PAID, PARTIALLY_PAID, OVERDUE, CANCELLED

    @Builder.Default
    private Boolean isRecurring = false;

    private String recurringFrequency; // MONTHLY, QUARTERLY, ANNUALLY

    private String notes;

    private String termsAndConditions;

    @CreatedDate
    private Instant createdAt;

    @LastModifiedDate
    private Instant updatedAt;

    @CreatedBy
    private String createdBy;

    @LastModifiedBy
    private String updatedBy;
}
