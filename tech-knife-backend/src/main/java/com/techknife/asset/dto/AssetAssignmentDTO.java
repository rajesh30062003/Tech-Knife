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
public class AssetAssignmentDTO {

    private String id;

    @NotBlank(message = "Asset ID is required")
    private String assetId;

    private String assetCode;

    private String assetName;

    @NotBlank(message = "Employee ID is required")
    private String employeeId;

    private String employeeName;

    private String departmentId;

    private String departmentName;

    private LocalDate assignmentDate;

    private LocalDate expectedReturnDate;

    private LocalDate actualReturnDate;

    private String assignedBy;

    private String status;

    private String returnCondition;

    private String notes;

    private Instant createdAt;

    private Instant updatedAt;
}
