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

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "crm_contracts")
public class Contract {

    @Id
    private String id;

    @Indexed(unique = true)
    private String contractNumber;

    @Indexed
    private String customerId;

    private String opportunityId;

    private String title;

    private LocalDate startDate;

    private LocalDate endDate;

    private LocalDate renewalDate;

    private Double contractValue;

    @Builder.Default
    private String status = "DRAFT"; // DRAFT, ACTIVE, EXPIRED, RENEWED, TERMINATED

    @Builder.Default
    private Boolean digitalSignatureReady = false;

    private String digitalSignatureUrl;

    private String contractDocumentUrl;

    private String termsAndConditions;

    @CreatedDate
    private Instant createdAt;

    @LastModifiedDate
    private Instant updatedAt;
}
