package com.techknife.inventory.service.impl;

import com.techknife.inventory.dto.InventoryCategoryDTO;
import com.techknife.inventory.entity.InventoryCategory;
import com.techknife.inventory.repository.InventoryCategoryRepository;
import com.techknife.inventory.service.InventoryCategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class InventoryCategoryServiceImpl implements InventoryCategoryService {

    private final InventoryCategoryRepository categoryRepository;

    @Override
    public List<InventoryCategoryDTO> getAllCategories() {
        return categoryRepository.findAll().stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public InventoryCategoryDTO getCategoryById(String id) {
        InventoryCategory category = categoryRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Inventory category not found with id: " + id));
        return mapToDTO(category);
    }

    @Override
    public InventoryCategoryDTO createCategory(InventoryCategoryDTO dto) {
        if (categoryRepository.existsByCode(dto.getCode())) {
            throw new IllegalArgumentException("Inventory category already exists with code: " + dto.getCode());
        }

        InventoryCategory category = InventoryCategory.builder()
                .code(dto.getCode())
                .name(dto.getName())
                .description(dto.getDescription())
                .status(dto.getStatus() != null ? dto.getStatus() : "ACTIVE")
                .build();

        InventoryCategory saved = categoryRepository.save(category);
        return mapToDTO(saved);
    }

    @Override
    public InventoryCategoryDTO updateCategory(String id, InventoryCategoryDTO dto) {
        InventoryCategory category = categoryRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Inventory category not found with id: " + id));

        if (dto.getName() != null) category.setName(dto.getName());
        if (dto.getDescription() != null) category.setDescription(dto.getDescription());
        if (dto.getStatus() != null) category.setStatus(dto.getStatus());

        InventoryCategory saved = categoryRepository.save(category);
        return mapToDTO(saved);
    }

    @Override
    public void deleteCategory(String id) {
        if (!categoryRepository.existsById(id)) {
            throw new IllegalArgumentException("Inventory category not found with id: " + id);
        }
        categoryRepository.deleteById(id);
    }

    private InventoryCategoryDTO mapToDTO(InventoryCategory c) {
        return InventoryCategoryDTO.builder()
                .id(c.getId())
                .code(c.getCode())
                .name(c.getName())
                .description(c.getDescription())
                .status(c.getStatus())
                .createdAt(c.getCreatedAt())
                .updatedAt(c.getUpdatedAt())
                .createdBy(c.getCreatedBy())
                .updatedBy(c.getUpdatedBy())
                .build();
    }
}
