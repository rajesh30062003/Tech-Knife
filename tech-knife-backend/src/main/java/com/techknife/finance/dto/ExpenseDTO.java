package com.techknife.finance.dto;

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
public class ExpenseDTO {

    private String id;

    private String expenseNumber;

    private String categoryId;

    private String categoryName;

    @NotBlank(message = "Expense title is required")
    private String title;

    @NotNull(message = "Expense amount is required")
    private BigDecimal amount;

    private LocalDate expenseDate;

    private String vendorId;

    private String vendorName;

    private String employeeId;

    private String salaryRunId;

    private String costCenterId;

    private String financialYearId;

    private String receiptUrl;

    private String approvalStatus;

    private String approvedBy;

    private Instant approvedAt;

    private String description;

    private Instant createdAt;

    private Instant updatedAt;

    private String createdBy;

    private String updatedBy;
}
