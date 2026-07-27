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
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

/**
 * Data Transfer Object for updating an existing Employee record.
 */
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

    private LocalDate joiningDate;

    private LocalDate probationEndDate;

    private LocalDate confirmationDate;

    private EmploymentType employmentType;

    @PositiveOrZero(message = "Salary must be zero or positive")
    private BigDecimal salary;

    private String salaryGrade;

    private String workLocation;

    private String shift;

    private String remarks;

    private List<String> skills;

    private List<Skill> skillDetails;

    private List<Education> education;

    private List<Experience> experience;

    private BankDetails bankDetails;

    private String pan;

    private String aadhaar;

    private String passport;

    private String drivingLicense;

    private String githubUsername;

    private String profileImage;

    private EmployeeStatus status;
}
