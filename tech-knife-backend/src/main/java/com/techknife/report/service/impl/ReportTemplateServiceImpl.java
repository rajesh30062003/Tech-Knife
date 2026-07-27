package com.techknife.report.service.impl;

import com.techknife.backend.exception.BadRequestException;
import com.techknife.backend.exception.ResourceNotFoundException;
import com.techknife.report.dto.ReportTemplateDTO;
import com.techknife.report.entity.ReportCategoryType;
import com.techknife.report.entity.ReportTemplate;
import com.techknife.report.repository.ReportTemplateRepository;
import com.techknife.report.service.ReportTemplateService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ReportTemplateServiceImpl implements ReportTemplateService {

    private final ReportTemplateRepository templateRepository;

    @Override
    public ReportTemplateDTO createTemplate(ReportTemplateDTO dto) {
        if (templateRepository.existsByCode(dto.getCode())) {
            throw new BadRequestException("Duplicate Report Template: Template code '" + dto.getCode() + "' already exists");
        }
        if (templateRepository.existsByNameIgnoreCase(dto.getName())) {
            throw new BadRequestException("Duplicate Report Template: Template name '" + dto.getName() + "' already exists");
        }

        ReportTemplate template = mapToEntity(dto);
        ReportTemplate saved = templateRepository.save(template);
        return mapToDTO(saved);
    }

    @Override
    public ReportTemplateDTO updateTemplate(String id, ReportTemplateDTO dto) {
        ReportTemplate existing = templateRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("ReportTemplate", "id", id));

        if (!existing.getCode().equalsIgnoreCase(dto.getCode()) && templateRepository.existsByCode(dto.getCode())) {
            throw new BadRequestException("Duplicate Report Template: Template code '" + dto.getCode() + "' already exists");
        }

        existing.setName(dto.getName());
        existing.setDescription(dto.getDescription());
        existing.setCategory(dto.getCategory());
        existing.setCode(dto.getCode());
        existing.setDefaultColumns(dto.getDefaultColumns());
        existing.setDefaultFilters(dto.getDefaultFilters());
        existing.setDefaultSortField(dto.getDefaultSortField());
        existing.setDefaultSortDirection(dto.getDefaultSortDirection());
        existing.setAvailableColumns(dto.getAvailableColumns());
        existing.setSystemTemplate(dto.isSystemTemplate());

        ReportTemplate updated = templateRepository.save(existing);
        return mapToDTO(updated);
    }

    @Override
    public ReportTemplateDTO getTemplateById(String id) {
        ReportTemplate template = templateRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("ReportTemplate", "id", id));
        return mapToDTO(template);
    }

    @Override
    public ReportTemplateDTO getTemplateByCode(String code) {
        ReportTemplate template = templateRepository.findByCode(code)
                .orElseThrow(() -> new ResourceNotFoundException("ReportTemplate", "code", code));
        return mapToDTO(template);
    }

    @Override
    public List<ReportTemplateDTO> getAllTemplates() {
        return templateRepository.findAll().stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<ReportTemplateDTO> getTemplatesByCategory(ReportCategoryType category) {
        return templateRepository.findByCategory(category).stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<ReportTemplateDTO> searchTemplates(String query) {
        if (query == null || query.trim().isEmpty()) {
            return getAllTemplates();
        }
        return templateRepository.findByNameContainingIgnoreCase(query.trim()).stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public void deleteTemplate(String id) {
        if (!templateRepository.existsById(id)) {
            throw new ResourceNotFoundException("ReportTemplate", "id", id);
        }
        templateRepository.deleteById(id);
    }

    private ReportTemplate mapToEntity(ReportTemplateDTO dto) {
        return ReportTemplate.builder()
                .id(dto.getId())
                .name(dto.getName())
                .description(dto.getDescription())
                .category(dto.getCategory())
                .code(dto.getCode())
                .defaultColumns(dto.getDefaultColumns())
                .defaultFilters(dto.getDefaultFilters())
                .defaultSortField(dto.getDefaultSortField())
                .defaultSortDirection(dto.getDefaultSortDirection())
                .availableColumns(dto.getAvailableColumns())
                .systemTemplate(dto.isSystemTemplate())
                .usageCount(dto.getUsageCount())
                .build();
    }

    private ReportTemplateDTO mapToDTO(ReportTemplate entity) {
        return ReportTemplateDTO.builder()
                .id(entity.getId())
                .name(entity.getName())
                .description(entity.getDescription())
                .category(entity.getCategory())
                .code(entity.getCode())
                .defaultColumns(entity.getDefaultColumns())
                .defaultFilters(entity.getDefaultFilters())
                .defaultSortField(entity.getDefaultSortField())
                .defaultSortDirection(entity.getDefaultSortDirection())
                .availableColumns(entity.getAvailableColumns())
                .systemTemplate(entity.isSystemTemplate())
                .usageCount(entity.getUsageCount())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .createdBy(entity.getCreatedBy())
                .build();
    }
}
