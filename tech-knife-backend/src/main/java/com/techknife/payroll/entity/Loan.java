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
@Document(collection = "payroll_loans")
public class Loan {

    @Id
    private String id;

    @Indexed
    private String employeeId;

    private String employeeName;

    private String loanType; // PERSONAL, HOME, CAR, EMERGENCY, EDUCATION

    private BigDecimal amount;

    private BigDecimal interestRate;

    private Integer termMonths;

    private BigDecimal emiAmount;

    private BigDecimal remainingAmount;

    @Builder.Default
    private String status = "PENDING"; // PENDING, APPROVED, ACTIVE, CLOSED, REJECTED

    @CreatedDate
    private Instant createdAt;

    @LastModifiedDate
    private Instant updatedAt;

    @CreatedBy
    private String createdBy;

    @LastModifiedBy
    private String updatedBy;
}
