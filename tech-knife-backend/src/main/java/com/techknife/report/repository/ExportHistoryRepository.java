package com.techknife.report.repository;

import com.techknife.report.entity.ExportHistory;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ExportHistoryRepository extends MongoRepository<ExportHistory, String> {
    List<ExportHistory> findByReportId(String reportId);
    List<ExportHistory> findByExportedBy(String exportedBy);
}
