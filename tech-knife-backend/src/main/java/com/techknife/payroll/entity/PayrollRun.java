package com.techknife.payroll.entity;

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
@Document(collection = "payroll_runs")
public class PayrollRun {

    @Id
    private String id;

    @Indexed
    private String payrollCycleId;

    private String payrollCycleName;

    @Builder.Default
    private Integer totalEmployees = 0;

    @Builder.Default
    private BigDecimal totalGrossPay = BigDecimal.ZERO;

    @Builder.Default
    private BigDecimal totalNetPay = BigDecimal.ZERO;

    @Builder.Default
    private BigDecimal totalDeductions = BigDecimal.ZERO;

    @Indexed
    @Builder.Default
    private String status = "DRAFT"; // DRAFT, PROCESSING, APPROVED, DISBURSED

    private String processedBy;

    @CreatedDate
    private Instant createdAt;

    @LastModifiedDate
    private Instant updatedAt;

    @CreatedBy
    private String createdBy;

    @LastModifiedBy
    private String updatedBy;
}
