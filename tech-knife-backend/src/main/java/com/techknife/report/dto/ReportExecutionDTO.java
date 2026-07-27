package com.techknife.report.dto;

import com.techknife.report.entity.ExecutionStatus;
import com.techknife.report.entity.ExportFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReportExecutionDTO {
    private String id;
    private String reportId;
    private String reportName;
    private String scheduleId;
    private ExportFormat format;
    private ExecutionStatus status;
    private int progressPercentage;
    private String errorMessage;
    private int retryCount;
    private int maxRetries;

    private String fileUrl;
    private long fileSize;
    private long durationMs;

    private Instant startedAt;
    private Instant completedAt;

    private Instant createdAt;
    private Instant updatedAt;
    private String createdBy;
}
