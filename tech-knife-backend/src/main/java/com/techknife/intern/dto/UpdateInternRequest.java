package com.techknife.intern.dto;

import com.techknife.employee.entity.Gender;
import com.techknife.intern.entity.InternStatus;
import jakarta.validation.constraints.Email;
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
public class UpdateInternRequest {

    @Email(message = "Invalid personal email format")
    private String personalEmail;

    private String firstName;
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
    private String remarks;
}
