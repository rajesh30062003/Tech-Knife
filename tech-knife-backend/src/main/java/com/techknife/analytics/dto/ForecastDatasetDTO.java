package com.techknife.analytics.dto;

import com.techknife.analytics.entity.ForecastDataset;
import com.techknife.analytics.entity.ForecastType;
import com.techknife.analytics.entity.TrendPeriod;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.List;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ForecastDatasetDTO {

    private String id;
    private ForecastType forecastType;
    private String name;
    private String description;
    private Instant startDate;
    private Instant endDate;
    private TrendPeriod granularity;

    private List<ForecastDataset.ForecastDataPoint> historicalPoints;
    private List<ForecastDataset.ForecastDataPoint> projectedPoints;
    private Map<String, Object> metadata;
    private Instant createdAt;
    private String createdBy;
}
