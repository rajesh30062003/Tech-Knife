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
public class TaxRuleDTO {

    private String id;

    @NotBlank(message = "Rule code is required")
    private String ruleCode;

    @NotBlank(message = "Rule name is required")
    private String ruleName;

    @NotBlank(message = "Tax type is required")
    private String taxType; // GST, TDS, PROFESSIONAL_TAX, CUSTOM

    @NotNull(message = "Tax rate is required")
    private BigDecimal rate;

    private String description;

    private String status;

    private Instant createdAt;

    private Instant updatedAt;

    private String createdBy;

    private String updatedBy;
}
