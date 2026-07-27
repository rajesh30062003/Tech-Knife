package com.techknife.analytics.repository;

import com.techknife.analytics.entity.AnalyticsSnapshot;
import com.techknife.analytics.entity.KPICategory;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;

@Repository
public interface AnalyticsSnapshotRepository extends MongoRepository<AnalyticsSnapshot, String> {
    List<AnalyticsSnapshot> findByCategory(KPICategory category);
    List<AnalyticsSnapshot> findBySnapshotDateBetween(Instant start, Instant end);
}
