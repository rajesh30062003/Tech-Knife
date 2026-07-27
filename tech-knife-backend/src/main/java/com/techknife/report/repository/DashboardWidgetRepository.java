package com.techknife.report.repository;

import com.techknife.report.entity.DashboardWidget;
import com.techknife.report.entity.ReportCategoryType;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DashboardWidgetRepository extends MongoRepository<DashboardWidget, String> {
    List<DashboardWidget> findByCategory(ReportCategoryType category);
    List<DashboardWidget> findByActiveTrue();
    List<DashboardWidget> findByTitleContainingIgnoreCase(String title);
}
