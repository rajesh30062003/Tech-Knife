package com.techknife.finance.dto;

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
public class CostCenterDTO {

    private String id;

    @NotBlank(message = "Cost center code is required")
    private String centerCode;

    @NotBlank(message = "Cost center name is required")
    private String centerName;

    @NotBlank(message = "Cost center type is required")
    private String type; // COMPANY, DEPARTMENT, PROJECT, BRANCH

    private String departmentId;

    private String projectId;

    private String branchName;

    private String description;

    private String status;

    private Instant createdAt;

    private Instant updatedAt;

    private String createdBy;

    private String updatedBy;
}
