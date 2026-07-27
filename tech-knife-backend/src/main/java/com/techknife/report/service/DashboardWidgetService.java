package com.techknife.report.service;

import com.techknife.report.dto.DashboardWidgetDTO;
import com.techknife.report.dto.WidgetLayoutDTO;
import com.techknife.report.entity.DashboardType;
import com.techknife.report.entity.ReportCategoryType;

import java.util.List;

public interface DashboardWidgetService {
    DashboardWidgetDTO createWidget(DashboardWidgetDTO dto);
    DashboardWidgetDTO updateWidget(String id, DashboardWidgetDTO dto);
    DashboardWidgetDTO getWidgetById(String id);
    List<DashboardWidgetDTO> getAllActiveWidgets();
    List<DashboardWidgetDTO> getWidgetsByCategory(ReportCategoryType category);
    List<DashboardWidgetDTO> searchWidgets(String query);
    void deleteWidget(String id);

    WidgetLayoutDTO saveWidgetLayout(WidgetLayoutDTO dto);
    WidgetLayoutDTO getWidgetLayout(String userId, DashboardType dashboardType);
}
