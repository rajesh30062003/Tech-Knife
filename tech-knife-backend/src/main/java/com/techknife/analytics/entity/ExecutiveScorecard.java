package com.techknife.analytics.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;
import java.util.List;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "analytics_executive_scorecards")
public class ExecutiveScorecard {

    @Id
    private String id;

    private ExecutiveRole role;
    private String title;
    private String period;
    private Double overallPerformanceScore;
    
    private List<ScorecardMetric> keyMetrics;
    private Map<String, Object> summaryHighlights;

    @CreatedDate
    private Instant createdAt;

    @LastModifiedDate
    private Instant updatedAt;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ScorecardMetric {
        private String metricKey;
        private String name;
        private KPICategory category;
        private Object value;
        private Object target;
        private Double percentageAchieved;
        private String status; // ON_TRACK, AT_RISK, BEHIND, EXCEEDED
    }
}
