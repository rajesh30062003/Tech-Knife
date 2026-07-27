package com.techknife.finance.service;

import com.techknife.finance.dto.GeneralLedgerDTO;

import java.time.LocalDate;
import java.util.List;

public interface GeneralLedgerService {

    List<GeneralLedgerDTO> getAllLedgerEntries();

    List<GeneralLedgerDTO> getLedgerEntriesByAccount(String accountId);

    List<GeneralLedgerDTO> getLedgerEntriesByAccountAndDateRange(String accountId, LocalDate startDate, LocalDate endDate);

    List<GeneralLedgerDTO> getLedgerEntriesByFinancialYear(String financialYearId);
}
