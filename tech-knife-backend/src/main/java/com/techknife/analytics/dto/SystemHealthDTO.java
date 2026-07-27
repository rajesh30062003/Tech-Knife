package com.techknife.analytics.dto;

import com.techknife.analytics.entity.HealthStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SystemHealthDTO {

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
    private Instant timestamp;
}
