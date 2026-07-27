package com.techknife.intern.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;
import java.time.LocalDate;

/**
 * MongoDB Document for Intern Evaluation and Review scoring.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "intern_evaluations")
public class InternEvaluation {

    @Id
    private String id;

    @Indexed
    private String internId;

    @Indexed
    private String evaluatorId; // Mentor or Manager Employee ID

    private LocalDate evaluationDate;

    private Double technicalSkills; // Score out of 10

    private Double communication; // Score out of 10

    private Double problemSolving; // Score out of 10

    private Double attendance; // Score out of 10

    private Double discipline; // Score out of 10

    private Double learningAbility; // Score out of 10

    private Double overallScore; // Calculated average or weighted total

    private String remarks;

    private EvaluationRecommendation recommendation;

    @CreatedDate
    private Instant createdAt;

    @LastModifiedDate
    private Instant updatedAt;

    @CreatedBy
    private String createdBy;

    @LastModifiedBy
    private String updatedBy;
}
