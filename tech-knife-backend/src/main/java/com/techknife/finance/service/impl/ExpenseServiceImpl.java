package com.techknife.finance.service.impl;

import com.techknife.finance.dto.ExpenseDTO;
import com.techknife.finance.entity.Expense;
import com.techknife.finance.repository.ExpenseRepository;
import com.techknife.finance.service.ExpenseService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ExpenseServiceImpl implements ExpenseService {

    private final ExpenseRepository expenseRepository;

    @Override
    public List<ExpenseDTO> getAllExpenses() {
        return expenseRepository.findAll().stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<ExpenseDTO> getExpensesByCategory(String categoryId) {
        return expenseRepository.findByCategoryId(categoryId).stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<ExpenseDTO> getExpensesByEmployee(String employeeId) {
        return expenseRepository.findByEmployeeId(employeeId).stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public ExpenseDTO getExpenseById(String id) {
        Expense expense = expenseRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Expense not found with id: " + id));
        return mapToDTO(expense);
    }

    @Override
    public ExpenseDTO createExpense(ExpenseDTO dto) {
        String expNum = dto.getExpenseNumber() != null && !dto.getExpenseNumber().isBlank()
                ? dto.getExpenseNumber()
                : "EXP-" + System.currentTimeMillis() + "-" + UUID.randomUUID().toString().substring(0, 4).toUpperCase();

        Expense expense = Expense.builder()
                .expenseNumber(expNum)
                .categoryId(dto.getCategoryId())
                .categoryName(dto.getCategoryName())
                .title(dto.getTitle())
                .amount(dto.getAmount())
                .expenseDate(dto.getExpenseDate() != null ? dto.getExpenseDate() : LocalDate.now())
                .vendorId(dto.getVendorId())
                .vendorName(dto.getVendorName())
                .employeeId(dto.getEmployeeId())
                .salaryRunId(dto.getSalaryRunId())
                .costCenterId(dto.getCostCenterId())
                .financialYearId(dto.getFinancialYearId())
                .receiptUrl(dto.getReceiptUrl())
                .approvalStatus(dto.getApprovalStatus() != null ? dto.getApprovalStatus() : "PENDING")
                .description(dto.getDescription())
                .build();

        Expense saved = expenseRepository.save(expense);
        return mapToDTO(saved);
    }

    @Override
    public ExpenseDTO updateExpense(String id, ExpenseDTO dto) {
        Expense expense = expenseRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Expense not found with id: " + id));

        if (dto.getCategoryId() != null) expense.setCategoryId(dto.getCategoryId());
        if (dto.getCategoryName() != null) expense.setCategoryName(dto.getCategoryName());
        if (dto.getTitle() != null) expense.setTitle(dto.getTitle());
        if (dto.getAmount() != null) expense.setAmount(dto.getAmount());
        if (dto.getExpenseDate() != null) expense.setExpenseDate(dto.getExpenseDate());
        if (dto.getVendorId() != null) expense.setVendorId(dto.getVendorId());
        if (dto.getVendorName() != null) expense.setVendorName(dto.getVendorName());
        if (dto.getEmployeeId() != null) expense.setEmployeeId(dto.getEmployeeId());
        if (dto.getSalaryRunId() != null) expense.setSalaryRunId(dto.getSalaryRunId());
        if (dto.getCostCenterId() != null) expense.setCostCenterId(dto.getCostCenterId());
        if (dto.getFinancialYearId() != null) expense.setFinancialYearId(dto.getFinancialYearId());
        if (dto.getReceiptUrl() != null) expense.setReceiptUrl(dto.getReceiptUrl());
        if (dto.getDescription() != null) expense.setDescription(dto.getDescription());

        Expense saved = expenseRepository.save(expense);
        return mapToDTO(saved);
    }

    @Override
    public ExpenseDTO approveExpense(String id, String approvalStatus, String approvedBy) {
        Expense expense = expenseRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Expense not found with id: " + id));

        expense.setApprovalStatus(approvalStatus.toUpperCase());
        expense.setApprovedBy(approvedBy != null ? approvedBy : "SYSTEM");
        expense.setApprovedAt(Instant.now());

        Expense saved = expenseRepository.save(expense);
        return mapToDTO(saved);
    }

    @Override
    public void deleteExpense(String id) {
        if (!expenseRepository.existsById(id)) {
            throw new IllegalArgumentException("Expense not found with id: " + id);
        }
        expenseRepository.deleteById(id);
    }

    private ExpenseDTO mapToDTO(Expense exp) {
        return ExpenseDTO.builder()
                .id(exp.getId())
                .expenseNumber(exp.getExpenseNumber())
                .categoryId(exp.getCategoryId())
                .categoryName(exp.getCategoryName())
                .title(exp.getTitle())
                .amount(exp.getAmount())
                .expenseDate(exp.getExpenseDate())
                .vendorId(exp.getVendorId())
                .vendorName(exp.getVendorName())
                .employeeId(exp.getEmployeeId())
                .salaryRunId(exp.getSalaryRunId())
                .costCenterId(exp.getCostCenterId())
                .financialYearId(exp.getFinancialYearId())
                .receiptUrl(exp.getReceiptUrl())
                .approvalStatus(exp.getApprovalStatus())
                .approvedBy(exp.getApprovedBy())
                .approvedAt(exp.getApprovedAt())
                .description(exp.getDescription())
                .createdAt(exp.getCreatedAt())
                .updatedAt(exp.getUpdatedAt())
                .createdBy(exp.getCreatedBy())
                .updatedBy(exp.getUpdatedBy())
                .build();
    }
}
