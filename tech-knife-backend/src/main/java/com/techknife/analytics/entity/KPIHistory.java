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
@Document(collection = "analytics_kpi_histories")
public class KPIHistory {

    @Id
    private String id;

    private String kpiId;
    private String kpiCode;
    private KPICategory category;
    private Object value;
    private Object targetValue;
    private Instant recordedAt;

    @CreatedDate
    private Instant createdAt;
}
