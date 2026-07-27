package com.techknife.report.dto;

import com.techknife.report.entity.ReportCategoryType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReportSearchRequest {
    private String query;
    private ReportCategoryType category;
    private Instant startDate;
    private Instant endDate;
    private Boolean savedOnly;
    private Boolean isTemplate;
    private int page = 0;
    private int size = 20;
}
