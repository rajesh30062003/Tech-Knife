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
public class InterviewDTO {

    private String id;

    @NotBlank(message = "Application ID is required")
    private String applicationId;

    private String candidateId;

    private String jobPostingId;

    private String candidateName;

    private String jobTitle;

    private String interviewType;

    private String mode;

    private List<String> panelMembers;

    private Instant interviewTime;

    private String locationOrLink;

    private String result;

    private List<InterviewFeedbackDTO> feedbacks;

    private Instant createdAt;

    private Instant updatedAt;
}
