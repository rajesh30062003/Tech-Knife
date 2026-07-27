package com.techknife.analytics.repository;

import com.techknife.analytics.entity.DashboardMetric;
import com.techknife.analytics.entity.KPICategory;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface DashboardMetricRepository extends MongoRepository<DashboardMetric, String> {
    Optional<DashboardMetric> findByMetricKey(String metricKey);
    List<DashboardMetric> findByCategory(KPICategory category);
    List<DashboardMetric> findByActiveTrue();
}
