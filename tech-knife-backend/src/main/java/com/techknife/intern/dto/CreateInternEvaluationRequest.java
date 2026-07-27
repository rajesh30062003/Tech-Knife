package com.techknife.intern.dto;

import com.techknife.intern.entity.EvaluationRecommendation;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateInternEvaluationRequest {

    private String evaluatorId;
    private LocalDate evaluationDate;

    @NotNull @Min(0) @Max(10)
    private Double technicalSkills;

    @NotNull @Min(0) @Max(10)
    private Double communication;

    @NotNull @Min(0) @Max(10)
    private Double problemSolving;

    @NotNull @Min(0) @Max(10)
    private Double attendance;

    @NotNull @Min(0) @Max(10)
    private Double discipline;

    @NotNull @Min(0) @Max(10)
    private Double learningAbility;

    private String remarks;
    private EvaluationRecommendation recommendation;
}
