package com.techknife.dto;

import com.techknife.entity.Employee;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateEmployeeRequest {

    @Email(message = "Invalid personal email format")
    private String personalEmail;

    @Size(min = 10, max = 15, message = "Primary mobile number must be between 10 and 15 digits")
    private String primaryMobile;

    private String alternateMobile;

    private String firstName;

    private String lastName;

    private Employee.Gender gender;

    private LocalDate dob;

    private String bloodGroup;

    private String departmentId;

    private String designationId;

    private String managerId;

    private LocalDate joiningDate;

    private Employee.EmploymentType employmentType;

    @PositiveOrZero(message = "Salary must be zero or positive")
    private BigDecimal salary;

    private List<String> skills;

    private String githubUsername;

    private String profileImage;

    private Employee.EmployeeStatus status;
}
