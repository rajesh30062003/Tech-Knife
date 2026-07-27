package com.techknife.asset.service;

import com.techknife.asset.dto.AssetCategoryDTO;

import java.util.List;

public interface AssetCategoryService {
    List<AssetCategoryDTO> getAllCategories();
    AssetCategoryDTO getCategoryById(String id);
    AssetCategoryDTO createCategory(AssetCategoryDTO dto);
    AssetCategoryDTO updateCategory(String id, AssetCategoryDTO dto);
    void deleteCategory(String id);
}
