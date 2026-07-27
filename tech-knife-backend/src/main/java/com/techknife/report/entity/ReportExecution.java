package com.techknife.report.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "report_executions")
public class ReportExecution {

    @Id
    private String id;

    private String reportId;
    private String reportName;
    private String scheduleId;
    private ExportFormat format;
    private ExecutionStatus status;
    private int progressPercentage; // 0 to 100
    private String errorMessage;
    private int retryCount;
    private int maxRetries;

    private String fileUrl;
    private long fileSize; // bytes
    private long durationMs; // execution duration in milliseconds

    private Instant startedAt;
    private Instant completedAt;

    @CreatedDate
    private Instant createdAt;

    @LastModifiedDate
    private Instant updatedAt;

    @CreatedBy
    private String createdBy;
}
