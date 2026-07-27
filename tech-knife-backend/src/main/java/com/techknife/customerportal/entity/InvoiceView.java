package com.techknife.customerportal.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "customer_invoices")
public class InvoiceView {

    @Id
    private String id;

    @Indexed(unique = true)
    private String invoiceNumber;

    @Indexed
    private String customerAccountId;

    private String customerName;

    @Indexed
    private String projectId;

    private String projectName;

    @Builder.Default
    private Double amount = 0.0;

    @Builder.Default
    private Double taxAmount = 0.0;

    @Builder.Default
    private Double totalAmount = 0.0;

    @Builder.Default
    private String currency = "USD";

    @Builder.Default
    private String status = "UNPAID"; // UNPAID, PAID, OVERDUE, CANCELLED, PARTIALLY_PAID

    private LocalDate issueDate;

    private LocalDate dueDate;

    private String pdfUrl;

    @Builder.Default
    private List<InvoiceItem> lineItems = new ArrayList<>();

    @CreatedDate
    private Instant createdAt;

    @LastModifiedDate
    private Instant updatedAt;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class InvoiceItem {
        private String description;
        private Integer quantity;
        private Double unitPrice;
        private Double amount;
    }
}
