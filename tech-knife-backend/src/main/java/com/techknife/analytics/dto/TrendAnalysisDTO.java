package com.techknife.analytics.dto;

import com.techknife.analytics.entity.KPICategory;
import com.techknife.analytics.entity.TrendAnalysis;
import com.techknife.analytics.entity.TrendPeriod;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TrendAnalysisDTO {

    private String id;
    private String metricKey;
    private String title;
    private KPICategory category;
    private TrendPeriod period;
    private Double growthPercentage;
    private Double variance;
    private Double movingAverage;
    private List<TrendAnalysis.DataPoint> dataPoints;
    private Instant createdAt;
}
