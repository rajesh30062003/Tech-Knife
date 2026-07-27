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
@Document(collection = "analytics_system_metrics")
public class SystemMetric {

    @Id
    private String id;

    private String metricName;
    private Double value;
    private String unit;
    private String nodeOrService;

    @CreatedDate
    private Instant recordedAt;
}
