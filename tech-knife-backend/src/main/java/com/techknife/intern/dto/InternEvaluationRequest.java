package com.techknife.intern.dto;

import com.techknife.intern.entity.EvaluationRecommendation;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class InternEvaluationRequest {

    private String evaluatorId;

    @Min(1) @Max(10)
    private double technicalSkillsScore;

    @Min(1) @Max(10)
    private double communicationScore;

    @Min(1) @Max(10)
    private double problemSolvingScore;

    @Min(1) @Max(10)
    private double attendanceScore;

    @Min(1) @Max(10)
    private double disciplineScore;

    @Min(1) @Max(10)
    private double learningAbilityScore;

    private String remarks;

    private EvaluationRecommendation recommendation;
}
