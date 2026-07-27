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
@Document(collection = "report_templates")
public class ReportTemplate {

    @Id
    private String id;

    private String name;
    private String description;
    private ReportCategoryType category;
    private String code; // Unique code to detect duplicate templates
    private List<String> defaultColumns;
    private Map<String, Object> defaultFilters;
    private String defaultSortField;
    private String defaultSortDirection;
    private List<String> availableColumns;
    private boolean systemTemplate;
    private int usageCount;

    @CreatedDate
    private Instant createdAt;

    @LastModifiedDate
    private Instant updatedAt;

    @CreatedBy
    private String createdBy;
}
