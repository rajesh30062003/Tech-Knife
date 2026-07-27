package com.techknife.finance.repository;

import com.techknife.finance.entity.ExpenseCategory;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ExpenseCategoryRepository extends MongoRepository<ExpenseCategory, String> {

    Optional<ExpenseCategory> findByCategoryCode(String categoryCode);

    boolean existsByCategoryCode(String categoryCode);

    boolean existsByCategoryNameIgnoreCase(String categoryName);
}
