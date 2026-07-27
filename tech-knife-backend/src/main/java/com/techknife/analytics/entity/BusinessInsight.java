package com.techknife.analytics.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "analytics_business_insights")
public class BusinessInsight {

    @Id
    private String id;

    private InsightType insightType;
    private InsightSeverity severity;
    private KPICategory category;
    private String title;
    private String description;
    private String recommendation;
    private Double impactScore; // 0 to 100
    private String targetEntityId;
    private String targetEntityName;
    private Map<String, Object> metrics;
    private boolean acknowledged;

    @CreatedDate
    private Instant createdAt;
}
