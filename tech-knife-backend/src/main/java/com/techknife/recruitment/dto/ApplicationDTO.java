package com.techknife.recruitment.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ApplicationDTO {

    private String id;

    @NotBlank(message = "Candidate ID is required")
    private String candidateId;

    @NotBlank(message = "Job posting ID is required")
    private String jobPostingId;

    private String candidateName;

    private String candidateEmail;

    private String jobTitle;

    private String department;

    private String status;

    private Instant appliedDate;

    private String notes;

    private Instant createdAt;

    private Instant updatedAt;
}
