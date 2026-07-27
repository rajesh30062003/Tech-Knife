package com.techknife.payroll.service;

import com.techknife.payroll.dto.LoanDTO;
import com.techknife.payroll.dto.LoanRepaymentDTO;

import java.util.List;

public interface LoanService {
    List<LoanDTO> getAllLoans();
    List<LoanDTO> getLoansByEmployeeId(String employeeId);
    LoanDTO getLoanById(String id);
    LoanDTO applyForLoan(LoanDTO dto);
    LoanDTO updateLoanStatus(String id, String status);
    List<LoanRepaymentDTO> getRepaymentsByLoanId(String loanId);
    LoanRepaymentDTO recordRepayment(LoanRepaymentDTO dto);
}
