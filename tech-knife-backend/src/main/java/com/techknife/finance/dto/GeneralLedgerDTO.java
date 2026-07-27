package com.techknife.finance.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GeneralLedgerDTO {

    private String id;

    private String accountId;

    private String accountCode;

    private String accountName;

    private String journalEntryId;

    private String journalNumber;

    private String referenceNumber;

    private LocalDate transactionDate;

    private String financialYearId;

    private String costCenterId;

    private BigDecimal debitAmount;

    private BigDecimal creditAmount;

    private BigDecimal openingBalance;

    private BigDecimal closingBalance;

    private String narration;

    private Instant createdAt;

    private Instant updatedAt;

    private String createdBy;

    private String updatedBy;
}
