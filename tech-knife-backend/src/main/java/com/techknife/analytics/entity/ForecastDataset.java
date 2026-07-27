package com.techknife.analytics.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;
import java.util.List;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "analytics_forecast_datasets")
public class ForecastDataset {

    @Id
    private String id;

    private ForecastType forecastType;
    private String name;
    private String description;
    private Instant startDate;
    private Instant endDate;
    private TrendPeriod granularity;

    private List<ForecastDataPoint> historicalPoints;
    private List<ForecastDataPoint> projectedPoints;
    private Map<String, Object> metadata;

    @CreatedDate
    private Instant createdAt;

    @CreatedBy
    private String createdBy;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ForecastDataPoint {
        private String periodLabel;
        private Instant date;
        private Double actualValue;
        private Double projectedValue;
        private Double lowerBound;
        private Double upperBound;
        private Double confidenceScore;
    }
}
