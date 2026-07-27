package com.techknife.employee.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BulkDepartmentTransferRequest {

    @NotEmpty(message = "Employee ID list must not be empty")
    private List<String> employeeIds;

    @NotBlank(message = "Target department ID is mandatory")
    private String targetDepartmentId;

    private String remarks;
}
