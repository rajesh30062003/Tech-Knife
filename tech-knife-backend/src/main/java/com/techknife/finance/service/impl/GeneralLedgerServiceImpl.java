package com.techknife.finance.service.impl;

import com.techknife.finance.dto.GeneralLedgerDTO;
import com.techknife.finance.entity.GeneralLedger;
import com.techknife.finance.repository.GeneralLedgerRepository;
import com.techknife.finance.service.GeneralLedgerService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class GeneralLedgerServiceImpl implements GeneralLedgerService {

    private final GeneralLedgerRepository generalLedgerRepository;

    @Override
    public List<GeneralLedgerDTO> getAllLedgerEntries() {
        return generalLedgerRepository.findAll().stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<GeneralLedgerDTO> getLedgerEntriesByAccount(String accountId) {
        return generalLedgerRepository.findByAccountId(accountId).stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<GeneralLedgerDTO> getLedgerEntriesByAccountAndDateRange(String accountId, LocalDate startDate, LocalDate endDate) {
        return generalLedgerRepository.findByAccountIdAndTransactionDateBetween(accountId, startDate, endDate).stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<GeneralLedgerDTO> getLedgerEntriesByFinancialYear(String financialYearId) {
        return generalLedgerRepository.findByFinancialYearId(financialYearId).stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    private GeneralLedgerDTO mapToDTO(GeneralLedger gl) {
        return GeneralLedgerDTO.builder()
                .id(gl.getId())
                .accountId(gl.getAccountId())
                .accountCode(gl.getAccountCode())
                .accountName(gl.getAccountName())
                .journalEntryId(gl.getJournalEntryId())
                .journalNumber(gl.getJournalNumber())
                .referenceNumber(gl.getReferenceNumber())
                .transactionDate(gl.getTransactionDate())
                .financialYearId(gl.getFinancialYearId())
                .costCenterId(gl.getCostCenterId())
                .debitAmount(gl.getDebitAmount())
                .creditAmount(gl.getCreditAmount())
                .openingBalance(gl.getOpeningBalance())
                .closingBalance(gl.getClosingBalance())
                .narration(gl.getNarration())
                .createdAt(gl.getCreatedAt())
                .updatedAt(gl.getUpdatedAt())
                .createdBy(gl.getCreatedBy())
                .updatedBy(gl.getUpdatedBy())
                .build();
    }
}
