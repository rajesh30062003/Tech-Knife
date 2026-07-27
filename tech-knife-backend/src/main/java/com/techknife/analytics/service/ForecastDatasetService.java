package com.techknife.analytics.service;

import com.techknife.analytics.dto.ForecastDatasetDTO;
import com.techknife.analytics.entity.ForecastType;
import com.techknife.analytics.entity.TrendPeriod;

import java.util.List;

public interface ForecastDatasetService {
    List<ForecastDatasetDTO> getAllForecastDatasets();
    ForecastDatasetDTO getForecastDatasetByType(ForecastType forecastType);
    ForecastDatasetDTO generateForecastDataset(ForecastType forecastType, TrendPeriod granularity, int forecastPeriods);
}
