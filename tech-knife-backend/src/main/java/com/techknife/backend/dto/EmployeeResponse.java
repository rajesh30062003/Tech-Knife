package com.techknife.backend.dto;

import com.techknife.backend.entity.Employee;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EmployeeResponse {

    private String id;
    private String employeeId;
    private String officialEmail;
    private String personalEmail;
    private String primaryMobile;
    private String alternateMobile;
    private String firstName;
    private String lastName;
    private String fullName;
    private Employee.Gender gender;
    private LocalDate dob;
    private String bloodGroup;
    private String departmentId;
    private String designationId;
    private String managerId;
    private LocalDate joiningDate;
    private Employee.EmploymentType employmentType;
    private BigDecimal salary;
    private List<String> skills;
    private String githubUsername;
    private String profileImage;
    private Employee.EmployeeStatus status;
    private String managerName;
    private List<Object> currentProjects;
    private Instant createdAt;
    private Instant updatedAt;
    private String createdBy;
    private String updatedBy;
}
