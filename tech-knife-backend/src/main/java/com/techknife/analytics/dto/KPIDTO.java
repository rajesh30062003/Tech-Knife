package com.techknife.analytics.dto;

import com.techknife.analytics.entity.KPICategory;
import com.techknife.analytics.entity.MetricDisplayType;
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
public class KPIDTO {

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
    private Instant createdAt;
    private Instant updatedAt;
    private String createdBy;
}
