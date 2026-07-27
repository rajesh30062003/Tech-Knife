package com.techknife.finance.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.Instant;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BudgetDTO {

    private String id;

    @NotBlank(message = "Budget name is required")
    private String budgetName;

    @NotBlank(message = "Budget scope is required")
    private String budgetScope; // COMPANY, DEPARTMENT, PROJECT

    private String departmentId;

    private String projectId;

    private String financialYearId;

    private String costCenterId;

    @NotNull(message = "Budgeted amount is required")
    private BigDecimal budgetedAmount;

    private BigDecimal actualAmount;

    private BigDecimal varianceAmount;

    private String description;

    private String status;

    private Instant createdAt;

    private Instant updatedAt;

    private String createdBy;

    private String updatedBy;
}
