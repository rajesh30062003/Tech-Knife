package com.techknife.report.repository;

import com.techknife.report.entity.ReportSchedule;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReportScheduleRepository extends MongoRepository<ReportSchedule, String> {
    List<ReportSchedule> findByReportId(String reportId);
    List<ReportSchedule> findByActiveTrue();
    boolean existsByReportIdAndCronExpressionAndActiveTrue(String reportId, String cronExpression);
    List<ReportSchedule> findByReportNameContainingIgnoreCase(String name);
}
