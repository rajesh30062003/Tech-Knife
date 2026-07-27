package com.techknife.finance.repository;

import com.techknife.finance.entity.TaxRule;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TaxRuleRepository extends MongoRepository<TaxRule, String> {

    Optional<TaxRule> findByRuleCode(String ruleCode);

    List<TaxRule> findByTaxType(String taxType);

    boolean existsByRuleCode(String ruleCode);
}
