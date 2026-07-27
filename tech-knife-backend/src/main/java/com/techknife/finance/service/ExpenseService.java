package com.techknife.finance.service;

import com.techknife.finance.dto.ExpenseDTO;

import java.util.List;

public interface ExpenseService {

    List<ExpenseDTO> getAllExpenses();

    List<ExpenseDTO> getExpensesByCategory(String categoryId);

    List<ExpenseDTO> getExpensesByEmployee(String employeeId);

    ExpenseDTO getExpenseById(String id);

    ExpenseDTO createExpense(ExpenseDTO dto);

    ExpenseDTO updateExpense(String id, ExpenseDTO dto);

    ExpenseDTO approveExpense(String id, String approvalStatus, String approvedBy);

    void deleteExpense(String id);
}
