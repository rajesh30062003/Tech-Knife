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
public class ReportHistoryDTO {
    private String id;
    private String reportId;
    private String reportName;
    private String executionId;
    private String generatedBy;
    private Instant generatedAt;
    private ExecutionStatus executionStatus;
    private String downloadLink;
    private long fileSize;
    private long durationMs;
    private ExportFormat fileFormat;
    private Instant createdAt;
}
