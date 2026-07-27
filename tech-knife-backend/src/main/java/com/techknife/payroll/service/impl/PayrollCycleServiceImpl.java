package com.techknife.payroll.service.impl;

import com.techknife.payroll.dto.PayrollCycleDTO;
import com.techknife.payroll.entity.PayrollCycle;
import com.techknife.payroll.repository.PayrollCycleRepository;
import com.techknife.payroll.service.PayrollCycleService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PayrollCycleServiceImpl implements PayrollCycleService {

    private final PayrollCycleRepository payrollCycleRepository;

    @Override
    public List<PayrollCycleDTO> getAllCycles() {
        return payrollCycleRepository.findAll().stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public PayrollCycleDTO getCycleById(String id) {
        PayrollCycle cycle = payrollCycleRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Payroll cycle not found with id: " + id));
        return mapToDTO(cycle);
    }

    @Override
    public PayrollCycleDTO createCycle(PayrollCycleDTO dto) {
        PayrollCycle cycle = PayrollCycle.builder()
                .cycleName(dto.getCycleName())
                .month(dto.getMonth())
                .year(dto.getYear())
                .startDate(dto.getStartDate())
                .endDate(dto.getEndDate())
                .processingDate(dto.getProcessingDate())
                .status(dto.getStatus() != null ? dto.getStatus() : "OPEN")
                .build();

        PayrollCycle saved = payrollCycleRepository.save(cycle);
        return mapToDTO(saved);
    }

    @Override
    public PayrollCycleDTO updateCycle(String id, PayrollCycleDTO dto) {
        PayrollCycle existing = payrollCycleRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Payroll cycle not found with id: " + id));

        if (dto.getCycleName() != null) existing.setCycleName(dto.getCycleName());
        if (dto.getMonth() != null) existing.setMonth(dto.getMonth());
        if (dto.getYear() != null) existing.setYear(dto.getYear());
        if (dto.getStartDate() != null) existing.setStartDate(dto.getStartDate());
        if (dto.getEndDate() != null) existing.setEndDate(dto.getEndDate());
        if (dto.getProcessingDate() != null) existing.setProcessingDate(dto.getProcessingDate());
        if (dto.getStatus() != null) existing.setStatus(dto.getStatus());

        PayrollCycle saved = payrollCycleRepository.save(existing);
        return mapToDTO(saved);
    }

    @Override
    public void deleteCycle(String id) {
        if (!payrollCycleRepository.existsById(id)) {
            throw new IllegalArgumentException("Payroll cycle not found with id: " + id);
        }
        payrollCycleRepository.deleteById(id);
    }

    private PayrollCycleDTO mapToDTO(PayrollCycle c) {
        return PayrollCycleDTO.builder()
                .id(c.getId())
                .cycleName(c.getCycleName())
                .month(c.getMonth())
                .year(c.getYear())
                .startDate(c.getStartDate())
                .endDate(c.getEndDate())
                .processingDate(c.getProcessingDate())
                .status(c.getStatus())
                .createdAt(c.getCreatedAt())
                .updatedAt(c.getUpdatedAt())
                .createdBy(c.getCreatedBy())
                .updatedBy(c.getUpdatedBy())
                .build();
    }
}
