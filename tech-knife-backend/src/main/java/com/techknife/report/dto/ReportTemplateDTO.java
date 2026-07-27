package com.techknife.report.dto;

import com.techknife.report.entity.ReportCategoryType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.List;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReportTemplateDTO {
    private String id;

    @NotBlank(message = "Template name is required")
    private String name;

    private String description;

    @NotNull(message = "Category is required")
    private ReportCategoryType category;

    @NotBlank(message = "Code is required")
    private String code;

    private List<String> defaultColumns;
    private Map<String, Object> defaultFilters;
    private String defaultSortField;
    private String defaultSortDirection;
    private List<String> availableColumns;
    private boolean systemTemplate;
    private int usageCount;

    private Instant createdAt;
    private Instant updatedAt;
    private String createdBy;
}
