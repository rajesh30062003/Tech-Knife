package com.techknife.payroll.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SalaryComponentDTO {

    private String id;

    @NotBlank(message = "Component code is mandatory")
    private String componentCode;

    @NotBlank(message = "Component name is mandatory")
    private String componentName;

    private String componentType; // EARNING, DEDUCTION

    private String calculationType; // FIXED, PERCENTAGE

    private Double percentageValue;

    private String baseComponent;

    private Boolean isTaxable;

    private Boolean isStatutory;

    private String status;

    private Instant createdAt;
    private Instant updatedAt;
    private String createdBy;
    private String updatedBy;
}
