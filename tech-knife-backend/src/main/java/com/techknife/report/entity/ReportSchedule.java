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
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "report_schedules")
public class ReportSchedule {

    @Id
    private String id;

    private String reportId;
    private String reportName;
    private ScheduleFrequency frequency;
    private String cronExpression;
    private ExportFormat exportFormat;

    private List<String> emailRecipients;
    private boolean sendEmail;
    private boolean storeInCloud;

    private boolean active;
    private Instant lastRunTime;
    private Instant nextRunTime;
    private String lastExecutionStatus;

    @CreatedDate
    private Instant createdAt;

    @LastModifiedDate
    private Instant updatedAt;

    @CreatedBy
    private String createdBy;
}
