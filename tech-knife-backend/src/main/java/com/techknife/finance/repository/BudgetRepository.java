package com.techknife.finance.repository;

import com.techknife.finance.entity.Budget;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BudgetRepository extends MongoRepository<Budget, String> {

    List<Budget> findByFinancialYearId(String financialYearId);

    List<Budget> findByDepartmentId(String departmentId);

    List<Budget> findByProjectId(String projectId);

    List<Budget> findByCostCenterId(String costCenterId);
}
