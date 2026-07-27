package com.techknife.report.repository;

import com.techknife.report.entity.ReportHistory;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReportHistoryRepository extends MongoRepository<ReportHistory, String> {
    List<ReportHistory> findByReportId(String reportId);
    List<ReportHistory> findByGeneratedBy(String generatedBy);
    List<ReportHistory> findByReportNameContainingIgnoreCase(String name);
}
