package com.techknife.employee.entity;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.CompoundIndexes;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * MongoDB Document representing an Employee record in the Tech Knife Enterprise Management System.
 * Stores comprehensive employee demographic, organizational, financial, and contact details.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "employees")
@CompoundIndexes({
    @CompoundIndex(name = "idx_emp_dept_status", def = "{'departmentId': 1, 'status': 1}"),
    @CompoundIndex(name = "idx_emp_name", def = "{'firstName': 1, 'lastName': 1}")
})
public class Employee {

    @Id
    private String id;

    @NotBlank(message = "Employee ID is mandatory")
    @Indexed(unique = true)
    private String employeeId;

    @NotBlank(message = "Official email is mandatory")
    @Email(message = "Invalid official email format")
    @Indexed(unique = true)
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

    @Indexed
    private String companyId;

    @Indexed
    private String branchId;

    @Indexed
    private String departmentId;

    @Indexed
    private String designationId;

    @Indexed
    private String managerId;

    @Indexed
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

    @Builder.Default
    private List<EmployeeDocument> documents = new ArrayList<>();

    private BankDetails bankDetails;

    private String pan;

    private String aadhaar;

    private String passport;

    private String drivingLicense;

    private String githubUsername;

    private String profileImage;

    @NotNull(message = "Employee status is mandatory")
    @Indexed
    private EmployeeStatus status;

    @CreatedDate
    private Instant createdAt;

    @LastModifiedDate
    private Instant updatedAt;

    @CreatedBy
    private String createdBy;

    @LastModifiedBy
    private String updatedBy;
}
