package com.techknife.finance.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GeneralLedgerReportDTO {

    private String accountId;

    private String accountCode;

    private String accountName;

    private BigDecimal openingBalance;

    private BigDecimal totalDebit;

    private BigDecimal totalCredit;

    private BigDecimal closingBalance;

    private List<GeneralLedgerDTO> entries;
}
