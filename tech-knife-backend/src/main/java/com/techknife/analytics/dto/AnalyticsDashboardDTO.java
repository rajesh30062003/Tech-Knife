package com.techknife.analytics.dto;

import com.techknife.analytics.entity.ExecutiveRole;
import com.techknife.analytics.entity.KPICategory;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AnalyticsDashboardDTO {

    private String id;
    private String name;
    private String code;
    private String description;
    private ExecutiveRole role;
    private KPICategory category;
    private List<DashboardSectionDTO> sections;
    private boolean defaultDashboard;
    private boolean isSystem;
    private boolean active;
    private Instant createdAt;
    private Instant updatedAt;
    private String createdBy;
}
