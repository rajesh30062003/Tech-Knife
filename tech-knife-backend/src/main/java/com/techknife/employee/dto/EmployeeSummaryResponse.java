package com.techknife.employee.dto;

import com.techknife.employee.entity.EmployeeStatus;
import com.techknife.employee.entity.EmploymentType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

/**
 * Lightweight summary Data Transfer Object for Employee listings and table views.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EmployeeSummaryResponse {

    private String id;
    private String employeeId;
    private String fullName;
    private String officialEmail;
    private String primaryMobile;
    private String departmentId;
    private String designationId;
    private EmploymentType employmentType;
    private EmployeeStatus status;
    private String profileImage;
    private LocalDate joiningDate;
}
