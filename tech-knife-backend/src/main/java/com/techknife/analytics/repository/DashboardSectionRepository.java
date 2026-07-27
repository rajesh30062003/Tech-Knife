package com.techknife.analytics.repository;

import com.techknife.analytics.entity.DashboardSection;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DashboardSectionRepository extends MongoRepository<DashboardSection, String> {
    List<DashboardSection> findByDashboardId(String dashboardId);
}
