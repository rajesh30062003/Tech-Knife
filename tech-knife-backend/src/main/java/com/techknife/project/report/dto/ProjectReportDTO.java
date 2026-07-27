package com.techknife.project.report.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.List;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProjectReportDTO {

    private String reportType; // PROJECT_STATUS, SPRINT, TIMESHEET, TASK, PRODUCTIVITY, RESOURCE_UTILIZATION, RISK, COMPLETION_FORECAST
    private String projectId;
    private Instant generatedAt;
    private String generatedBy;
    private Map<String, Object> summaryMetrics;
    private List<Map<String, Object>> reportDetails;
    private String forecastedCompletionDate;
}
