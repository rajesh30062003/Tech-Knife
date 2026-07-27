package com.techknife.payroll.repository;

import com.techknife.payroll.entity.TaxConfiguration;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TaxConfigurationRepository extends MongoRepository<TaxConfiguration, String> {
    List<TaxConfiguration> findByFinancialYear(String financialYear);
}
