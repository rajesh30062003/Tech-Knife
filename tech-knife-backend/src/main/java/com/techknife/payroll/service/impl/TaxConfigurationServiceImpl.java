package com.techknife.payroll.service.impl;

import com.techknife.payroll.dto.TaxConfigurationDTO;
import com.techknife.payroll.entity.TaxConfiguration;
import com.techknife.payroll.repository.TaxConfigurationRepository;
import com.techknife.payroll.service.TaxConfigurationService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TaxConfigurationServiceImpl implements TaxConfigurationService {

    private final TaxConfigurationRepository taxConfigurationRepository;

    @Override
    public List<TaxConfigurationDTO> getAllTaxConfigurations() {
        return taxConfigurationRepository.findAll().stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<TaxConfigurationDTO> getTaxConfigurationsByYear(String year) {
        return taxConfigurationRepository.findByFinancialYear(year).stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public TaxConfigurationDTO getTaxConfigurationById(String id) {
        TaxConfiguration tc = taxConfigurationRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Tax configuration not found with id: " + id));
        return mapToDTO(tc);
    }

    @Override
    public TaxConfigurationDTO createTaxConfiguration(TaxConfigurationDTO dto) {
        TaxConfiguration tc = TaxConfiguration.builder()
                .financialYear(dto.getFinancialYear())
                .taxSlabName(dto.getTaxSlabName())
                .minIncome(dto.getMinIncome())
                .maxIncome(dto.getMaxIncome())
                .taxRate(dto.getTaxRate())
                .status(dto.getStatus() != null ? dto.getStatus() : "ACTIVE")
                .build();

        TaxConfiguration saved = taxConfigurationRepository.save(tc);
        return mapToDTO(saved);
    }

    @Override
    public TaxConfigurationDTO updateTaxConfiguration(String id, TaxConfigurationDTO dto) {
        TaxConfiguration existing = taxConfigurationRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Tax configuration not found with id: " + id));

        if (dto.getFinancialYear() != null) existing.setFinancialYear(dto.getFinancialYear());
        if (dto.getTaxSlabName() != null) existing.setTaxSlabName(dto.getTaxSlabName());
        if (dto.getMinIncome() != null) existing.setMinIncome(dto.getMinIncome());
        if (dto.getMaxIncome() != null) existing.setMaxIncome(dto.getMaxIncome());
        if (dto.getTaxRate() != null) existing.setTaxRate(dto.getTaxRate());
        if (dto.getStatus() != null) existing.setStatus(dto.getStatus());

        TaxConfiguration saved = taxConfigurationRepository.save(existing);
        return mapToDTO(saved);
    }

    @Override
    public void deleteTaxConfiguration(String id) {
        if (!taxConfigurationRepository.existsById(id)) {
            throw new IllegalArgumentException("Tax configuration not found with id: " + id);
        }
        taxConfigurationRepository.deleteById(id);
    }

    private TaxConfigurationDTO mapToDTO(TaxConfiguration tc) {
        return TaxConfigurationDTO.builder()
                .id(tc.getId())
                .financialYear(tc.getFinancialYear())
                .taxSlabName(tc.getTaxSlabName())
                .minIncome(tc.getMinIncome())
                .maxIncome(tc.getMaxIncome())
                .taxRate(tc.getTaxRate())
                .status(tc.getStatus())
                .createdAt(tc.getCreatedAt())
                .updatedAt(tc.getUpdatedAt())
                .createdBy(tc.getCreatedBy())
                .updatedBy(tc.getUpdatedBy())
                .build();
    }
}
