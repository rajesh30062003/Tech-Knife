package com.techknife.report.dto;

import com.techknife.report.entity.ReportCategoryType;
import com.techknife.report.entity.WidgetType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DashboardWidgetDTO {
    private String id;

    @NotBlank(message = "Title is required")
    private String title;

    private String description;

    @NotNull(message = "Widget type is required")
    private WidgetType widgetType;

    private ReportCategoryType category;
    private String reportId;
    private String kpiMetricKey;
    private Map<String, Object> queryConfig;
    private int refreshIntervalSeconds;
    private boolean active;

    private Instant createdAt;
    private Instant updatedAt;
    private String createdBy;
}
