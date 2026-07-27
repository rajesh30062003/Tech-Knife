package com.techknife.finance.service.impl;

import com.techknife.finance.dto.TaxRuleDTO;
import com.techknife.finance.entity.TaxRule;
import com.techknife.finance.repository.TaxRuleRepository;
import com.techknife.finance.service.TaxRuleService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TaxRuleServiceImpl implements TaxRuleService {

    private final TaxRuleRepository taxRuleRepository;

    @Override
    public List<TaxRuleDTO> getAllTaxRules() {
        return taxRuleRepository.findAll().stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<TaxRuleDTO> getTaxRulesByType(String taxType) {
        return taxRuleRepository.findByTaxType(taxType.toUpperCase()).stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public TaxRuleDTO getTaxRuleById(String id) {
        TaxRule rule = taxRuleRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Tax rule not found with id: " + id));
        return mapToDTO(rule);
    }

    @Override
    public TaxRuleDTO createTaxRule(TaxRuleDTO dto) {
        if (taxRuleRepository.existsByRuleCode(dto.getRuleCode())) {
            throw new IllegalArgumentException("Tax rule code already exists: " + dto.getRuleCode());
        }

        TaxRule rule = TaxRule.builder()
                .ruleCode(dto.getRuleCode())
                .ruleName(dto.getRuleName())
                .taxType(dto.getTaxType().toUpperCase())
                .rate(dto.getRate())
                .description(dto.getDescription())
                .status(dto.getStatus() != null ? dto.getStatus() : "ACTIVE")
                .build();

        TaxRule saved = taxRuleRepository.save(rule);
        return mapToDTO(saved);
    }

    @Override
    public TaxRuleDTO updateTaxRule(String id, TaxRuleDTO dto) {
        TaxRule rule = taxRuleRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Tax rule not found with id: " + id));

        if (dto.getRuleName() != null) rule.setRuleName(dto.getRuleName());
        if (dto.getTaxType() != null) rule.setTaxType(dto.getTaxType().toUpperCase());
        if (dto.getRate() != null) rule.setRate(dto.getRate());
        if (dto.getDescription() != null) rule.setDescription(dto.getDescription());
        if (dto.getStatus() != null) rule.setStatus(dto.getStatus());

        TaxRule saved = taxRuleRepository.save(rule);
        return mapToDTO(saved);
    }

    @Override
    public void deleteTaxRule(String id) {
        if (!taxRuleRepository.existsById(id)) {
            throw new IllegalArgumentException("Tax rule not found with id: " + id);
        }
        taxRuleRepository.deleteById(id);
    }

    private TaxRuleDTO mapToDTO(TaxRule r) {
        return TaxRuleDTO.builder()
                .id(r.getId())
                .ruleCode(r.getRuleCode())
                .ruleName(r.getRuleName())
                .taxType(r.getTaxType())
                .rate(r.getRate())
                .description(r.getDescription())
                .status(r.getStatus())
                .createdAt(r.getCreatedAt())
                .updatedAt(r.getUpdatedAt())
                .createdBy(r.getCreatedBy())
                .updatedBy(r.getUpdatedBy())
                .build();
    }
}
