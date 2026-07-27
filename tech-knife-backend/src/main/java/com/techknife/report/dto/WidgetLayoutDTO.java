package com.techknife.report.dto;

import com.techknife.report.entity.DashboardType;
import jakarta.validation.constraints.NotNull;
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
public class WidgetLayoutDTO {
    private String id;
    private String name;

    @NotNull(message = "Dashboard type is required")
    private DashboardType dashboardType;

    private String userId;
    private List<WidgetPositionDTO> positions;
    private boolean defaultLayout;

    private Instant createdAt;
    private Instant updatedAt;
    private String createdBy;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class WidgetPositionDTO {
        private String widgetId;
        private int x;
        private int y;
        private int w;
        private int h;
    }
}
