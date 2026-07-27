package com.techknife.payroll.service.impl;

import com.techknife.payroll.dto.EmployeeSalaryDTO;
import com.techknife.payroll.entity.EmployeeSalary;
import com.techknife.payroll.repository.EmployeeSalaryRepository;
import com.techknife.payroll.service.EmployeeSalaryService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class EmployeeSalaryServiceImpl implements EmployeeSalaryService {

    private final EmployeeSalaryRepository employeeSalaryRepository;

    @Override
    public List<EmployeeSalaryDTO> getAllEmployeeSalaries() {
        return employeeSalaryRepository.findAll().stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<EmployeeSalaryDTO> getSalariesByEmployeeId(String employeeId) {
        return employeeSalaryRepository.findByEmployeeId(employeeId).stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public EmployeeSalaryDTO getEmployeeSalaryById(String id) {
        EmployeeSalary es = employeeSalaryRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Employee salary record not found with id: " + id));
        return mapToDTO(es);
    }

    @Override
    public EmployeeSalaryDTO createEmployeeSalary(EmployeeSalaryDTO dto) {
        EmployeeSalary es = EmployeeSalary.builder()
                .employeeId(dto.getEmployeeId())
                .employeeName(dto.getEmployeeName())
                .salaryStructureId(dto.getSalaryStructureId())
                .salaryStructureName(dto.getSalaryStructureName())
                .baseSalary(dto.getBaseSalary())
                .currency(dto.getCurrency() != null ? dto.getCurrency() : "USD")
                .effectiveDate(dto.getEffectiveDate())
                .bankName(dto.getBankName())
                .accountNumber(dto.getAccountNumber())
                .ifscOrSwiftCode(dto.getIfscOrSwiftCode())
                .status(dto.getStatus() != null ? dto.getStatus() : "ACTIVE")
                .build();

        EmployeeSalary saved = employeeSalaryRepository.save(es);
        return mapToDTO(saved);
    }

    @Override
    public EmployeeSalaryDTO updateEmployeeSalary(String id, EmployeeSalaryDTO dto) {
        EmployeeSalary existing = employeeSalaryRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Employee salary record not found with id: " + id));

        if (dto.getEmployeeId() != null) existing.setEmployeeId(dto.getEmployeeId());
        if (dto.getEmployeeName() != null) existing.setEmployeeName(dto.getEmployeeName());
        if (dto.getSalaryStructureId() != null) existing.setSalaryStructureId(dto.getSalaryStructureId());
        if (dto.getSalaryStructureName() != null) existing.setSalaryStructureName(dto.getSalaryStructureName());
        if (dto.getBaseSalary() != null) existing.setBaseSalary(dto.getBaseSalary());
        if (dto.getCurrency() != null) existing.setCurrency(dto.getCurrency());
        if (dto.getEffectiveDate() != null) existing.setEffectiveDate(dto.getEffectiveDate());
        if (dto.getBankName() != null) existing.setBankName(dto.getBankName());
        if (dto.getAccountNumber() != null) existing.setAccountNumber(dto.getAccountNumber());
        if (dto.getIfscOrSwiftCode() != null) existing.setIfscOrSwiftCode(dto.getIfscOrSwiftCode());
        if (dto.getStatus() != null) existing.setStatus(dto.getStatus());

        EmployeeSalary saved = employeeSalaryRepository.save(existing);
        return mapToDTO(saved);
    }

    @Override
    public void deleteEmployeeSalary(String id) {
        if (!employeeSalaryRepository.existsById(id)) {
            throw new IllegalArgumentException("Employee salary record not found with id: " + id);
        }
        employeeSalaryRepository.deleteById(id);
    }

    private EmployeeSalaryDTO mapToDTO(EmployeeSalary es) {
        return EmployeeSalaryDTO.builder()
                .id(es.getId())
                .employeeId(es.getEmployeeId())
                .employeeName(es.getEmployeeName())
                .salaryStructureId(es.getSalaryStructureId())
                .salaryStructureName(es.getSalaryStructureName())
                .baseSalary(es.getBaseSalary())
                .currency(es.getCurrency())
                .effectiveDate(es.getEffectiveDate())
                .bankName(es.getBankName())
                .accountNumber(es.getAccountNumber())
                .ifscOrSwiftCode(es.getIfscOrSwiftCode())
                .status(es.getStatus())
                .createdAt(es.getCreatedAt())
                .updatedAt(es.getUpdatedAt())
                .createdBy(es.getCreatedBy())
                .updatedBy(es.getUpdatedBy())
                .build();
    }
}
