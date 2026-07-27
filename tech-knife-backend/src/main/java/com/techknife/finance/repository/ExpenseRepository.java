package com.techknife.finance.repository;

import com.techknife.finance.entity.Expense;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface ExpenseRepository extends MongoRepository<Expense, String> {

    Optional<Expense> findByExpenseNumber(String expenseNumber);

    List<Expense> findByCategoryId(String categoryId);

    List<Expense> findByVendorId(String vendorId);

    List<Expense> findByEmployeeId(String employeeId);

    List<Expense> findByApprovalStatus(String approvalStatus);

    boolean existsByExpenseNumber(String expenseNumber);

    List<Expense> findByExpenseNumberContainingIgnoreCaseOrTitleContainingIgnoreCaseOrVendorNameContainingIgnoreCase(
            String num, String title, String vendor);

    List<Expense> findByExpenseDateBetween(LocalDate startDate, LocalDate endDate);
}
