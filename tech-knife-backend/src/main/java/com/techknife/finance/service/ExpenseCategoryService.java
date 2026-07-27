package com.techknife.finance.service;

import com.techknife.finance.dto.ExpenseCategoryDTO;

import java.util.List;

public interface ExpenseCategoryService {

    List<ExpenseCategoryDTO> getAllCategories();

    ExpenseCategoryDTO getCategoryById(String id);

    ExpenseCategoryDTO createCategory(ExpenseCategoryDTO dto);

    ExpenseCategoryDTO updateCategory(String id, ExpenseCategoryDTO dto);

    void deleteCategory(String id);
}
