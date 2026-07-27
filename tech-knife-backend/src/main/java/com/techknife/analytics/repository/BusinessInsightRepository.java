package com.techknife.analytics.repository;

import com.techknife.analytics.entity.BusinessInsight;
import com.techknife.analytics.entity.InsightSeverity;
import com.techknife.analytics.entity.KPICategory;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BusinessInsightRepository extends MongoRepository<BusinessInsight, String> {
    List<BusinessInsight> findBySeverity(InsightSeverity severity);
    List<BusinessInsight> findByCategory(KPICategory category);
    List<BusinessInsight> findByAcknowledgedFalse();
}
