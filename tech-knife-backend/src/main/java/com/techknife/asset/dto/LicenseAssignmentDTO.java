package com.techknife.asset.dto;

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
public class LicenseAssignmentDTO {

    private String id;

    @NotBlank(message = "License ID is required")
    private String licenseId;

    private String softwareName;

    @NotBlank(message = "Employee ID is required")
    private String employeeId;

    private String employeeName;

    private LocalDate assignedDate;

    private String status;

    private Instant createdAt;
}
