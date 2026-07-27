package com.techknife.report.dto;

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
public class ExportHistoryDTO {
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
    private Instant createdAt;
}
