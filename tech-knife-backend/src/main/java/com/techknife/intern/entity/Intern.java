package com.techknife.intern.entity;

import com.techknife.employee.entity.Gender;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * MongoDB Document representing an Intern record in the Tech Knife Enterprise Platform.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "interns")
public class Intern {

    @Id
    private String id;

    @NotBlank(message = "Intern code is mandatory")
    @Indexed(unique = true)
    private String internCode;

    @NotBlank(message = "Official email is mandatory")
    @Email(message = "Invalid official email format")
    @Indexed(unique = true)
    private String officialEmail;

    @Email(message = "Invalid personal email format")
    private String personalEmail;

    @NotBlank(message = "First name is mandatory")
    private String firstName;

    @NotBlank(message = "Last name is mandatory")
    private String lastName;

    private String phone;

    private Gender gender;

    private LocalDate dob;

    private String college;

    private String university;

    private String course;

    private Integer semester;

    private Integer passingYear;

    private String resumeUrl;

    private String resumePublicId;

    private String githubUrl;

    private String linkedInUrl;

    private String portfolioUrl;

    @Builder.Default
    private List<String> skills = new ArrayList<>();

    @Indexed
    private String companyId;

    @Indexed
    private String branchId;

    @Indexed
    private String departmentId;

    @Indexed
    private String mentorId;

    @NotNull(message = "Joining date is mandatory")
    private LocalDate joiningDate;

    private LocalDate endDate;

    private BigDecimal stipend;

    @Builder.Default
    private InternStatus status = InternStatus.ACTIVE;

    @Builder.Default
    private Boolean certificateGenerated = false;

    private String certificateId;

    @Builder.Default
    private Boolean convertedToEmployee = false;

    private String convertedEmployeeId;

    private String remarks;

    @CreatedDate
    private Instant createdAt;

    @LastModifiedDate
    private Instant updatedAt;

    @CreatedBy
    private String createdBy;

    @LastModifiedBy
    private String updatedBy;
}
