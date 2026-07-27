package com.techknife.analytics.repository;

import com.techknife.analytics.entity.SystemMetric;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;

@Repository
public interface SystemMetricRepository extends MongoRepository<SystemMetric, String> {
    List<SystemMetric> findByMetricNameAndRecordedAtBetweenOrderByRecordedAtAsc(String metricName, Instant start, Instant end);
    List<SystemMetric> findTop50ByMetricNameOrderByRecordedAtDesc(String metricName);
}
