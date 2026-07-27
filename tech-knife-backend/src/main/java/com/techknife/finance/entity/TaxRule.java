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

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "fin_tax_rules")
public class TaxRule {

    @Id
    private String id;

    @Indexed(unique = true)
    private String ruleCode;

    private String ruleName;

    private String taxType; // GST, TDS, PROFESSIONAL_TAX, CUSTOM

    @Builder.Default
    private BigDecimal rate = BigDecimal.ZERO;

    private String description;

    @Builder.Default
    private String status = "ACTIVE"; // ACTIVE, INACTIVE

    @CreatedDate
    private Instant createdAt;

    @LastModifiedDate
    private Instant updatedAt;

    @CreatedBy
    private String createdBy;

    @LastModifiedBy
    private String updatedBy;
}
