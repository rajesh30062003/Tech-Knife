package com.techknife.recruitment.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "interview_feedbacks")
public class InterviewFeedback {

    @Id
    private String id;

    @Indexed
    private String interviewId;

    private String interviewerId;

    private Integer technicalRating; // 1 to 5

    private Integer communicationRating; // 1 to 5

    private Integer problemSolvingRating; // 1 to 5

    private Integer behaviorRating; // 1 to 5

    private Double overallRating;

    private String recommendation; // RECOMMEND, STRONG_RECOMMEND, REJECT, HOLD

    private String remarks;

    @CreatedDate
    private Instant createdAt;

    @LastModifiedDate
    private Instant updatedAt;
}
