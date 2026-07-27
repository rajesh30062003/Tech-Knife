package com.techknife.payroll.service.impl;

import com.techknife.payroll.dto.DeductionDTO;
import com.techknife.payroll.entity.Deduction;
import com.techknife.payroll.repository.DeductionRepository;
import com.techknife.payroll.service.DeductionService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DeductionServiceImpl implements DeductionService {

    private final DeductionRepository deductionRepository;

    @Override
    public List<DeductionDTO> getAllDeductions() {
        return deductionRepository.findAll().stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<DeductionDTO> getDeductionsByEmployeeId(String employeeId) {
        return deductionRepository.findByEmployeeId(employeeId).stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public DeductionDTO getDeductionById(String id) {
        Deduction deduction = deductionRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Deduction record not found with id: " + id));
        return mapToDTO(deduction);
    }

    @Override
    public DeductionDTO createDeduction(DeductionDTO dto) {
        Deduction deduction = Deduction.builder()
                .employeeId(dto.getEmployeeId())
                .employeeName(dto.getEmployeeName())
                .deductionType(dto.getDeductionType() != null ? dto.getDeductionType() : "TAX")
                .amount(dto.getAmount())
                .recurring(dto.getRecurring() != null ? dto.getRecurring() : false)
                .status(dto.getStatus() != null ? dto.getStatus() : "ACTIVE")
                .build();

        Deduction saved = deductionRepository.save(deduction);
        return mapToDTO(saved);
    }

    @Override
    public DeductionDTO updateDeduction(String id, DeductionDTO dto) {
        Deduction existing = deductionRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Deduction record not found with id: " + id));

        if (dto.getEmployeeId() != null) existing.setEmployeeId(dto.getEmployeeId());
        if (dto.getEmployeeName() != null) existing.setEmployeeName(dto.getEmployeeName());
        if (dto.getDeductionType() != null) existing.setDeductionType(dto.getDeductionType());
        if (dto.getAmount() != null) existing.setAmount(dto.getAmount());
        if (dto.getRecurring() != null) existing.setRecurring(dto.getRecurring());
        if (dto.getStatus() != null) existing.setStatus(dto.getStatus());

        Deduction saved = deductionRepository.save(existing);
        return mapToDTO(saved);
    }

    @Override
    public void deleteDeduction(String id) {
        if (!deductionRepository.existsById(id)) {
            throw new IllegalArgumentException("Deduction record not found with id: " + id);
        }
        deductionRepository.deleteById(id);
    }

    private DeductionDTO mapToDTO(Deduction d) {
        return DeductionDTO.builder()
                .id(d.getId())
                .employeeId(d.getEmployeeId())
                .employeeName(d.getEmployeeName())
                .deductionType(d.getDeductionType())
                .amount(d.getAmount())
                .recurring(d.getRecurring())
                .status(d.getStatus())
                .createdAt(d.getCreatedAt())
                .updatedAt(d.getUpdatedAt())
                .createdBy(d.getCreatedBy())
                .updatedBy(d.getUpdatedBy())
                .build();
    }
}
