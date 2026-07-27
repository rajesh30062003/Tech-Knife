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
public class TaxConfigurationDTO {

    private String id;

    @NotBlank(message = "Financial year is mandatory")
    private String financialYear;

    private String taxSlabName;

    private BigDecimal minIncome;

    private BigDecimal maxIncome;

    private BigDecimal taxRate;

    private String status;

    private Instant createdAt;
    private Instant updatedAt;
    private String createdBy;
    private String updatedBy;
}
