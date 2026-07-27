package com.techknife.customerportal.dto;

import com.techknife.customerportal.entity.InvoiceView;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.time.LocalDate;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class InvoiceViewDTO {

    private String id;
    private String invoiceNumber;
    private String customerAccountId;
    private String customerName;
    private String projectId;
    private String projectName;
    private Double amount;
    private Double taxAmount;
    private Double totalAmount;
    private String currency;
    private String status;
    private LocalDate issueDate;
    private LocalDate dueDate;
    private String pdfUrl;
    private List<InvoiceView.InvoiceItem> lineItems;
    private List<PaymentHistoryDTO> paymentHistory;
    private Instant createdAt;
    private Instant updatedAt;
}
