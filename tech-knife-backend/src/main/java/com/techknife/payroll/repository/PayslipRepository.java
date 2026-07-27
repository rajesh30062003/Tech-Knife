package com.techknife.payroll.repository;

import com.techknife.payroll.entity.Payslip;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PayslipRepository extends MongoRepository<Payslip, String> {
    List<Payslip> findByEmployeeId(String employeeId);
    List<Payslip> findByPayrollRunId(String payrollRunId);
}
