package com.techknife.payroll.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LoanRepaymentDTO {

    private String id;

    @NotBlank(message = "Loan ID is mandatory")
    private String loanId;

    private LocalDate repaymentDate;

    @NotNull(message = "Amount paid is mandatory")
    private BigDecimal amountPaid;

    private BigDecimal principalAmount;

    private BigDecimal interestAmount;

    private BigDecimal remainingBalance;

    private String paymentMode;

    private Instant createdAt;
    private Instant updatedAt;
    private String createdBy;
    private String updatedBy;
}
