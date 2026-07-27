package com.techknife.payroll.repository;

import com.techknife.payroll.entity.Deduction;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DeductionRepository extends MongoRepository<Deduction, String> {
    List<Deduction> findByEmployeeId(String employeeId);
}
