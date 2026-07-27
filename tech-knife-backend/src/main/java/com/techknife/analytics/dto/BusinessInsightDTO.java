package com.techknife.analytics.dto;

import com.techknife.analytics.entity.InsightSeverity;
import com.techknife.analytics.entity.InsightType;
import com.techknife.analytics.entity.KPICategory;
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
public class BusinessInsightDTO {

    private String id;
    private InsightType insightType;
    private InsightSeverity severity;
    private KPICategory category;
    private String title;
    private String description;
    private String recommendation;
    private Double impactScore;
    private String targetEntityId;
    private String targetEntityName;
    private Map<String, Object> metrics;
    private boolean acknowledged;
    private Instant createdAt;
}
