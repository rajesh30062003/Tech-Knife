package com.techknife.analytics.dto;

import com.techknife.analytics.entity.KPICategory;
import com.techknife.analytics.entity.MetricDisplayType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DashboardMetricDTO {

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
}
