package com.techknife.finance.service.impl;

import com.techknife.finance.dto.BudgetDTO;
import com.techknife.finance.entity.Budget;
import com.techknife.finance.repository.BudgetRepository;
import com.techknife.finance.service.BudgetService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BudgetServiceImpl implements BudgetService {

    private final BudgetRepository budgetRepository;

    @Override
    public List<BudgetDTO> getAllBudgets() {
        return budgetRepository.findAll().stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<BudgetDTO> getBudgetsByFinancialYear(String financialYearId) {
        return budgetRepository.findByFinancialYearId(financialYearId).stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public BudgetDTO getBudgetById(String id) {
        Budget budget = budgetRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Budget not found with id: " + id));
        return mapToDTO(budget);
    }

    @Override
    public BudgetDTO createBudget(BudgetDTO dto) {
        BigDecimal budgeted = dto.getBudgetedAmount() != null ? dto.getBudgetedAmount() : BigDecimal.ZERO;
        BigDecimal actual = dto.getActualAmount() != null ? dto.getActualAmount() : BigDecimal.ZERO;
        BigDecimal variance = budgeted.subtract(actual);

        Budget budget = Budget.builder()
                .budgetName(dto.getBudgetName())
                .budgetScope(dto.getBudgetScope() != null ? dto.getBudgetScope().toUpperCase() : "COMPANY")
                .departmentId(dto.getDepartmentId())
                .projectId(dto.getProjectId())
                .financialYearId(dto.getFinancialYearId())
                .costCenterId(dto.getCostCenterId())
                .budgetedAmount(budgeted)
                .actualAmount(actual)
                .varianceAmount(variance)
                .description(dto.getDescription())
                .status(dto.getStatus() != null ? dto.getStatus() : "ACTIVE")
                .build();

        Budget saved = budgetRepository.save(budget);
        return mapToDTO(saved);
    }

    @Override
    public BudgetDTO updateBudget(String id, BudgetDTO dto) {
        Budget budget = budgetRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Budget not found with id: " + id));

        if (dto.getBudgetName() != null) budget.setBudgetName(dto.getBudgetName());
        if (dto.getBudgetScope() != null) budget.setBudgetScope(dto.getBudgetScope().toUpperCase());
        if (dto.getDepartmentId() != null) budget.setDepartmentId(dto.getDepartmentId());
        if (dto.getProjectId() != null) budget.setProjectId(dto.getProjectId());
        if (dto.getFinancialYearId() != null) budget.setFinancialYearId(dto.getFinancialYearId());
        if (dto.getCostCenterId() != null) budget.setCostCenterId(dto.getCostCenterId());
        if (dto.getDescription() != null) budget.setDescription(dto.getDescription());
        if (dto.getStatus() != null) budget.setStatus(dto.getStatus());

        if (dto.getBudgetedAmount() != null) budget.setBudgetedAmount(dto.getBudgetedAmount());
        if (dto.getActualAmount() != null) budget.setActualAmount(dto.getActualAmount());

        budget.setVarianceAmount(budget.getBudgetedAmount().subtract(budget.getActualAmount()));

        Budget saved = budgetRepository.save(budget);
        return mapToDTO(saved);
    }

    @Override
    public void deleteBudget(String id) {
        if (!budgetRepository.existsById(id)) {
            throw new IllegalArgumentException("Budget not found with id: " + id);
        }
        budgetRepository.deleteById(id);
    }

    private BudgetDTO mapToDTO(Budget b) {
        return BudgetDTO.builder()
                .id(b.getId())
                .budgetName(b.getBudgetName())
                .budgetScope(b.getBudgetScope())
                .departmentId(b.getDepartmentId())
                .projectId(b.getProjectId())
                .financialYearId(b.getFinancialYearId())
                .costCenterId(b.getCostCenterId())
                .budgetedAmount(b.getBudgetedAmount())
                .actualAmount(b.getActualAmount())
                .varianceAmount(b.getVarianceAmount())
                .description(b.getDescription())
                .status(b.getStatus())
                .createdAt(b.getCreatedAt())
                .updatedAt(b.getUpdatedAt())
                .createdBy(b.getCreatedBy())
                .updatedBy(b.getUpdatedBy())
                .build();
    }
}
