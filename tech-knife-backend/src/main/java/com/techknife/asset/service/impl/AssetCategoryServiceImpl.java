package com.techknife.asset.service.impl;

import com.techknife.asset.dto.AssetCategoryDTO;
import com.techknife.asset.entity.AssetCategory;
import com.techknife.asset.repository.AssetCategoryRepository;
import com.techknife.asset.service.AssetCategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AssetCategoryServiceImpl implements AssetCategoryService {

    private final AssetCategoryRepository categoryRepository;

    @Override
    public List<AssetCategoryDTO> getAllCategories() {
        return categoryRepository.findAll().stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public AssetCategoryDTO getCategoryById(String id) {
        AssetCategory category = categoryRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Asset category not found with id: " + id));
        return mapToDTO(category);
    }

    @Override
    public AssetCategoryDTO createCategory(AssetCategoryDTO dto) {
        if (categoryRepository.existsByCode(dto.getCode())) {
            throw new IllegalArgumentException("Asset category already exists with code: " + dto.getCode());
        }

        AssetCategory category = AssetCategory.builder()
                .code(dto.getCode())
                .name(dto.getName())
                .description(dto.getDescription())
                .depreciationRate(dto.getDepreciationRate())
                .usefulLifeYears(dto.getUsefulLifeYears())
                .status(dto.getStatus() != null ? dto.getStatus() : "ACTIVE")
                .build();

        AssetCategory saved = categoryRepository.save(category);
        return mapToDTO(saved);
    }

    @Override
    public AssetCategoryDTO updateCategory(String id, AssetCategoryDTO dto) {
        AssetCategory category = categoryRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Asset category not found with id: " + id));

        if (dto.getName() != null) category.setName(dto.getName());
        if (dto.getDescription() != null) category.setDescription(dto.getDescription());
        if (dto.getDepreciationRate() != null) category.setDepreciationRate(dto.getDepreciationRate());
        if (dto.getUsefulLifeYears() != null) category.setUsefulLifeYears(dto.getUsefulLifeYears());
        if (dto.getStatus() != null) category.setStatus(dto.getStatus());

        AssetCategory saved = categoryRepository.save(category);
        return mapToDTO(saved);
    }

    @Override
    public void deleteCategory(String id) {
        if (!categoryRepository.existsById(id)) {
            throw new IllegalArgumentException("Asset category not found with id: " + id);
        }
        categoryRepository.deleteById(id);
    }

    private AssetCategoryDTO mapToDTO(AssetCategory c) {
        return AssetCategoryDTO.builder()
                .id(c.getId())
                .code(c.getCode())
                .name(c.getName())
                .description(c.getDescription())
                .depreciationRate(c.getDepreciationRate())
                .usefulLifeYears(c.getUsefulLifeYears())
                .status(c.getStatus())
                .createdAt(c.getCreatedAt())
                .updatedAt(c.getUpdatedAt())
                .createdBy(c.getCreatedBy())
                .updatedBy(c.getUpdatedBy())
                .build();
    }
}
