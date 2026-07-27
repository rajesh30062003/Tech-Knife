package com.techknife.report.dto;

import com.techknife.report.entity.ExportFormat;
import com.techknife.report.entity.ScheduleFrequency;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReportScheduleDTO {
    private String id;

    @NotBlank(message = "Report ID is required")
    private String reportId;

    private String reportName;

    @NotNull(message = "Frequency is required")
    private ScheduleFrequency frequency;

    private String cronExpression;

    @NotNull(message = "Export format is required")
    private ExportFormat exportFormat;

    private List<String> emailRecipients;
    private boolean sendEmail;
    private boolean storeInCloud;

    private boolean active;
    private Instant lastRunTime;
    private Instant nextRunTime;
    private String lastExecutionStatus;

    private Instant createdAt;
    private Instant updatedAt;
    private String createdBy;
}
