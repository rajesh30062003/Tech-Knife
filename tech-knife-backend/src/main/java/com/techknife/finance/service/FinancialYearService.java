package com.techknife.finance.service;

import com.techknife.finance.dto.FinancialYearDTO;

import java.util.List;

public interface FinancialYearService {

    List<FinancialYearDTO> getAllFinancialYears();

    FinancialYearDTO getFinancialYearById(String id);

    FinancialYearDTO createFinancialYear(FinancialYearDTO dto);

    FinancialYearDTO updateFinancialYearStatus(String id, String status, Boolean isLocked);
}
