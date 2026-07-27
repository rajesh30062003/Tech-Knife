package com.techknife.finance.service;

import com.techknife.finance.dto.BudgetDTO;

import java.util.List;

public interface BudgetService {

    List<BudgetDTO> getAllBudgets();

    List<BudgetDTO> getBudgetsByFinancialYear(String financialYearId);

    BudgetDTO getBudgetById(String id);

    BudgetDTO createBudget(BudgetDTO dto);

    BudgetDTO updateBudget(String id, BudgetDTO dto);

    void deleteBudget(String id);
}
