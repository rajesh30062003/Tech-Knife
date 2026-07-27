package com.techknife.report.service;

import com.techknife.report.dto.*;
import com.techknife.report.entity.DashboardType;
import com.techknife.report.entity.ReportCategoryType;

import java.util.List;
import java.util.Map;

public interface ReportService {
    ReportDTO createReport(ReportDTO dto);
    ReportDTO updateReport(String id, ReportDTO dto);
    ReportDTO getReportById(String id);
    List<ReportDTO> getAllReports();
    List<ReportDTO> getSavedReports();
    List<ReportDTO> getReportsByCategory(ReportCategoryType category);
    void deleteReport(String id);

    Map<String, Object> executeReport(ReportBuildRequest request);

    KpiReportDTO generateKpiReport(String metricKey);
    List<KpiReportDTO> getAllKpiReports();
    ExecutiveDashboardDTO generateExecutiveDashboard(DashboardType dashboardType);

    List<ReportDTO> searchReports(ReportSearchRequest searchRequest);
}
