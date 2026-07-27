package com.techknife.payroll.repository;

import com.techknife.payroll.entity.PayrollRun;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PayrollRunRepository extends MongoRepository<PayrollRun, String> {
    List<PayrollRun> findByPayrollCycleId(String payrollCycleId);
}
