package com.techknife.report.repository;

import com.techknife.report.entity.ExecutionStatus;
import com.techknife.report.entity.ExportJob;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ExportJobRepository extends MongoRepository<ExportJob, String> {
    List<ExportJob> findByReportId(String reportId);
    List<ExportJob> findByStatus(ExecutionStatus status);
}
