package com.techknife.crm.dto;

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
public class ContractDTO {
    private String id;
    private String contractNumber;
    private String customerId;
    private String opportunityId;
    private String title;

    private LocalDate startDate;
    private LocalDate endDate;
    private LocalDate renewalDate;
    private Double contractValue;

    private String status;
    private Boolean digitalSignatureReady;
    private String digitalSignatureUrl;
    private String contractDocumentUrl;
    private String termsAndConditions;

    private Instant createdAt;
    private Instant updatedAt;
}
