package com.techknife.finance.service.impl;

import com.techknife.finance.dto.ExpenseCategoryDTO;
import com.techknife.finance.entity.ExpenseCategory;
import com.techknife.finance.repository.ExpenseCategoryRepository;
import com.techknife.finance.service.ExpenseCategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ExpenseCategoryServiceImpl implements ExpenseCategoryService {

    private final ExpenseCategoryRepository expenseCategoryRepository;

    @Override
    public List<ExpenseCategoryDTO> getAllCategories() {
        return expenseCategoryRepository.findAll().stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public ExpenseCategoryDTO getCategoryById(String id) {
        ExpenseCategory category = expenseCategoryRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Expense category not found with id: " + id));
        return mapToDTO(category);
    }

    @Override
    public ExpenseCategoryDTO createCategory(ExpenseCategoryDTO dto) {
        if (expenseCategoryRepository.existsByCategoryCode(dto.getCategoryCode())) {
            throw new IllegalArgumentException("Expense category code already exists: " + dto.getCategoryCode());
        }

        ExpenseCategory category = ExpenseCategory.builder()
                .categoryCode(dto.getCategoryCode())
                .categoryName(dto.getCategoryName())
                .description(dto.getDescription())
                .status(dto.getStatus() != null ? dto.getStatus() : "ACTIVE")
                .build();

        ExpenseCategory saved = expenseCategoryRepository.save(category);
        return mapToDTO(saved);
    }

    @Override
    public ExpenseCategoryDTO updateCategory(String id, ExpenseCategoryDTO dto) {
        ExpenseCategory category = expenseCategoryRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Expense category not found with id: " + id));

        if (dto.getCategoryName() != null) category.setCategoryName(dto.getCategoryName());
        if (dto.getDescription() != null) category.setDescription(dto.getDescription());
        if (dto.getStatus() != null) category.setStatus(dto.getStatus());

        ExpenseCategory saved = expenseCategoryRepository.save(category);
        return mapToDTO(saved);
    }

    @Override
    public void deleteCategory(String id) {
        if (!expenseCategoryRepository.existsById(id)) {
            throw new IllegalArgumentException("Expense category not found with id: " + id);
        }
        expenseCategoryRepository.deleteById(id);
    }

    private ExpenseCategoryDTO mapToDTO(ExpenseCategory cat) {
        return ExpenseCategoryDTO.builder()
                .id(cat.getId())
                .categoryCode(cat.getCategoryCode())
                .categoryName(cat.getCategoryName())
                .description(cat.getDescription())
                .status(cat.getStatus())
                .createdAt(cat.getCreatedAt())
                .updatedAt(cat.getUpdatedAt())
                .createdBy(cat.getCreatedBy())
                .updatedBy(cat.getUpdatedBy())
                .build();
    }
}
