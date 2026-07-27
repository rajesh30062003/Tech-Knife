package com.techknife.dto;

import com.techknife.entity.Employee;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateEmployeeRequest {

    @NotBlank(message = "Employee ID is mandatory")
    private String employeeId;

    @NotBlank(message = "Official email is mandatory")
    @Email(message = "Invalid official email format")
    private String officialEmail;

    @Email(message = "Invalid personal email format")
    private String personalEmail;

    @NotBlank(message = "Primary mobile number is mandatory")
    @Size(min = 10, max = 15, message = "Primary mobile number must be between 10 and 15 digits")
    private String primaryMobile;

    private String alternateMobile;

    @NotBlank(message = "First name is mandatory")
    private String firstName;

    @NotBlank(message = "Last name is mandatory")
    private String lastName;

    private Employee.Gender gender;

    private LocalDate dob;

    private String bloodGroup;

    private String departmentId;

    private String designationId;

    private String managerId;

    @NotNull(message = "Joining date is mandatory")
    private LocalDate joiningDate;

    @NotNull(message = "Employment type is mandatory")
    private Employee.EmploymentType employmentType;

    @PositiveOrZero(message = "Salary must be zero or positive")
    private BigDecimal salary;

    @Builder.Default
    private List<String> skills = new ArrayList<>();

    private String githubUsername;

    private String profileImage;

    @Builder.Default
    private Employee.EmployeeStatus status = Employee.EmployeeStatus.ACTIVE;
}
