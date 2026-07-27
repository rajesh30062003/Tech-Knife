package com.techknife.payroll.service.impl;

import com.techknife.payroll.dto.LoanDTO;
import com.techknife.payroll.dto.LoanRepaymentDTO;
import com.techknife.payroll.entity.Loan;
import com.techknife.payroll.entity.LoanRepayment;
import com.techknife.payroll.repository.LoanRepository;
import com.techknife.payroll.repository.LoanRepaymentRepository;
import com.techknife.payroll.service.LoanService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class LoanServiceImpl implements LoanService {

    private final LoanRepository loanRepository;
    private final LoanRepaymentRepository loanRepaymentRepository;

    @Override
    public List<LoanDTO> getAllLoans() {
        return loanRepository.findAll().stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<LoanDTO> getLoansByEmployeeId(String employeeId) {
        return loanRepository.findByEmployeeId(employeeId).stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public LoanDTO getLoanById(String id) {
        Loan loan = loanRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Loan record not found with id: " + id));
        return mapToDTO(loan);
    }

    @Override
    public LoanDTO applyForLoan(LoanDTO dto) {
        BigDecimal amount = dto.getAmount() != null ? dto.getAmount() : BigDecimal.ZERO;
        Loan loan = Loan.builder()
                .employeeId(dto.getEmployeeId())
                .employeeName(dto.getEmployeeName())
                .loanType(dto.getLoanType() != null ? dto.getLoanType() : "PERSONAL")
                .amount(amount)
                .interestRate(dto.getInterestRate() != null ? dto.getInterestRate() : BigDecimal.ZERO)
                .termMonths(dto.getTermMonths() != null ? dto.getTermMonths() : 12)
                .emiAmount(dto.getEmiAmount() != null ? dto.getEmiAmount() : BigDecimal.ZERO)
                .remainingAmount(dto.getRemainingAmount() != null ? dto.getRemainingAmount() : amount)
                .status(dto.getStatus() != null ? dto.getStatus() : "PENDING")
                .build();

        Loan saved = loanRepository.save(loan);
        return mapToDTO(saved);
    }

    @Override
    public LoanDTO updateLoanStatus(String id, String status) {
        Loan loan = loanRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Loan record not found with id: " + id));

        loan.setStatus(status != null ? status.toUpperCase() : "PENDING");
        Loan saved = loanRepository.save(loan);
        return mapToDTO(saved);
    }

    @Override
    public List<LoanRepaymentDTO> getRepaymentsByLoanId(String loanId) {
        return loanRepaymentRepository.findByLoanId(loanId).stream()
                .map(this::mapRepaymentToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public LoanRepaymentDTO recordRepayment(LoanRepaymentDTO dto) {
        Loan loan = loanRepository.findById(dto.getLoanId())
                .orElseThrow(() -> new IllegalArgumentException("Loan record not found with id: " + dto.getLoanId()));

        BigDecimal amountPaid = dto.getAmountPaid() != null ? dto.getAmountPaid() : BigDecimal.ZERO;
        BigDecimal currentRemaining = loan.getRemainingAmount() != null ? loan.getRemainingAmount() : BigDecimal.ZERO;
        BigDecimal newRemaining = currentRemaining.subtract(amountPaid).max(BigDecimal.ZERO);

        LoanRepayment repayment = LoanRepayment.builder()
                .loanId(loan.getId())
                .repaymentDate(dto.getRepaymentDate())
                .amountPaid(amountPaid)
                .principalAmount(dto.getPrincipalAmount() != null ? dto.getPrincipalAmount() : amountPaid)
                .interestAmount(dto.getInterestAmount() != null ? dto.getInterestAmount() : BigDecimal.ZERO)
                .remainingBalance(newRemaining)
                .paymentMode(dto.getPaymentMode() != null ? dto.getPaymentMode() : "SALARY_DEDUCTION")
                .build();

        LoanRepayment saved = loanRepaymentRepository.save(repayment);

        loan.setRemainingAmount(newRemaining);
        if (newRemaining.compareTo(BigDecimal.ZERO) == 0) {
            loan.setStatus("CLOSED");
        }
        loanRepository.save(loan);

        return mapRepaymentToDTO(saved);
    }

    private LoanDTO mapToDTO(Loan l) {
        return LoanDTO.builder()
                .id(l.getId())
                .employeeId(l.getEmployeeId())
                .employeeName(l.getEmployeeName())
                .loanType(l.getLoanType())
                .amount(l.getAmount())
                .interestRate(l.getInterestRate())
                .termMonths(l.getTermMonths())
                .emiAmount(l.getEmiAmount())
                .remainingAmount(l.getRemainingAmount())
                .status(l.getStatus())
                .createdAt(l.getCreatedAt())
                .updatedAt(l.getUpdatedAt())
                .createdBy(l.getCreatedBy())
                .updatedBy(l.getUpdatedBy())
                .build();
    }

    private LoanRepaymentDTO mapRepaymentToDTO(LoanRepayment r) {
        return LoanRepaymentDTO.builder()
                .id(r.getId())
                .loanId(r.getLoanId())
                .repaymentDate(r.getRepaymentDate())
                .amountPaid(r.getAmountPaid())
                .principalAmount(r.getPrincipalAmount())
                .interestAmount(r.getInterestAmount())
                .remainingBalance(r.getRemainingBalance())
                .paymentMode(r.getPaymentMode())
                .createdAt(r.getCreatedAt())
                .updatedAt(r.getUpdatedAt())
                .createdBy(r.getCreatedBy())
                .updatedBy(r.getUpdatedBy())
                .build();
    }
}
