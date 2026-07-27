package com.techknife.project.sprint.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SprintReviewDTO {

    private String id;
    private String sprintId;
    private String demonstratedFeatures;
    private String stakeholderFeedback;
    private String reviewedBy;
    private Instant reviewDate;
    private Instant createdAt;
}
