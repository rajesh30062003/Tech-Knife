package com.techknife.recruitment.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OfferLetterDTO {

    private String id;

    @NotBlank(message = "Application ID is required")
    private String applicationId;

    private String candidateId;

    private String jobPostingId;

    private String candidateName;

    private String candidateEmail;

    private String jobTitle;

    private Double salary;

    private String designation;

    private LocalDate joiningDate;

    private LocalDate validityDate;

    private String acceptanceStatus;

    private String offerLetterUrl;

    private Instant createdAt;

    private Instant updatedAt;
}
