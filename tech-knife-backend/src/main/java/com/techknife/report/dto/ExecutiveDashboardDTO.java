package com.techknife.report.dto;

import com.techknife.report.entity.DashboardType;
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
public class ExecutiveDashboardDTO {
    private DashboardType dashboardType;
    private String title;
    private String description;
    private Instant generatedAt;
    private List<KpiReportDTO> kpis;
    private List<DashboardWidgetDTO> widgets;
    private Map<String, Object> summaryMetrics;
    private WidgetLayoutDTO layout;
}
