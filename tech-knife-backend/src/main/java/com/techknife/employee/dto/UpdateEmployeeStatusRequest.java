package com.techknife.employee.dto;

import com.techknife.employee.entity.EmployeeStatus;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Data Transfer Object for updating an employee's employment status.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateEmployeeStatusRequest {

    @NotNull(message = "Employee status is mandatory")
    private EmployeeStatus status;

    private String statusReason;

    public String getRemarks() {
        return statusReason;
    }
}

