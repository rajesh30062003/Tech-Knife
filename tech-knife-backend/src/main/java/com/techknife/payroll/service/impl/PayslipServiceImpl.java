package com.techknife.payroll.service.impl;

import com.techknife.payroll.dto.PayslipDTO;
import com.techknife.payroll.entity.Payslip;
import com.techknife.payroll.repository.PayslipRepository;
import com.techknife.payroll.service.PayslipService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PayslipServiceImpl implements PayslipService {

    private final PayslipRepository payslipRepository;

    @Override
    public List<PayslipDTO> getAllPayslips() {
        return payslipRepository.findAll().stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<PayslipDTO> getPayslipsByEmployeeId(String employeeId) {
        return payslipRepository.findByEmployeeId(employeeId).stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<PayslipDTO> getPayslipsByRunId(String runId) {
        return payslipRepository.findByPayrollRunId(runId).stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public PayslipDTO getPayslipById(String id) {
        Payslip payslip = payslipRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Payslip not found with id: " + id));
        return mapToDTO(payslip);
    }

    @Override
    public PayslipDTO generatePayslip(PayslipDTO dto) {
        Payslip payslip = Payslip.builder()
                .employeeId(dto.getEmployeeId())
                .employeeName(dto.getEmployeeName())
                .payrollRunId(dto.getPayrollRunId())
                .grossPay(dto.getGrossPay())
                .totalDeductions(dto.getTotalDeductions())
                .netPay(dto.getNetPay())
                .paymentDate(dto.getPaymentDate())
                .paymentStatus(dto.getPaymentStatus() != null ? dto.getPaymentStatus() : "PENDING")
                .downloadUrl(dto.getDownloadUrl())
                .build();

        Payslip saved = payslipRepository.save(payslip);
        return mapToDTO(saved);
    }

    private PayslipDTO mapToDTO(Payslip p) {
        return PayslipDTO.builder()
                .id(p.getId())
                .employeeId(p.getEmployeeId())
                .employeeName(p.getEmployeeName())
                .payrollRunId(p.getPayrollRunId())
                .grossPay(p.getGrossPay())
                .totalDeductions(p.getTotalDeductions())
                .netPay(p.getNetPay())
                .paymentDate(p.getPaymentDate())
                .paymentStatus(p.getPaymentStatus())
                .downloadUrl(p.getDownloadUrl())
                .createdAt(p.getCreatedAt())
                .updatedAt(p.getUpdatedAt())
                .createdBy(p.getCreatedBy())
                .updatedBy(p.getUpdatedBy())
                .build();
    }
}
