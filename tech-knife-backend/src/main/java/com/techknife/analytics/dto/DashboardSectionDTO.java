package com.techknife.analytics.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DashboardSectionDTO {

    private String id;
    private String dashboardId;
    private String title;
    private String description;
    private int displayOrder;
    private List<DashboardMetricDTO> metrics;
    private boolean collapsed;
}
