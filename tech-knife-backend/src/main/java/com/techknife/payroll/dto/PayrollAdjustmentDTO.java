package com.techknife.payroll.dto;

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
public class PayrollAdjustmentDTO {

    private String id;

    @NotBlank(message = "Employee ID is mandatory")
    private String employeeId;

    private String employeeName;

    private String payrollCycleId;

    private String adjustmentType; // BONUS, OVERTIME, PENALTY, EXPENSE

    @NotNull(message = "Amount is mandatory")
    private BigDecimal amount;

    private String reason;

    private String status;

    private Instant createdAt;
    private Instant updatedAt;
    private String createdBy;
    private String updatedBy;
}
