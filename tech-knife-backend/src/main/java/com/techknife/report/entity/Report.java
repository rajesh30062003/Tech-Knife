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
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "report_reports")
public class Report {

    @Id
    private String id;

    private String name;
    private String description;
    private ReportCategoryType category;
    private String templateId;

    // Report Builder configurations
    private List<String> selectedColumns;
    private Map<String, Object> filters; // Custom filters e.g. status, department, etc.
    private List<SortConfig> sorting;
    private List<String> grouping;
    private List<AggregationConfig> aggregations;
    
    private Instant startDate;
    private Instant endDate;
    
    private int pageNumber;
    private int pageSize;

    private boolean saved;
    private boolean isTemplate;
    private String tags;

    @CreatedDate
    private Instant createdAt;

    @LastModifiedDate
    private Instant updatedAt;

    @CreatedBy
    private String createdBy;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class SortConfig {
        private String field;
        private String direction; // ASC or DESC
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class AggregationConfig {
        private String field;
        private String function; // SUM, AVG, COUNT, MIN, MAX
        private String alias;
    }
}
