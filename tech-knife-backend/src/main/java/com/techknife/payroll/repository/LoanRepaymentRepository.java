package com.techknife.payroll.repository;

import com.techknife.payroll.entity.LoanRepayment;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LoanRepaymentRepository extends MongoRepository<LoanRepayment, String> {
    List<LoanRepayment> findByLoanId(String loanId);
}
