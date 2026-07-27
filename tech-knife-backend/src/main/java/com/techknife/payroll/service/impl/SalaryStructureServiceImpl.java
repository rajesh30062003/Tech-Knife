package com.techknife.payroll.service.impl;

import com.techknife.payroll.dto.SalaryStructureDTO;
import com.techknife.payroll.entity.SalaryStructure;
import com.techknife.payroll.repository.SalaryStructureRepository;
import com.techknife.payroll.service.SalaryStructureService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SalaryStructureServiceImpl implements SalaryStructureService {

    private final SalaryStructureRepository salaryStructureRepository;

    @Override
    public List<SalaryStructureDTO> getAllStructures() {
        return salaryStructureRepository.findAll().stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public SalaryStructureDTO getStructureById(String id) {
        SalaryStructure structure = salaryStructureRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Salary structure not found with id: " + id));
        return mapToDTO(structure);
    }

    @Override
    public SalaryStructureDTO createStructure(SalaryStructureDTO dto) {
        SalaryStructure structure = SalaryStructure.builder()
                .structureCode(dto.getStructureCode())
                .structureName(dto.getStructureName())
                .employeeType(dto.getEmployeeType())
                .grade(dto.getGrade())
                .effectiveDate(dto.getEffectiveDate())
                .status(dto.getStatus() != null ? dto.getStatus() : "ACTIVE")
                .build();

        SalaryStructure saved = salaryStructureRepository.save(structure);
        return mapToDTO(saved);
    }

    @Override
    public SalaryStructureDTO updateStructure(String id, SalaryStructureDTO dto) {
        SalaryStructure existing = salaryStructureRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Salary structure not found with id: " + id));

        if (dto.getStructureCode() != null) existing.setStructureCode(dto.getStructureCode());
        if (dto.getStructureName() != null) existing.setStructureName(dto.getStructureName());
        if (dto.getEmployeeType() != null) existing.setEmployeeType(dto.getEmployeeType());
        if (dto.getGrade() != null) existing.setGrade(dto.getGrade());
        if (dto.getEffectiveDate() != null) existing.setEffectiveDate(dto.getEffectiveDate());
        if (dto.getStatus() != null) existing.setStatus(dto.getStatus());

        SalaryStructure saved = salaryStructureRepository.save(existing);
        return mapToDTO(saved);
    }

    @Override
    public void deleteStructure(String id) {
        if (!salaryStructureRepository.existsById(id)) {
            throw new IllegalArgumentException("Salary structure not found with id: " + id);
        }
        salaryStructureRepository.deleteById(id);
    }

    private SalaryStructureDTO mapToDTO(SalaryStructure s) {
        return SalaryStructureDTO.builder()
                .id(s.getId())
                .structureCode(s.getStructureCode())
                .structureName(s.getStructureName())
                .employeeType(s.getEmployeeType())
                .grade(s.getGrade())
                .effectiveDate(s.getEffectiveDate())
                .status(s.getStatus())
                .createdAt(s.getCreatedAt())
                .updatedAt(s.getUpdatedAt())
                .createdBy(s.getCreatedBy())
                .updatedBy(s.getUpdatedBy())
                .build();
    }
}
