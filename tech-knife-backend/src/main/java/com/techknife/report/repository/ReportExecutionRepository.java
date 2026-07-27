package com.techknife.report.repository;

import com.techknife.report.entity.ExecutionStatus;
import com.techknife.report.entity.ReportExecution;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReportExecutionRepository extends MongoRepository<ReportExecution, String> {
    List<ReportExecution> findByReportId(String reportId);
    List<ReportExecution> findByScheduleId(String scheduleId);
    List<ReportExecution> findByStatus(ExecutionStatus status);
}
