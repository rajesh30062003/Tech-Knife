package com.techknife.finance.repository;

import com.techknife.finance.entity.FinancialYear;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface FinancialYearRepository extends MongoRepository<FinancialYear, String> {

    Optional<FinancialYear> findByYearCode(String yearCode);

    Optional<FinancialYear> findByStatus(String status);

    boolean existsByYearCode(String yearCode);

    List<FinancialYear> findByYearCodeContainingIgnoreCaseOrYearNameContainingIgnoreCase(String code, String name);
}
