package com.techknife.analytics.repository;

import com.techknife.analytics.entity.PerformanceMetric;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;

@Repository
public interface PerformanceMetricRepository extends MongoRepository<PerformanceMetric, String> {
    List<PerformanceMetric> findByTimestampBetween(Instant start, Instant end);
    List<PerformanceMetric> findTop50ByOrderByExecutionTimeMsDesc();
}
