package com.techknife.employee.dto;

import com.techknife.employee.entity.EmployeeStatus;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BulkUpdateStatusRequest {

    @NotEmpty(message = "Employee ID list cannot be empty")
    private List<String> employeeIds;

    @NotNull(message = "Target status is required")
    private EmployeeStatus status;

    private String remarks;
}
