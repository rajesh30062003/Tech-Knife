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

import java.time.Instant;
import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "fin_financial_years")
public class FinancialYear {

    @Id
    private String id;

    @Indexed(unique = true)
    private String yearCode; // e.g. "FY2025-26"

    private String yearName; // e.g. "Financial Year 2025-2026"

    private LocalDate startDate;

    private LocalDate endDate;

    @Builder.Default
    private String status = "PLANNING"; // PLANNING, OPEN, CLOSED, LOCKED, ARCHIVED

    @Builder.Default
    private Boolean isLocked = false;

    private String notes;

    @CreatedDate
    private Instant createdAt;

    @LastModifiedDate
    private Instant updatedAt;

    @CreatedBy
    private String createdBy;

    @LastModifiedBy
    private String updatedBy;
}
