package com.techknife.recruitment.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CandidateDTO {

    private String id;

    private String candidateCode;

    @NotBlank(message = "First name is required")
    private String firstName;

    @NotBlank(message = "Last name is required")
    private String lastName;

    @NotBlank(message = "Email is required")
    @Email(message = "Invalid email format")
    private String email;

    private String phone;

    private String address;

    private String experience;

    private String currentCompany;

    private Double currentCtc;

    private Double expectedCtc;

    private String noticePeriod;

    private List<String> skills;

    private String resumeUrl;

    private String portfolioUrl;

    private String linkedInUrl;

    private String gitHubUrl;

    private String status;

    private Instant createdAt;

    private Instant updatedAt;
}
