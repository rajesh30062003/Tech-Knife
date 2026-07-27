package com.techknife.payroll.repository;

import com.techknife.payroll.entity.Reimbursement;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReimbursementRepository extends MongoRepository<Reimbursement, String> {
    List<Reimbursement> findByEmployeeId(String employeeId);
}
