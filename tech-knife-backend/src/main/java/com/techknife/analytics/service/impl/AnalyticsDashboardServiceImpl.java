package com.techknife.analytics.service.impl;

import com.techknife.analytics.dto.AnalyticsDashboardDTO;
import com.techknife.analytics.dto.DashboardMetricDTO;
import com.techknife.analytics.dto.DashboardSectionDTO;
import com.techknife.analytics.entity.AnalyticsDashboard;
import com.techknife.analytics.entity.DashboardMetric;
import com.techknife.analytics.entity.DashboardSection;
import com.techknife.analytics.entity.ExecutiveRole;
import com.techknife.analytics.repository.AnalyticsDashboardRepository;
import com.techknife.analytics.service.AnalyticsDashboardService;
import com.techknife.common.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AnalyticsDashboardServiceImpl implements AnalyticsDashboardService {

    private final AnalyticsDashboardRepository dashboardRepository;

    @Override
    public List<AnalyticsDashboardDTO> getAllDashboards() {
        return dashboardRepository.findAll().stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public AnalyticsDashboardDTO getDashboardById(String id) {
        AnalyticsDashboard dashboard = dashboardRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Analytics dashboard not found with id: " + id));
        return mapToDTO(dashboard);
    }

    @Override
    public AnalyticsDashboardDTO getDashboardByCode(String code) {
        AnalyticsDashboard dashboard = dashboardRepository.findByCode(code)
                .orElseThrow(() -> new ResourceNotFoundException("Analytics dashboard not found with code: " + code));
        return mapToDTO(dashboard);
    }

    @Override
    public AnalyticsDashboardDTO getDashboardByRole(ExecutiveRole role) {
        AnalyticsDashboard dashboard = dashboardRepository.findByRole(role)
                .orElseThrow(() -> new ResourceNotFoundException("Analytics dashboard not found for role: " + role));
        return mapToDTO(dashboard);
    }

    @Override
    public AnalyticsDashboardDTO createDashboard(AnalyticsDashboardDTO dto) {
        AnalyticsDashboard dashboard = mapToEntity(dto);
        if (dashboard.getCode() == null || dashboard.getCode().isBlank()) {
            dashboard.setCode("DASH-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase());
        }
        if (dashboard.getSections() == null) {
            dashboard.setSections(new ArrayList<>());
        }
        dashboard.setCreatedAt(Instant.now());
        dashboard.setUpdatedAt(Instant.now());
        dashboard.setActive(true);

        return mapToDTO(dashboardRepository.save(dashboard));
    }

    @Override
    public AnalyticsDashboardDTO updateDashboard(String id, AnalyticsDashboardDTO dto) {
        AnalyticsDashboard dashboard = dashboardRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Analytics dashboard not found with id: " + id));

        dashboard.setName(dto.getName());
        dashboard.setDescription(dto.getDescription());
        dashboard.setRole(dto.getRole());
        dashboard.setCategory(dto.getCategory());
        dashboard.setDefaultDashboard(dto.isDefaultDashboard());
        dashboard.setActive(dto.isActive());
        dashboard.setUpdatedAt(Instant.now());

        if (dto.getSections() != null) {
            dashboard.setSections(dto.getSections().stream().map(this::mapSectionToEntity).collect(Collectors.toList()));
        }

        return mapToDTO(dashboardRepository.save(dashboard));
    }

    @Override
    public void deleteDashboard(String id) {
        if (!dashboardRepository.existsById(id)) {
            throw new ResourceNotFoundException("Analytics dashboard not found with id: " + id);
        }
        dashboardRepository.deleteById(id);
    }

    @Override
    public AnalyticsDashboardDTO addSection(String dashboardId, DashboardSectionDTO sectionDTO) {
        AnalyticsDashboard dashboard = dashboardRepository.findById(dashboardId)
                .orElseThrow(() -> new ResourceNotFoundException("Analytics dashboard not found with id: " + dashboardId));

        if (dashboard.getSections() == null) {
            dashboard.setSections(new ArrayList<>());
        }

        DashboardSection section = mapSectionToEntity(sectionDTO);
        if (section.getId() == null) {
            section.setId(UUID.randomUUID().toString());
        }
        section.setDashboardId(dashboardId);
        dashboard.getSections().add(section);
        dashboard.setUpdatedAt(Instant.now());

        return mapToDTO(dashboardRepository.save(dashboard));
    }

    @Override
    public AnalyticsDashboardDTO addMetricToSection(String dashboardId, String sectionId, DashboardMetricDTO metricDTO) {
        AnalyticsDashboard dashboard = dashboardRepository.findById(dashboardId)
                .orElseThrow(() -> new ResourceNotFoundException("Analytics dashboard not found with id: " + dashboardId));

        if (dashboard.getSections() != null) {
            for (DashboardSection section : dashboard.getSections()) {
                if (sectionId.equals(section.getId())) {
                    if (section.getMetrics() == null) {
                        section.setMetrics(new ArrayList<>());
                    }
                    DashboardMetric metric = mapMetricToEntity(metricDTO);
                    if (metric.getId() == null) {
                        metric.setId(UUID.randomUUID().toString());
                    }
                    section.getMetrics().add(metric);
                    break;
                }
            }
        }
        dashboard.setUpdatedAt(Instant.now());
        return mapToDTO(dashboardRepository.save(dashboard));
    }

    private AnalyticsDashboardDTO mapToDTO(AnalyticsDashboard entity) {
        if (entity == null) return null;
        return AnalyticsDashboardDTO.builder()
                .id(entity.getId())
                .name(entity.getName())
                .code(entity.getCode())
                .description(entity.getDescription())
                .role(entity.getRole())
                .category(entity.getCategory())
                .sections(entity.getSections() != null ? entity.getSections().stream().map(this::mapSectionToDTO).collect(Collectors.toList()) : new ArrayList<>())
                .defaultDashboard(entity.isDefaultDashboard())
                .isSystem(entity.isSystem())
                .active(entity.isActive())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .createdBy(entity.getCreatedBy())
                .build();
    }

    private AnalyticsDashboard mapToEntity(AnalyticsDashboardDTO dto) {
        if (dto == null) return null;
        return AnalyticsDashboard.builder()
                .id(dto.getId())
                .name(dto.getName())
                .code(dto.getCode())
                .description(dto.getDescription())
                .role(dto.getRole())
                .category(dto.getCategory())
                .sections(dto.getSections() != null ? dto.getSections().stream().map(this::mapSectionToEntity).collect(Collectors.toList()) : new ArrayList<>())
                .defaultDashboard(dto.isDefaultDashboard())
                .isSystem(dto.isSystem())
                .active(dto.isActive())
                .build();
    }

    private DashboardSectionDTO mapSectionToDTO(DashboardSection entity) {
        if (entity == null) return null;
        return DashboardSectionDTO.builder()
                .id(entity.getId())
                .dashboardId(entity.getDashboardId())
                .title(entity.getTitle())
                .description(entity.getDescription())
                .displayOrder(entity.getDisplayOrder())
                .metrics(entity.getMetrics() != null ? entity.getMetrics().stream().map(this::mapMetricToDTO).collect(Collectors.toList()) : new ArrayList<>())
                .collapsed(entity.isCollapsed())
                .build();
    }

    private DashboardSection mapSectionToEntity(DashboardSectionDTO dto) {
        if (dto == null) return null;
        return DashboardSection.builder()
                .id(dto.getId())
                .dashboardId(dto.getDashboardId())
                .title(dto.getTitle())
                .description(dto.getDescription())
                .displayOrder(dto.getDisplayOrder())
                .metrics(dto.getMetrics() != null ? dto.getMetrics().stream().map(this::mapMetricToEntity).collect(Collectors.toList()) : new ArrayList<>())
                .collapsed(dto.isCollapsed())
                .build();
    }

    private DashboardMetricDTO mapMetricToDTO(DashboardMetric entity) {
        if (entity == null) return null;
        return DashboardMetricDTO.builder()
                .id(entity.getId())
                .metricKey(entity.getMetricKey())
                .title(entity.getTitle())
                .description(entity.getDescription())
                .category(entity.getCategory())
                .displayType(entity.getDisplayType())
                .kpiId(entity.getKpiId())
                .currentValue(entity.getCurrentValue())
                .previousValue(entity.getPreviousValue())
                .percentageChange(entity.getPercentageChange())
                .unit(entity.getUnit())
                .icon(entity.getIcon())
                .colorHex(entity.getColorHex())
                .queryConfig(entity.getQueryConfig())
                .refreshIntervalSeconds(entity.getRefreshIntervalSeconds())
                .displayOrder(entity.getDisplayOrder())
                .active(entity.isActive())
                .build();
    }

    private DashboardMetric mapMetricToEntity(DashboardMetricDTO dto) {
        if (dto == null) return null;
        return DashboardMetric.builder()
                .id(dto.getId())
                .metricKey(dto.getMetricKey())
                .title(dto.getTitle())
                .description(dto.getDescription())
                .category(dto.getCategory())
                .displayType(dto.getDisplayType())
                .kpiId(dto.getKpiId())
                .currentValue(dto.getCurrentValue())
                .previousValue(dto.getPreviousValue())
                .percentageChange(dto.getPercentageChange())
                .unit(dto.getUnit())
                .icon(dto.getIcon())
                .colorHex(dto.getColorHex())
                .queryConfig(dto.getQueryConfig())
                .refreshIntervalSeconds(dto.getRefreshIntervalSeconds())
                .displayOrder(dto.getDisplayOrder())
                .active(dto.isActive())
                .build();
    }
}
