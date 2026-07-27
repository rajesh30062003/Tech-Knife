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
public class InterviewFeedbackDTO {

    private String id;

    @NotBlank(message = "Interview ID is required")
    private String interviewId;

    @NotBlank(message = "Interviewer ID is required")
    private String interviewerId;

    private Integer technicalRating;

    private Integer communicationRating;

    private Integer problemSolvingRating;

    private Integer behaviorRating;

    private Double overallRating;

    private String recommendation;

    private String remarks;

    private Instant createdAt;

    private Instant updatedAt;
}
