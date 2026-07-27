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
@Document(collection = "analytics_kpis")
public class KPI {

    @Id
    private String id;

    private String code;
    private String name;
    private String description;
    private KPICategory category;
    private String groupId;
    
    private Object currentValue;
    private Object previousValue;
    private Object targetValue;
    private Double percentageChange;
    
    private String unit;
    private String calculationFormula;
    private MetricDisplayType defaultDisplayType;
    private Map<String, Object> queryConfig;
    
    private boolean isSystem;
    private boolean active;

    @CreatedDate
    private Instant createdAt;

    @LastModifiedDate
    private Instant updatedAt;

    @CreatedBy
    private String createdBy;
}
