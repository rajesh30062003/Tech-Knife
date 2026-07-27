package com.techknife.asset.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BulkAssetAssignRequest {

    @NotEmpty(message = "Asset IDs list cannot be empty")
    private List<String> assetIds;

    @NotBlank(message = "Employee ID is required")
    private String employeeId;

    private String employeeName;

    private String departmentId;

    private String departmentName;

    private LocalDate assignmentDate;

    private LocalDate expectedReturnDate;

    private String notes;
}
