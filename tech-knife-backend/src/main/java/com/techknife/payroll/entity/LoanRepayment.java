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
import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "payroll_loan_repayments")
public class LoanRepayment {

    @Id
    private String id;

    @Indexed
    private String loanId;

    private LocalDate repaymentDate;

    private BigDecimal amountPaid;

    private BigDecimal principalAmount;

    private BigDecimal interestAmount;

    private BigDecimal remainingBalance;

    private String paymentMode; // SALARY_DEDUCTION, BANK_TRANSFER, CASH, CHEQUE

    @CreatedDate
    private Instant createdAt;

    @LastModifiedDate
    private Instant updatedAt;

    @CreatedBy
    private String createdBy;

    @LastModifiedBy
    private String updatedBy;
}
