package com.techknife.intern.dto;

import com.techknife.intern.entity.EvaluationRecommendation;
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
public class InternEvaluationResponse {
    private String id;
    private String internId;
    private String evaluatorId;
    private String evaluatorName;
    private LocalDate evaluationDate;
    private Double technicalSkills;
    private Double communication;
    private Double problemSolving;
    private Double attendance;
    private Double discipline;
    private Double learningAbility;
    private Double overallScore;
    private String remarks;
    private EvaluationRecommendation recommendation;
    private Instant createdAt;
}
