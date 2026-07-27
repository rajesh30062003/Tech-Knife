package com.techknife.payroll.dto;

import jakarta.validation.constraints.NotBlank;
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
public class PayrollRunDTO {

    private String id;

    @NotBlank(message = "Payroll cycle ID is mandatory")
    private String payrollCycleId;

    private String payrollCycleName;

    private Integer totalEmployees;

    private BigDecimal totalGrossPay;

    private BigDecimal totalNetPay;

    private BigDecimal totalDeductions;

    private String status;

    private String processedBy;

    private Instant createdAt;
    private Instant updatedAt;
    private String createdBy;
    private String updatedBy;
}
