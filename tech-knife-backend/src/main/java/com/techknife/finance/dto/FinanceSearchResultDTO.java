package com.techknife.finance.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FinanceSearchResultDTO {

    private String query;

    private List<InvoiceDTO> invoices;

    private List<VendorDTO> vendors;

    private List<ExpenseDTO> expenses;

    private List<JournalEntryDTO> journals;

    private List<GeneralLedgerDTO> ledgers;

    private List<CostCenterDTO> costCenters;

    private List<FinancialYearDTO> financialYears;
}
