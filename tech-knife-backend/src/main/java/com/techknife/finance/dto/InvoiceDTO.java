package com.techknife.finance.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class InvoiceDTO {

    private String id;

    private String invoiceNumber;

    @NotBlank(message = "Customer ID is required")
    private String customerId;

    private String customerName;

    private String customerEmail;

    private String invoiceType;

    private LocalDate issueDate;

    private LocalDate dueDate;

    private String financialYearId;

    private String costCenterId;

    private List<InvoiceItemDTO> items;

    private BigDecimal subtotal;

    private BigDecimal taxTotal;

    private BigDecimal totalAmount;

    private BigDecimal paidAmount;

    private BigDecimal balanceDue;

    private String status;

    private Boolean isRecurring;

    private String recurringFrequency;

    private String notes;

    private String termsAndConditions;

    private Instant createdAt;

    private Instant updatedAt;

    private String createdBy;

    private String updatedBy;
}
