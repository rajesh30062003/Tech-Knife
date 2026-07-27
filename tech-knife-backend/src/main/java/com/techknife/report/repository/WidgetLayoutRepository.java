package com.techknife.report.repository;

import com.techknife.report.entity.DashboardType;
import com.techknife.report.entity.WidgetLayout;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface WidgetLayoutRepository extends MongoRepository<WidgetLayout, String> {
    List<WidgetLayout> findByUserId(String userId);
    Optional<WidgetLayout> findByDashboardTypeAndDefaultLayoutTrue(DashboardType dashboardType);
    Optional<WidgetLayout> findByUserIdAndDashboardType(String userId, DashboardType dashboardType);
}
