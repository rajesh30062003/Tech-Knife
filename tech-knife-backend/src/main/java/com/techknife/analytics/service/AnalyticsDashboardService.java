package com.techknife.analytics.service;

import com.techknife.analytics.dto.AnalyticsDashboardDTO;
import com.techknife.analytics.dto.DashboardMetricDTO;
import com.techknife.analytics.dto.DashboardSectionDTO;
import com.techknife.analytics.entity.ExecutiveRole;

import java.util.List;

public interface AnalyticsDashboardService {
    List<AnalyticsDashboardDTO> getAllDashboards();
    AnalyticsDashboardDTO getDashboardById(String id);
    AnalyticsDashboardDTO getDashboardByCode(String code);
    AnalyticsDashboardDTO getDashboardByRole(ExecutiveRole role);
    AnalyticsDashboardDTO createDashboard(AnalyticsDashboardDTO dto);
    AnalyticsDashboardDTO updateDashboard(String id, AnalyticsDashboardDTO dto);
    void deleteDashboard(String id);
    AnalyticsDashboardDTO addSection(String dashboardId, DashboardSectionDTO sectionDTO);
    AnalyticsDashboardDTO addMetricToSection(String dashboardId, String sectionId, DashboardMetricDTO metricDTO);
}
