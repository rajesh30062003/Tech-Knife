package com.techknife.analytics.service;

import com.techknife.analytics.dto.BusinessInsightDTO;
import com.techknife.analytics.entity.InsightSeverity;
import com.techknife.analytics.entity.KPICategory;

import java.util.List;

public interface BusinessInsightService {
    List<BusinessInsightDTO> getAllInsights();
    List<BusinessInsightDTO> getInsightsBySeverity(InsightSeverity severity);
    List<BusinessInsightDTO> getInsightsByCategory(KPICategory category);
    BusinessInsightDTO acknowledgeInsight(String id);
    List<BusinessInsightDTO> generateInsights();
}
