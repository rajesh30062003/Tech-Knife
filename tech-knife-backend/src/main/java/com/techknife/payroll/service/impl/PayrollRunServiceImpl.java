package com.techknife.payroll.service.impl;

import com.techknife.payroll.dto.PayrollRunDTO;
import com.techknife.payroll.entity.PayrollRun;
import com.techknife.payroll.repository.PayrollRunRepository;
import com.techknife.payroll.service.PayrollRunService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PayrollRunServiceImpl implements PayrollRunService {

    private final PayrollRunRepository payrollRunRepository;

    @Override
    public List<PayrollRunDTO> getAllRuns() {
        return payrollRunRepository.findAll().stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<PayrollRunDTO> getRunsByCycleId(String cycleId) {
        return payrollRunRepository.findByPayrollCycleId(cycleId).stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public PayrollRunDTO getRunById(String id) {
        PayrollRun run = payrollRunRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Payroll run not found with id: " + id));
        return mapToDTO(run);
    }

    @Override
    public PayrollRunDTO processPayrollRun(PayrollRunDTO dto) {
        PayrollRun run = PayrollRun.builder()
                .payrollCycleId(dto.getPayrollCycleId())
                .payrollCycleName(dto.getPayrollCycleName())
                .totalEmployees(dto.getTotalEmployees() != null ? dto.getTotalEmployees() : 0)
                .totalGrossPay(dto.getTotalGrossPay() != null ? dto.getTotalGrossPay() : BigDecimal.ZERO)
                .totalNetPay(dto.getTotalNetPay() != null ? dto.getTotalNetPay() : BigDecimal.ZERO)
                .totalDeductions(dto.getTotalDeductions() != null ? dto.getTotalDeductions() : BigDecimal.ZERO)
                .status(dto.getStatus() != null ? dto.getStatus() : "PROCESSING")
                .processedBy(dto.getProcessedBy())
                .build();

        PayrollRun saved = payrollRunRepository.save(run);
        return mapToDTO(saved);
    }

    @Override
    public PayrollRunDTO updateRunStatus(String id, String status) {
        PayrollRun run = payrollRunRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Payroll run not found with id: " + id));

        run.setStatus(status != null ? status.toUpperCase() : "PROCESSING");
        PayrollRun saved = payrollRunRepository.save(run);
        return mapToDTO(saved);
    }

    private PayrollRunDTO mapToDTO(PayrollRun r) {
        return PayrollRunDTO.builder()
                .id(r.getId())
                .payrollCycleId(r.getPayrollCycleId())
                .payrollCycleName(r.getPayrollCycleName())
                .totalEmployees(r.getTotalEmployees())
                .totalGrossPay(r.getTotalGrossPay())
                .totalNetPay(r.getTotalNetPay())
                .totalDeductions(r.getTotalDeductions())
                .status(r.getStatus())
                .processedBy(r.getProcessedBy())
                .createdAt(r.getCreatedAt())
                .updatedAt(r.getUpdatedAt())
                .createdBy(r.getCreatedBy())
                .updatedBy(r.getUpdatedBy())
                .build();
    }
}
