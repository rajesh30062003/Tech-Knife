package com.techknife.report.service;

import com.techknife.report.dto.ExportHistoryDTO;
import com.techknife.report.dto.ExportJobDTO;
import com.techknife.report.entity.ExportFormat;

import java.util.List;
import java.util.Map;

public interface ReportExportService {
    ExportJobDTO triggerExport(String reportId, ExportFormat format);
    ExportJobDTO getExportJobStatus(String jobId);
    List<ExportJobDTO> getExportJobsByReport(String reportId);

    byte[] generateExportBytes(Map<String, Object> reportData, ExportFormat format);
    ExportHistoryDTO logExportHistory(ExportHistoryDTO dto);
    List<ExportHistoryDTO> getExportHistoryByReport(String reportId);
    List<ExportHistoryDTO> getExportHistoryByUser(String userId);
}
