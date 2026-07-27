package com.techknife.employee.dto;

import com.techknife.employee.entity.BloodGroup;
import com.techknife.employee.entity.EmployeeStatus;
import com.techknife.employee.entity.EmploymentType;
import com.techknife.employee.entity.Gender;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.List;

/**
 * Standard Employee Data Transfer Object representing internal state.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EmployeeDTO {

    private String id;
    private String employeeId;
    private String officialEmail;
    private String personalEmail;
    private String primaryMobile;
    private String alternateMobile;
    private String firstName;
    private String lastName;
    private Gender gender;
    private LocalDate dob;
    private BloodGroup bloodGroup;
    private String departmentId;
    private String designationId;
    private String managerId;
    private LocalDate joiningDate;
    private EmploymentType employmentType;
    private BigDecimal salary;
    private List<String> skills;
    private String githubUsername;
    private String profileImage;
    private EmployeeStatus status;
    private Instant createdAt;
    private Instant updatedAt;
    private String createdBy;
    private String updatedBy;
}
