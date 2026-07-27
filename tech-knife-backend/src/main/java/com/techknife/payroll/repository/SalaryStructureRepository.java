package com.techknife.payroll.repository;

import com.techknife.payroll.entity.SalaryStructure;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface SalaryStructureRepository extends MongoRepository<SalaryStructure, String> {
    Optional<SalaryStructure> findByStructureCode(String structureCode);
}
