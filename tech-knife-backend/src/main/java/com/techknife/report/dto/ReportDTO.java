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
public class ReportDTO {
    private String id;

    @NotBlank(message = "Report name is required")
    private String name;

    private String description;

    @NotNull(message = "Category is required")
    private ReportCategoryType category;

    private String templateId;

    private List<String> selectedColumns;
    private Map<String, Object> filters;
    private List<SortDTO> sorting;
    private List<String> grouping;
    private List<AggregationDTO> aggregations;

    private Instant startDate;
    private Instant endDate;

    private int pageNumber;
    private int pageSize;

    private boolean saved;
    private boolean isTemplate;
    private String tags;

    private Instant createdAt;
    private Instant updatedAt;
    private String createdBy;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class SortDTO {
        private String field;
        private String direction;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class AggregationDTO {
        private String field;
        private String function;
        private String alias;
    }
}
