package com.techknife.payroll.repository;

import com.techknife.payroll.entity.PayrollCycle;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PayrollCycleRepository extends MongoRepository<PayrollCycle, String> {
    List<PayrollCycle> findByStatus(String status);
}
