package com.techknife.analytics.entity;

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
@Document(collection = "analytics_dashboard_metrics")
public class DashboardMetric {

    @Id
    private String id;

    private String metricKey;
    private String title;
    private String description;
    private KPICategory category;
    private MetricDisplayType displayType;
    private String kpiId;
    
    private Object currentValue;
    private Object previousValue;
    private Double percentageChange;
    private String unit;
    private String icon;
    private String colorHex;

    private Map<String, Object> queryConfig;
    private int refreshIntervalSeconds;
    private int displayOrder;
    private boolean active;

    @CreatedDate
    private Instant createdAt;

    @LastModifiedDate
    private Instant updatedAt;

    @CreatedBy
    private String createdBy;
}
