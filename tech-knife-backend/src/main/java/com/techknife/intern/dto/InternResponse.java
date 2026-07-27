package com.techknife.intern.dto;

import com.techknife.employee.entity.Gender;
import com.techknife.intern.entity.InternStatus;
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
public class InternResponse {
    private String id;
    private String internCode;
    private String officialEmail;
    private String personalEmail;
    private String firstName;
    private String lastName;
    private String fullName;
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
    private List<String> skills;

    private String companyId;
    private String branchId;
    private String departmentId;
    private String mentorId;

    private LocalDate joiningDate;
    private LocalDate endDate;
    private BigDecimal stipend;
    private InternStatus status;

    private Boolean certificateGenerated;
    private String certificateId;
    private Boolean convertedToEmployee;
    private String convertedEmployeeId;
    private String remarks;

    private Instant createdAt;
    private Instant updatedAt;
    private String createdBy;
    private String updatedBy;
}
