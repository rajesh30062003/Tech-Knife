package com.techknife.analytics.repository;

import com.techknife.analytics.entity.AnalyticsDashboard;
import com.techknife.analytics.entity.ExecutiveRole;
import com.techknife.analytics.entity.KPICategory;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AnalyticsDashboardRepository extends MongoRepository<AnalyticsDashboard, String> {
    Optional<AnalyticsDashboard> findByCode(String code);
    Optional<AnalyticsDashboard> findByRole(ExecutiveRole role);
    List<AnalyticsDashboard> findByCategory(KPICategory category);
    List<AnalyticsDashboard> findByActiveTrue();
    boolean existsByCode(String code);
}
