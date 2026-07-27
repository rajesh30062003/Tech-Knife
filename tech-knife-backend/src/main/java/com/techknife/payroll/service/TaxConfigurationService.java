package com.techknife.payroll.service;

import com.techknife.payroll.dto.TaxConfigurationDTO;

import java.util.List;

public interface TaxConfigurationService {
    List<TaxConfigurationDTO> getAllTaxConfigurations();
    List<TaxConfigurationDTO> getTaxConfigurationsByYear(String year);
    TaxConfigurationDTO getTaxConfigurationById(String id);
    TaxConfigurationDTO createTaxConfiguration(TaxConfigurationDTO dto);
    TaxConfigurationDTO updateTaxConfiguration(String id, TaxConfigurationDTO dto);
    void deleteTaxConfiguration(String id);
}
