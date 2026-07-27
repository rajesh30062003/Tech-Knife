package com.techknife.payroll.repository;

import com.techknife.payroll.entity.PayrollAdjustment;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PayrollAdjustmentRepository extends MongoRepository<PayrollAdjustment, String> {
    List<PayrollAdjustment> findByEmployeeId(String employeeId);
    List<PayrollAdjustment> findByPayrollCycleId(String payrollCycleId);
}
