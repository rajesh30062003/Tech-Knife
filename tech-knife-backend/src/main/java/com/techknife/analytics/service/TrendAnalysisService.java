package com.techknife.analytics.service;

import com.techknife.analytics.dto.TrendAnalysisDTO;
import com.techknife.analytics.entity.KPICategory;
import com.techknife.analytics.entity.TrendPeriod;

import java.util.List;

public interface TrendAnalysisService {
    List<TrendAnalysisDTO> getAllTrendAnalyses();
    TrendAnalysisDTO getTrendAnalysisByMetricAndPeriod(String metricKey, TrendPeriod period);
    TrendAnalysisDTO generateTrendAnalysis(String metricKey, KPICategory category, TrendPeriod period);
}
