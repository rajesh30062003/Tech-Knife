package com.techknife.analytics.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "analytics_trend_analyses")
public class TrendAnalysis {

    @Id
    private String id;

    private String metricKey;
    private String title;
    private KPICategory category;
    private TrendPeriod period;
    
    private Double growthPercentage;
    private Double variance;
    private Double movingAverage;
    
    private List<DataPoint> dataPoints;

    @CreatedDate
    private Instant createdAt;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class DataPoint {
        private String label;
        private Instant timestamp;
        private Double value;
        private Double previousValue;
        private Double movingAverage;
    }
}
