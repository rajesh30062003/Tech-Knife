package com.techknife.analytics.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "analytics_snapshots")
public class AnalyticsSnapshot {

    @Id
    private String id;

    private String snapshotName;
    private Instant snapshotDate;
    private KPICategory category;
    private Map<String, Object> aggregatedData;
    private long totalRecordsProcessed;

    @CreatedDate
    private Instant createdAt;
}
