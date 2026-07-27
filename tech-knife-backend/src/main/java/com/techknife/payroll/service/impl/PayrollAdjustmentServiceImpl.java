package com.techknife.payroll.service.impl;

import com.techknife.payroll.dto.PayrollAdjustmentDTO;
import com.techknife.payroll.entity.PayrollAdjustment;
import com.techknife.payroll.repository.PayrollAdjustmentRepository;
import com.techknife.payroll.service.PayrollAdjustmentService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PayrollAdjustmentServiceImpl implements PayrollAdjustmentService {

    private final PayrollAdjustmentRepository payrollAdjustmentRepository;

    @Override
    public List<PayrollAdjustmentDTO> getAllAdjustments() {
        return payrollAdjustmentRepository.findAll().stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<PayrollAdjustmentDTO> getAdjustmentsByEmployeeId(String employeeId) {
        return payrollAdjustmentRepository.findByEmployeeId(employeeId).stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<PayrollAdjustmentDTO> getAdjustmentsByCycleId(String cycleId) {
        return payrollAdjustmentRepository.findByPayrollCycleId(cycleId).stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public PayrollAdjustmentDTO getAdjustmentById(String id) {
        PayrollAdjustment adj = payrollAdjustmentRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Payroll adjustment not found with id: " + id));
        return mapToDTO(adj);
    }

    @Override
    public PayrollAdjustmentDTO createAdjustment(PayrollAdjustmentDTO dto) {
        PayrollAdjustment adj = PayrollAdjustment.builder()
                .employeeId(dto.getEmployeeId())
                .employeeName(dto.getEmployeeName())
                .payrollCycleId(dto.getPayrollCycleId())
                .adjustmentType(dto.getAdjustmentType() != null ? dto.getAdjustmentType() : "BONUS")
                .amount(dto.getAmount())
                .reason(dto.getReason())
                .status(dto.getStatus() != null ? dto.getStatus() : "PENDING")
                .build();

        PayrollAdjustment saved = payrollAdjustmentRepository.save(adj);
        return mapToDTO(saved);
    }

    @Override
    public PayrollAdjustmentDTO updateAdjustmentStatus(String id, String status) {
        PayrollAdjustment adj = payrollAdjustmentRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Payroll adjustment not found with id: " + id));

        adj.setStatus(status != null ? status.toUpperCase() : "PENDING");
        PayrollAdjustment saved = payrollAdjustmentRepository.save(adj);
        return mapToDTO(saved);
    }

    private PayrollAdjustmentDTO mapToDTO(PayrollAdjustment a) {
        return PayrollAdjustmentDTO.builder()
                .id(a.getId())
                .employeeId(a.getEmployeeId())
                .employeeName(a.getEmployeeName())
                .payrollCycleId(a.getPayrollCycleId())
                .adjustmentType(a.getAdjustmentType())
                .amount(a.getAmount())
                .reason(a.getReason())
                .status(a.getStatus())
                .createdAt(a.getCreatedAt())
                .updatedAt(a.getUpdatedAt())
                .createdBy(a.getCreatedBy())
                .updatedBy(a.getUpdatedBy())
                .build();
    }
}
