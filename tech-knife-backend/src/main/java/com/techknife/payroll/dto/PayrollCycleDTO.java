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
public class PayrollCycleDTO {

    private String id;

    @NotBlank(message = "Cycle name is mandatory")
    private String cycleName;

    private Integer month;

    private Integer year;

    private LocalDate startDate;

    private LocalDate endDate;

    private LocalDate processingDate;

    private String status;

    private Instant createdAt;
    private Instant updatedAt;
    private String createdBy;
    private String updatedBy;
}
