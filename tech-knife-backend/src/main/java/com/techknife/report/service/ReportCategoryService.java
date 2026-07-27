package com.techknife.report.service;

import com.techknife.report.dto.ReportCategoryDTO;
import com.techknife.report.entity.ReportCategoryType;

import java.util.List;

public interface ReportCategoryService {
    ReportCategoryDTO createCategory(ReportCategoryDTO dto);
    ReportCategoryDTO getCategoryByType(ReportCategoryType categoryType);
    List<ReportCategoryDTO> getAllCategories();
    void seedCategoriesIfEmpty();
}
