package com.techknife.payroll.repository;

import com.techknife.payroll.entity.EmployeeSalary;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface EmployeeSalaryRepository extends MongoRepository<EmployeeSalary, String> {
    List<EmployeeSalary> findByEmployeeId(String employeeId);
    Optional<EmployeeSalary> findByEmployeeIdAndStatus(String employeeId, String status);
}
