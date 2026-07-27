package com.techknife.payroll.service.impl;

import com.techknife.payroll.dto.SalaryComponentDTO;
import com.techknife.payroll.entity.SalaryComponent;
import com.techknife.payroll.repository.SalaryComponentRepository;
import com.techknife.payroll.service.SalaryComponentService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SalaryComponentServiceImpl implements SalaryComponentService {

    private final SalaryComponentRepository salaryComponentRepository;

    @Override
    public List<SalaryComponentDTO> getAllComponents() {
        return salaryComponentRepository.findAll().stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public SalaryComponentDTO getComponentById(String id) {
        SalaryComponent component = salaryComponentRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Salary component not found with id: " + id));
        return mapToDTO(component);
    }

    @Override
    public SalaryComponentDTO createComponent(SalaryComponentDTO dto) {
        SalaryComponent component = SalaryComponent.builder()
                .componentCode(dto.getComponentCode())
                .componentName(dto.getComponentName())
                .componentType(dto.getComponentType() != null ? dto.getComponentType() : "EARNING")
                .calculationType(dto.getCalculationType() != null ? dto.getCalculationType() : "FIXED")
                .percentageValue(dto.getPercentageValue())
                .baseComponent(dto.getBaseComponent())
                .isTaxable(dto.getIsTaxable() != null ? dto.getIsTaxable() : true)
                .isStatutory(dto.getIsStatutory() != null ? dto.getIsStatutory() : false)
                .status(dto.getStatus() != null ? dto.getStatus() : "ACTIVE")
                .build();

        SalaryComponent saved = salaryComponentRepository.save(component);
        return mapToDTO(saved);
    }

    @Override
    public SalaryComponentDTO updateComponent(String id, SalaryComponentDTO dto) {
        SalaryComponent existing = salaryComponentRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Salary component not found with id: " + id));

        if (dto.getComponentCode() != null) existing.setComponentCode(dto.getComponentCode());
        if (dto.getComponentName() != null) existing.setComponentName(dto.getComponentName());
        if (dto.getComponentType() != null) existing.setComponentType(dto.getComponentType());
        if (dto.getCalculationType() != null) existing.setCalculationType(dto.getCalculationType());
        if (dto.getPercentageValue() != null) existing.setPercentageValue(dto.getPercentageValue());
        if (dto.getBaseComponent() != null) existing.setBaseComponent(dto.getBaseComponent());
        if (dto.getIsTaxable() != null) existing.setIsTaxable(dto.getIsTaxable());
        if (dto.getIsStatutory() != null) existing.setIsStatutory(dto.getIsStatutory());
        if (dto.getStatus() != null) existing.setStatus(dto.getStatus());

        SalaryComponent saved = salaryComponentRepository.save(existing);
        return mapToDTO(saved);
    }

    @Override
    public void deleteComponent(String id) {
        if (!salaryComponentRepository.existsById(id)) {
            throw new IllegalArgumentException("Salary component not found with id: " + id);
        }
        salaryComponentRepository.deleteById(id);
    }

    private SalaryComponentDTO mapToDTO(SalaryComponent c) {
        return SalaryComponentDTO.builder()
                .id(c.getId())
                .componentCode(c.getComponentCode())
                .componentName(c.getComponentName())
                .componentType(c.getComponentType())
                .calculationType(c.getCalculationType())
                .percentageValue(c.getPercentageValue())
                .baseComponent(c.getBaseComponent())
                .isTaxable(c.getIsTaxable())
                .isStatutory(c.getIsStatutory())
                .status(c.getStatus())
                .createdAt(c.getCreatedAt())
                .updatedAt(c.getUpdatedAt())
                .createdBy(c.getCreatedBy())
                .updatedBy(c.getUpdatedBy())
                .build();
    }
}
