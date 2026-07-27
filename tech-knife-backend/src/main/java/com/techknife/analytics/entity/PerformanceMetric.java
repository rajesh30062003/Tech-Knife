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
@Document(collection = "analytics_performance_metrics")
public class PerformanceMetric {

    @Id
    private String id;

    private String endpointOrOperation;
    private String method;
    private long executionTimeMs;
    private int httpStatusCode;
    private boolean cacheHit;
    private String clientIp;

    @CreatedDate
    private Instant timestamp;
}
