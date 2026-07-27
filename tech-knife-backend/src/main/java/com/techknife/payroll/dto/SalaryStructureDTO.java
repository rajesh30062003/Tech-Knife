package com.techknife.payroll.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SalaryStructureDTO {

    private String id;

    @NotBlank(message = "Structure code is mandatory")
    private String structureCode;

    @NotBlank(message = "Structure name is mandatory")
    private String structureName;

    private String employeeType;

    private String grade;

    private LocalDate effectiveDate;

    private String status;

    private Instant createdAt;
    private Instant updatedAt;
    private String createdBy;
    private String updatedBy;
}
