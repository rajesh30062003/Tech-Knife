package com.techknife.analytics.repository;

import com.techknife.analytics.entity.KPICategory;
import com.techknife.analytics.entity.TrendAnalysis;
import com.techknife.analytics.entity.TrendPeriod;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TrendAnalysisRepository extends MongoRepository<TrendAnalysis, String> {
    Optional<TrendAnalysis> findByMetricKeyAndPeriod(String metricKey, TrendPeriod period);
    List<TrendAnalysis> findByCategory(KPICategory category);
}
