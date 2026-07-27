package com.techknife.analytics.repository;

import com.techknife.analytics.entity.KPICategory;
import com.techknife.analytics.entity.KPIHistory;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;

@Repository
public interface KPIHistoryRepository extends MongoRepository<KPIHistory, String> {
    List<KPIHistory> findByKpiIdOrderByRecordedAtAsc(String kpiId);
    List<KPIHistory> findByKpiCodeOrderByRecordedAtAsc(String kpiCode);
    List<KPIHistory> findByCategoryAndRecordedAtBetweenOrderByRecordedAtAsc(KPICategory category, Instant start, Instant end);
}
