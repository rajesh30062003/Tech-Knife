package com.techknife.report.repository;

import com.techknife.report.entity.Report;
import com.techknife.report.entity.ReportCategoryType;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReportRepository extends MongoRepository<Report, String> {
    List<Report> findByCategory(ReportCategoryType category);
    List<Report> findByCreatedBy(String createdBy);
    List<Report> findBySavedTrue();
    List<Report> findByNameContainingIgnoreCase(String name);
}
