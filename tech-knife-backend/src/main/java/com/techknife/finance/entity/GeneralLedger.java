package com.techknife.finance.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.mapping.Document;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "fin_general_ledgers")
public class GeneralLedger {

    @Id
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

    @Builder.Default
    private BigDecimal debitAmount = BigDecimal.ZERO;

    @Builder.Default
    private BigDecimal creditAmount = BigDecimal.ZERO;

    @Builder.Default
    private BigDecimal openingBalance = BigDecimal.ZERO;

    @Builder.Default
    private BigDecimal closingBalance = BigDecimal.ZERO;

    private String narration;

    @CreatedDate
    private Instant createdAt;

    @LastModifiedDate
    private Instant updatedAt;

    @CreatedBy
    private String createdBy;

    @LastModifiedBy
    private String updatedBy;
}
