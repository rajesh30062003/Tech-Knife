package com.techknife.report.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "report_histories")
public class ReportHistory {

    @Id
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

    @CreatedDate
    private Instant createdAt;
}
