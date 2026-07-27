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
@Document(collection = "payroll_adjustments")
public class PayrollAdjustment {

    @Id
    private String id;

    @Indexed
    private String employeeId;

    private String employeeName;

    @Indexed
    private String payrollCycleId;

    private String adjustmentType; // BONUS, OVERTIME, PENALTY, EXPENSE

    private BigDecimal amount;

    private String reason;

    @Builder.Default
    private String status = "PENDING"; // PENDING, APPROVED, REJECTED

    @CreatedDate
    private Instant createdAt;

    @LastModifiedDate
    private Instant updatedAt;

    @CreatedBy
    private String createdBy;

    @LastModifiedBy
    private String updatedBy;
}
