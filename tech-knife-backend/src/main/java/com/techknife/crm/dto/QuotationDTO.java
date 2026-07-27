package com.techknife.crm.dto;

import com.techknife.crm.entity.QuotationItem;
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
public class QuotationDTO {
    private String id;
    private String quotationNumber;
    private String customerId;
    private String opportunityId;
    private String leadId;
    private String title;

    private List<QuotationItem> items;
    private Double subTotal;
    private Double discountTotal;
    private Double taxTotal;
    private Double grandTotal;

    private LocalDate validityDate;
    private String approvalStatus;
    private String approvedBy;
    private String quotationUrl;
    private String notes;

    private Instant createdAt;
    private Instant updatedAt;
}
