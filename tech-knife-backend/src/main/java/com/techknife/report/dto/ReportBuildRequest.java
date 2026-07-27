package com.techknife.report.dto;

import com.techknife.report.entity.ReportCategoryType;
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
public class ReportBuildRequest {
    private String name;
    
    @NotNull(message = "Category is required")
    private ReportCategoryType category;
    
    private String templateId;
    private List<String> selectedColumns;
    private Map<String, Object> filters;
    private List<ReportDTO.SortDTO> sorting;
    private List<String> grouping;
    private List<ReportDTO.AggregationDTO> aggregations;
    
    private Instant startDate;
    private Instant endDate;
    
    private int pageNumber = 0;
    private int pageSize = 50;
    
    private boolean saveReport;
}
