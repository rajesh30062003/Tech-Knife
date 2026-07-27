package com.techknife.report.service;

import com.techknife.report.dto.ReportHistoryDTO;

import java.util.List;

public interface ReportHistoryService {
    ReportHistoryDTO recordHistory(ReportHistoryDTO dto);
    ReportHistoryDTO getHistoryById(String id);
    List<ReportHistoryDTO> getHistoryByReport(String reportId);
    List<ReportHistoryDTO> getHistoryByUser(String userId);
    List<ReportHistoryDTO> searchHistory(String query);
}
