package com.techknife.crm.entity;

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
@Document(collection = "crm_quotations")
public class Quotation {

    @Id
    private String id;

    @Indexed(unique = true)
    private String quotationNumber;

    @Indexed
    private String customerId;

    @Indexed
    private String opportunityId;

    @Indexed
    private String leadId;

    private String title;

    @Builder.Default
    private List<QuotationItem> items = new ArrayList<>();

    private Double subTotal;

    private Double discountTotal;

    private Double taxTotal;

    private Double grandTotal;

    private LocalDate validityDate;

    @Builder.Default
    private String approvalStatus = "PENDING"; // PENDING, APPROVED, REJECTED

    private String approvedBy;

    private String quotationUrl;

    private String notes;

    @CreatedDate
    private Instant createdAt;

    @LastModifiedDate
    private Instant updatedAt;
}
