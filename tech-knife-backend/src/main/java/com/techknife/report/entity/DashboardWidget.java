package com.techknife.report.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "report_dashboard_widgets")
public class DashboardWidget {

    @Id
    private String id;

    private String title;
    private String description;
    private WidgetType widgetType;
    private ReportCategoryType category;
    private String reportId;
    private String kpiMetricKey; // e.g. EMPLOYEE_COUNT, REVENUE, PROFIT, ATTENDANCE_PCT
    private Map<String, Object> queryConfig;
    private int refreshIntervalSeconds;
    private boolean active;

    @CreatedDate
    private Instant createdAt;

    @LastModifiedDate
    private Instant updatedAt;

    @CreatedBy
    private String createdBy;
}
