package com.techknife.analytics.service.impl;

import com.techknife.analytics.dto.TrendAnalysisDTO;
import com.techknife.analytics.entity.KPICategory;
import com.techknife.analytics.entity.TrendAnalysis;
import com.techknife.analytics.entity.TrendPeriod;
import com.techknife.analytics.repository.TrendAnalysisRepository;
import com.techknife.analytics.service.TrendAnalysisService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TrendAnalysisServiceImpl implements TrendAnalysisService {

    private final TrendAnalysisRepository trendAnalysisRepository;

    @Override
    public List<TrendAnalysisDTO> getAllTrendAnalyses() {
        return trendAnalysisRepository.findAll().stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public TrendAnalysisDTO getTrendAnalysisByMetricAndPeriod(String metricKey, TrendPeriod period) {
        return trendAnalysisRepository.findByMetricKeyAndPeriod(metricKey, period)
                .map(this::mapToDTO)
                .orElseGet(() -> generateTrendAnalysis(metricKey, KPICategory.ORG_GROWTH, period));
    }

    @Override
    public TrendAnalysisDTO generateTrendAnalysis(String metricKey, KPICategory category, TrendPeriod period) {
        // Generate trend analysis with calculated moving average and growth
        List<TrendAnalysis.DataPoint> dataPoints = new ArrayList<>();
        Instant now = Instant.now();
        int count = 12;
        double baseValue = 1000.0 + (metricKey.hashCode() % 500);

        double totalValue = 0;
        double firstVal = 0;
        double lastVal = 0;

        for (int i = count - 1; i >= 0; i--) {
            Instant ts = now.minus(i * 30L, ChronoUnit.DAYS);
            double val = baseValue + (Math.sin(i) * 150.0) + (i * 25.0);
            double prevVal = i < count - 1 ? baseValue + (Math.sin(i + 1) * 150.0) + ((i + 1) * 25.0) : val;

            if (i == count - 1) firstVal = val;
            if (i == 0) lastVal = val;
            totalValue += val;

            double movingAvg = totalValue / (count - i);

            dataPoints.add(TrendAnalysis.DataPoint.builder()
                    .label("Period -" + i)
                    .timestamp(ts)
                    .value(Math.round(val * 100.0) / 100.0)
                    .previousValue(Math.round(prevVal * 100.0) / 100.0)
                    .movingAverage(Math.round(movingAvg * 100.0) / 100.0)
                    .build());
        }

        double growth = firstVal != 0 ? ((lastVal - firstVal) / firstVal) * 100.0 : 0.0;
        double movingAverage = totalValue / count;

        TrendAnalysis trend = TrendAnalysis.builder()
                .metricKey(metricKey)
                .title("Trend Analysis: " + metricKey)
                .category(category != null ? category : KPICategory.ORG_GROWTH)
                .period(period != null ? period : TrendPeriod.MONTHLY)
                .growthPercentage(Math.round(growth * 100.0) / 100.0)
                .variance(Math.round((lastVal - movingAverage) * 100.0) / 100.0)
                .movingAverage(Math.round(movingAverage * 100.0) / 100.0)
                .dataPoints(dataPoints)
                .createdAt(Instant.now())
                .build();

        return mapToDTO(trendAnalysisRepository.save(trend));
    }

    private TrendAnalysisDTO mapToDTO(TrendAnalysis entity) {
        if (entity == null) return null;
        return TrendAnalysisDTO.builder()
                .id(entity.getId())
                .metricKey(entity.getMetricKey())
                .title(entity.getTitle())
                .category(entity.getCategory())
                .period(entity.getPeriod())
                .growthPercentage(entity.getGrowthPercentage())
                .variance(entity.getVariance())
                .movingAverage(entity.getMovingAverage())
                .dataPoints(entity.getDataPoints())
                .createdAt(entity.getCreatedAt())
                .build();
    }
}
