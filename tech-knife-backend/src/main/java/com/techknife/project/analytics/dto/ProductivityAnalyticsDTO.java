package com.techknife.project.analytics.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductivityAnalyticsDTO {

    private String projectId;
    private Double totalHoursLogged;
    private Double billableHoursLogged;
    private Double nonBillableHoursLogged;
    private Double taskCompletionRatePercentage;
    private Double sprintVelocityAverage;
    private Integer totalStoryPointsCompleted;
    private Double averageCycleTimeInDays;
    private Double averageLeadTimeInDays;
    private Map<String, Double> employeeProductivityHours; // EmployeeId -> Hours
    private Map<String, Double> teamProductivityByMonth; // Month -> Hours
}
