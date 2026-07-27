package com.techknife.report.service.impl;

import com.techknife.backend.exception.BadRequestException;
import com.techknife.backend.exception.ResourceNotFoundException;
import com.techknife.report.dto.ReportCategoryDTO;
import com.techknife.report.entity.ReportCategory;
import com.techknife.report.entity.ReportCategoryType;
import com.techknife.report.repository.ReportCategoryRepository;
import com.techknife.report.service.ReportCategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ReportCategoryServiceImpl implements ReportCategoryService {

    private final ReportCategoryRepository categoryRepository;

    @Override
    public ReportCategoryDTO createCategory(ReportCategoryDTO dto) {
        if (categoryRepository.existsByCategoryType(dto.getCategoryType())) {
            throw new BadRequestException("Category type already exists: " + dto.getCategoryType());
        }
        ReportCategory entity = mapToEntity(dto);
        ReportCategory saved = categoryRepository.save(entity);
        return mapToDTO(saved);
    }

    @Override
    public ReportCategoryDTO getCategoryByType(ReportCategoryType categoryType) {
        ReportCategory category = categoryRepository.findByCategoryType(categoryType)
                .orElseThrow(() -> new ResourceNotFoundException("ReportCategory", "categoryType", categoryType.name()));
        return mapToDTO(category);
    }

    @Override
    public List<ReportCategoryDTO> getAllCategories() {
        seedCategoriesIfEmpty();
        return categoryRepository.findAll().stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public void seedCategoriesIfEmpty() {
        if (categoryRepository.count() == 0) {
            int order = 1;
            for (ReportCategoryType type : ReportCategoryType.values()) {
                ReportCategory cat = ReportCategory.builder()
                        .categoryType(type)
                        .name(formatName(type.name()))
                        .description("System report category for " + formatName(type.name()))
                        .icon("bar-chart")
                        .displayOrder(order++)
                        .active(true)
                        .build();
                categoryRepository.save(cat);
            }
        }
    }

    private String formatName(String enumName) {
        String[] words = enumName.split("_");
        StringBuilder sb = new StringBuilder();
        for (String word : words) {
            if (!word.isEmpty()) {
                sb.append(Character.toUpperCase(word.charAt(0)))
                  .append(word.substring(1).toLowerCase())
                  .append(" ");
            }
        }
        return sb.toString().trim();
    }

    private ReportCategory mapToEntity(ReportCategoryDTO dto) {
        return ReportCategory.builder()
                .id(dto.getId())
                .categoryType(dto.getCategoryType())
                .name(dto.getName())
                .description(dto.getDescription())
                .icon(dto.getIcon())
                .displayOrder(dto.getDisplayOrder())
                .active(dto.isActive())
                .build();
    }

    private ReportCategoryDTO mapToDTO(ReportCategory entity) {
        return ReportCategoryDTO.builder()
                .id(entity.getId())
                .categoryType(entity.getCategoryType())
                .name(entity.getName())
                .description(entity.getDescription())
                .icon(entity.getIcon())
                .displayOrder(entity.getDisplayOrder())
                .active(entity.isActive())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .createdBy(entity.getCreatedBy())
                .build();
    }
}
