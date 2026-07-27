package com.techknife.report.service;

import com.techknife.report.dto.ReportTemplateDTO;
import com.techknife.report.entity.ReportCategoryType;

import java.util.List;

public interface ReportTemplateService {
    ReportTemplateDTO createTemplate(ReportTemplateDTO dto);
    ReportTemplateDTO updateTemplate(String id, ReportTemplateDTO dto);
    ReportTemplateDTO getTemplateById(String id);
    ReportTemplateDTO getTemplateByCode(String code);
    List<ReportTemplateDTO> getAllTemplates();
    List<ReportTemplateDTO> getTemplatesByCategory(ReportCategoryType category);
    List<ReportTemplateDTO> searchTemplates(String query);
    void deleteTemplate(String id);
}
