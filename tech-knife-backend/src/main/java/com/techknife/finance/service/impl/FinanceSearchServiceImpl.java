package com.techknife.finance.service.impl;

import com.techknife.finance.dto.*;
import com.techknife.finance.entity.*;
import com.techknife.finance.repository.*;
import com.techknife.finance.service.FinanceSearchService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class FinanceSearchServiceImpl implements FinanceSearchService {

    private final InvoiceRepository invoiceRepository;
    private final VendorRepository vendorRepository;
    private final ExpenseRepository expenseRepository;
    private final JournalEntryRepository journalEntryRepository;
    private final GeneralLedgerRepository generalLedgerRepository;
    private final CostCenterRepository costCenterRepository;
    private final FinancialYearRepository financialYearRepository;

    @Override
    public FinanceSearchResultDTO searchFinanceRecords(String query) {
        if (query == null || query.trim().isBlank()) {
            return FinanceSearchResultDTO.builder()
                    .query(query)
                    .invoices(List.of())
                    .vendors(List.of())
                    .expenses(List.of())
                    .journals(List.of())
                    .ledgers(List.of())
                    .costCenters(List.of())
                    .financialYears(List.of())
                    .build();
        }

        String q = query.trim();

        List<InvoiceDTO> invoices = invoiceRepository.findByInvoiceNumberContainingIgnoreCaseOrCustomerNameContainingIgnoreCase(q, q)
                .stream().map(i -> InvoiceDTO.builder()
                        .id(i.getId())
                        .invoiceNumber(i.getInvoiceNumber())
                        .customerId(i.getCustomerId())
                        .customerName(i.getCustomerName())
                        .totalAmount(i.getTotalAmount())
                        .status(i.getStatus())
                        .build()).collect(Collectors.toList());

        List<VendorDTO> vendors = vendorRepository.findByVendorNameContainingIgnoreCaseOrVendorCodeContainingIgnoreCaseOrGstNumberContainingIgnoreCase(q, q, q)
                .stream().map(v -> VendorDTO.builder()
                        .id(v.getId())
                        .vendorCode(v.getVendorCode())
                        .vendorName(v.getVendorName())
                        .gstNumber(v.getGstNumber())
                        .status(v.getStatus())
                        .build()).collect(Collectors.toList());

        List<ExpenseDTO> expenses = expenseRepository.findByExpenseNumberContainingIgnoreCaseOrTitleContainingIgnoreCaseOrVendorNameContainingIgnoreCase(q, q, q)
                .stream().map(e -> ExpenseDTO.builder()
                        .id(e.getId())
                        .expenseNumber(e.getExpenseNumber())
                        .title(e.getTitle())
                        .amount(e.getAmount())
                        .approvalStatus(e.getApprovalStatus())
                        .build()).collect(Collectors.toList());

        List<JournalEntryDTO> journals = journalEntryRepository.findByJournalNumberContainingIgnoreCaseOrReferenceNumberContainingIgnoreCaseOrNarrationContainingIgnoreCase(q, q, q)
                .stream().map(j -> JournalEntryDTO.builder()
                        .id(j.getId())
                        .journalNumber(j.getJournalNumber())
                        .narration(j.getNarration())
                        .status(j.getStatus())
                        .totalDebit(j.getTotalDebit())
                        .build()).collect(Collectors.toList());

        List<GeneralLedgerDTO> ledgers = generalLedgerRepository.findByAccountCodeContainingIgnoreCaseOrAccountNameContainingIgnoreCaseOrNarrationContainingIgnoreCase(q, q, q)
                .stream().map(gl -> GeneralLedgerDTO.builder()
                        .id(gl.getId())
                        .accountCode(gl.getAccountCode())
                        .accountName(gl.getAccountName())
                        .debitAmount(gl.getDebitAmount())
                        .creditAmount(gl.getCreditAmount())
                        .build()).collect(Collectors.toList());

        List<CostCenterDTO> costCenters = costCenterRepository.findByCenterCodeContainingIgnoreCaseOrCenterNameContainingIgnoreCase(q, q)
                .stream().map(cc -> CostCenterDTO.builder()
                        .id(cc.getId())
                        .centerCode(cc.getCenterCode())
                        .centerName(cc.getCenterName())
                        .type(cc.getType())
                        .status(cc.getStatus())
                        .build()).collect(Collectors.toList());

        List<FinancialYearDTO> financialYears = financialYearRepository.findByYearCodeContainingIgnoreCaseOrYearNameContainingIgnoreCase(q, q)
                .stream().map(fy -> FinancialYearDTO.builder()
                        .id(fy.getId())
                        .yearCode(fy.getYearCode())
                        .yearName(fy.getYearName())
                        .status(fy.getStatus())
                        .isLocked(fy.getIsLocked())
                        .build()).collect(Collectors.toList());

        return FinanceSearchResultDTO.builder()
                .query(q)
                .invoices(invoices)
                .vendors(vendors)
                .expenses(expenses)
                .journals(journals)
                .ledgers(ledgers)
                .costCenters(costCenters)
                .financialYears(financialYears)
                .build();
    }
}
