package com.techknife.payroll.repository;

import com.techknife.payroll.entity.SalaryComponent;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface SalaryComponentRepository extends MongoRepository<SalaryComponent, String> {
    Optional<SalaryComponent> findByComponentCode(String componentCode);
}
