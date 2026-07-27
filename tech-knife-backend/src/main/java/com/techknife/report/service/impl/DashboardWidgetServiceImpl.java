package com.techknife.report.service.impl;

import com.techknife.backend.exception.ResourceNotFoundException;
import com.techknife.report.dto.DashboardWidgetDTO;
import com.techknife.report.dto.WidgetLayoutDTO;
import com.techknife.report.entity.DashboardType;
import com.techknife.report.entity.DashboardWidget;
import com.techknife.report.entity.ReportCategoryType;
import com.techknife.report.entity.WidgetLayout;
import com.techknife.report.repository.DashboardWidgetRepository;
import com.techknife.report.repository.WidgetLayoutRepository;
import com.techknife.report.service.DashboardWidgetService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DashboardWidgetServiceImpl implements DashboardWidgetService {

    private final DashboardWidgetRepository widgetRepository;
    private final WidgetLayoutRepository layoutRepository;

    @Override
    public DashboardWidgetDTO createWidget(DashboardWidgetDTO dto) {
        DashboardWidget widget = mapWidgetToEntity(dto);
        widget.setActive(true);
        DashboardWidget saved = widgetRepository.save(widget);
        return mapWidgetToDTO(saved);
    }

    @Override
    public DashboardWidgetDTO updateWidget(String id, DashboardWidgetDTO dto) {
        DashboardWidget existing = widgetRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("DashboardWidget", "id", id));

        existing.setTitle(dto.getTitle());
        existing.setDescription(dto.getDescription());
        existing.setWidgetType(dto.getWidgetType());
        existing.setCategory(dto.getCategory());
        existing.setReportId(dto.getReportId());
        existing.setKpiMetricKey(dto.getKpiMetricKey());
        existing.setQueryConfig(dto.getQueryConfig());
        existing.setRefreshIntervalSeconds(dto.getRefreshIntervalSeconds());
        existing.setActive(dto.isActive());

        DashboardWidget updated = widgetRepository.save(existing);
        return mapWidgetToDTO(updated);
    }

    @Override
    public DashboardWidgetDTO getWidgetById(String id) {
        DashboardWidget widget = widgetRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("DashboardWidget", "id", id));
        return mapWidgetToDTO(widget);
    }

    @Override
    public List<DashboardWidgetDTO> getAllActiveWidgets() {
        return widgetRepository.findByActiveTrue().stream()
                .map(this::mapWidgetToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<DashboardWidgetDTO> getWidgetsByCategory(ReportCategoryType category) {
        return widgetRepository.findByCategory(category).stream()
                .map(this::mapWidgetToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<DashboardWidgetDTO> searchWidgets(String query) {
        if (query == null || query.trim().isEmpty()) {
            return getAllActiveWidgets();
        }
        return widgetRepository.findByTitleContainingIgnoreCase(query.trim()).stream()
                .map(this::mapWidgetToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public void deleteWidget(String id) {
        if (!widgetRepository.existsById(id)) {
            throw new ResourceNotFoundException("DashboardWidget", "id", id);
        }
        widgetRepository.deleteById(id);
    }

    @Override
    public WidgetLayoutDTO saveWidgetLayout(WidgetLayoutDTO dto) {
        WidgetLayout layout = WidgetLayout.builder()
                .id(dto.getId())
                .name(dto.getName() != null ? dto.getName() : "Custom Layout")
                .dashboardType(dto.getDashboardType())
                .userId(dto.getUserId())
                .positions(dto.getPositions() != null ? dto.getPositions().stream()
                        .map(p -> WidgetLayout.WidgetPosition.builder()
                                .widgetId(p.getWidgetId())
                                .x(p.getX())
                                .y(p.getY())
                                .w(p.getW())
                                .h(p.getH())
                                .build())
                        .collect(Collectors.toList()) : Collections.emptyList())
                .defaultLayout(dto.isDefaultLayout())
                .build();

        WidgetLayout saved = layoutRepository.save(layout);
        return mapLayoutToDTO(saved);
    }

    @Override
    public WidgetLayoutDTO getWidgetLayout(String userId, DashboardType dashboardType) {
        WidgetLayout layout = layoutRepository.findByUserIdAndDashboardType(userId, dashboardType)
                .orElseGet(() -> layoutRepository.findByDashboardTypeAndDefaultLayoutTrue(dashboardType)
                        .orElse(WidgetLayout.builder()
                                .dashboardType(dashboardType)
                                .userId(userId)
                                .name("Default " + dashboardType.name())
                                .positions(Collections.emptyList())
                                .defaultLayout(true)
                                .build()));
        return mapLayoutToDTO(layout);
    }

    private DashboardWidget mapWidgetToEntity(DashboardWidgetDTO dto) {
        return DashboardWidget.builder()
                .id(dto.getId())
                .title(dto.getTitle())
                .description(dto.getDescription())
                .widgetType(dto.getWidgetType())
                .category(dto.getCategory())
                .reportId(dto.getReportId())
                .kpiMetricKey(dto.getKpiMetricKey())
                .queryConfig(dto.getQueryConfig())
                .refreshIntervalSeconds(dto.getRefreshIntervalSeconds())
                .active(dto.isActive())
                .build();
    }

    private DashboardWidgetDTO mapWidgetToDTO(DashboardWidget entity) {
        return DashboardWidgetDTO.builder()
                .id(entity.getId())
                .title(entity.getTitle())
                .description(entity.getDescription())
                .widgetType(entity.getWidgetType())
                .category(entity.getCategory())
                .reportId(entity.getReportId())
                .kpiMetricKey(entity.getKpiMetricKey())
                .queryConfig(entity.getQueryConfig())
                .refreshIntervalSeconds(entity.getRefreshIntervalSeconds())
                .active(entity.isActive())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .createdBy(entity.getCreatedBy())
                .build();
    }

    private WidgetLayoutDTO mapLayoutToDTO(WidgetLayout entity) {
        List<WidgetLayoutDTO.WidgetPositionDTO> positions = entity.getPositions() != null ? entity.getPositions().stream()
                .map(p -> WidgetLayoutDTO.WidgetPositionDTO.builder()
                        .widgetId(p.getWidgetId())
                        .x(p.getX())
                        .y(p.getY())
                        .w(p.getW())
                        .h(p.getH())
                        .build())
                .collect(Collectors.toList()) : Collections.emptyList();

        return WidgetLayoutDTO.builder()
                .id(entity.getId())
                .name(entity.getName())
                .dashboardType(entity.getDashboardType())
                .userId(entity.getUserId())
                .positions(positions)
                .defaultLayout(entity.isDefaultLayout())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .createdBy(entity.getCreatedBy())
                .build();
    }
}
