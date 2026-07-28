package com.techknife.employee.dto;

import com.techknife.employee.entity.Address;
import com.techknife.employee.entity.BankDetails;
import com.techknife.employee.entity.BloodGroup;
import com.techknife.employee.entity.Education;
import com.techknife.employee.entity.EmergencyContact;
import com.techknife.employee.entity.EmployeeStatus;
import com.techknife.employee.entity.EmploymentType;
import com.techknife.employee.entity.Experience;
import com.techknife.employee.entity.Gender;
import com.techknife.employee.entity.MaritalStatus;
import com.techknife.employee.entity.Skill;
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

/**
 * Data Transfer Object for creating a new Employee record.
 */
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

    private Gender gender;

    private LocalDate dob;

    private BloodGroup bloodGroup;

    private String nationality;

    private MaritalStatus maritalStatus;

    private EmergencyContact emergencyContact;

    private Address currentAddress;

    private Address permanentAddress;

    private String companyId;

    private String branchId;

    private String departmentId;

    private String designationId;

    private String managerId;

    private String teamId;

    @NotNull(message = "Joining date is mandatory")
    private LocalDate joiningDate;

    private LocalDate probationEndDate;

    private LocalDate confirmationDate;

    @NotNull(message = "Employment type is mandatory")
    private EmploymentType employmentType;

    @PositiveOrZero(message = "Salary must be zero or positive")
    private BigDecimal salary;

    private String salaryGrade;

    private String workLocation;

    private String shift;

    private String remarks;

    @Builder.Default
    private List<String> skills = new ArrayList<>();

    @Builder.Default
    private List<Skill> skillDetails = new ArrayList<>();

    @Builder.Default
    private List<Education> education = new ArrayList<>();

    @Builder.Default
    private List<Experience> experience = new ArrayList<>();

    private BankDetails bankDetails;

    private String pan;

    private String aadhaar;

    private String passport;

    private String drivingLicense;

    private String githubUsername;

    private String profileImage;

    @Builder.Default
    private EmployeeStatus status = EmployeeStatus.ACTIVE;

    public static class CreateEmployeeRequestBuilder {
        public CreateEmployeeRequestBuilder mobileNumber(String mobileNumber) {
            this.primaryMobile = mobileNumber;
            return this;
        }

        public CreateEmployeeRequestBuilder designation(String designation) {
            this.designationId = designation;
            return this;
        }

        public CreateEmployeeRequestBuilder department(String department) {
            this.departmentId = department;
            return this;
        }
    }

}


