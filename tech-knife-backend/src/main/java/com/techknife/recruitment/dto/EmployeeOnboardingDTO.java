package com.techknife.recruitment.dto;

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
public class EmployeeOnboardingDTO {

    private String id;

    @NotBlank(message = "Candidate ID is required")
    private String candidateId;

    private String applicationId;

    private String offerLetterId;

    private String candidateName;

    private String documentCollectionStatus;

    private List<String> collectedDocuments;

    private String verificationStatus;

    private String accountCreationStatus;

    private String convertedEmployeeId;

    private String onboardingStatus;

    private Instant createdAt;

    private Instant updatedAt;
}
