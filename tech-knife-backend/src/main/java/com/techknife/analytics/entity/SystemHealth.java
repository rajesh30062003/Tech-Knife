package com.techknife.analytics.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "analytics_system_health")
public class SystemHealth {

    @Id
    private String id;

    private HealthStatus status;
    private double apiResponseTimeMs;
    private double databaseLatencyMs;
    private double memoryUsagePct;
    private double cpuUsagePct;
    private int activeBackgroundJobs;
    private int queueSize;
    private double storageUsageGb;
    private HealthStatus cloudinaryStatus;
    private String healthMessage;

    @CreatedDate
    private Instant timestamp;
}
