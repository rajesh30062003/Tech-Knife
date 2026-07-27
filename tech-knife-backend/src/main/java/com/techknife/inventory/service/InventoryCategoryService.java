package com.techknife.inventory.service;

import com.techknife.inventory.dto.InventoryCategoryDTO;

import java.util.List;

public interface InventoryCategoryService {
    List<InventoryCategoryDTO> getAllCategories();
    InventoryCategoryDTO getCategoryById(String id);
    InventoryCategoryDTO createCategory(InventoryCategoryDTO dto);
    InventoryCategoryDTO updateCategory(String id, InventoryCategoryDTO dto);
    void deleteCategory(String id);
}
