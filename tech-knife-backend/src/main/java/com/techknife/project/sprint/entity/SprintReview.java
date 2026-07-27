package com.techknife.project.sprint.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "sprint_reviews")
public class SprintReview {

    @Id
    private String id;

    private String sprintId;

    private String demonstratedFeatures;

    private String stakeholderFeedback;

    private String reviewedBy;

    private Instant reviewDate;

    @CreatedDate
    private Instant createdAt;
}
