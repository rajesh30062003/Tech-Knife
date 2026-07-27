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
import org.springframework.data.mongodb.core.mapping.Document;

import java.math.BigDecimal;
import java.time.Instant;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "fin_budgets")
public class Budget {

    @Id
    private String id;

    private String budgetName;

    private String budgetScope; // COMPANY, DEPARTMENT, PROJECT

    private String departmentId;

    private String projectId;

    private String financialYearId;

    private String costCenterId;

    @Builder.Default
    private BigDecimal budgetedAmount = BigDecimal.ZERO;

    @Builder.Default
    private BigDecimal actualAmount = BigDecimal.ZERO;

    @Builder.Default
    private BigDecimal varianceAmount = BigDecimal.ZERO;

    private String description;

    @Builder.Default
    private String status = "ACTIVE"; // DRAFT, ACTIVE, CLOSED

    @CreatedDate
    private Instant createdAt;

    @LastModifiedDate
    private Instant updatedAt;

    @CreatedBy
    private String createdBy;

    @LastModifiedBy
    private String updatedBy;
}
