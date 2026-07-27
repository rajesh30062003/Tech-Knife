package com.techknife.report.service;

import com.techknife.report.dto.ReportScheduleDTO;

import java.util.List;

public interface ReportScheduleService {
    ReportScheduleDTO createSchedule(ReportScheduleDTO dto);
    ReportScheduleDTO updateSchedule(String id, ReportScheduleDTO dto);
    ReportScheduleDTO getScheduleById(String id);
    List<ReportScheduleDTO> getSchedulesByReportId(String reportId);
    List<ReportScheduleDTO> getAllActiveSchedules();
    List<ReportScheduleDTO> searchSchedules(String query);
    void toggleScheduleStatus(String id, boolean active);
    void deleteSchedule(String id);
    void processDueSchedules();
}
