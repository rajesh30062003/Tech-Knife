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
@Document(collection = "report_export_histories")
public class ExportHistory {

    @Id
    private String id;

    private String jobId;
    private String reportId;
    private String reportName;
    private ExportFormat format;
    private String fileUrl;
    private long fileSize;
    private long durationMs;
    private String exportedBy;
    private Instant exportedAt;

    @CreatedDate
    private Instant createdAt;
}
