package com.techknife.report.service;

import com.techknife.report.dto.ReportExecutionDTO;
import com.techknife.report.entity.ExecutionStatus;
import com.techknife.report.entity.ExportFormat;

import java.util.List;

public interface ReportExecutionService {
    ReportExecutionDTO queueExecution(String reportId, String scheduleId, ExportFormat format);
    ReportExecutionDTO getExecutionStatus(String executionId);
    List<ReportExecutionDTO> getExecutionsByReport(String reportId);
    List<ReportExecutionDTO> getExecutionsByStatus(ExecutionStatus status);
    ReportExecutionDTO retryFailedJob(String executionId);
    void cancelExecution(String executionId);
    void processQueuedExecutions();
}
