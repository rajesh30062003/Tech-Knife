package com.techknife.finance.service;

import com.techknife.finance.dto.TaxRuleDTO;

import java.util.List;

public interface TaxRuleService {

    List<TaxRuleDTO> getAllTaxRules();

    List<TaxRuleDTO> getTaxRulesByType(String taxType);

    TaxRuleDTO getTaxRuleById(String id);

    TaxRuleDTO createTaxRule(TaxRuleDTO dto);

    TaxRuleDTO updateTaxRule(String id, TaxRuleDTO dto);

    void deleteTaxRule(String id);
}
