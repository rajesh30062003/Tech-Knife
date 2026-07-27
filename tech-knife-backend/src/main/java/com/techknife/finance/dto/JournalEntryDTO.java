package com.techknife.finance.dto;

import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class JournalEntryDTO {

    private String id;

    private String journalNumber;

    private String referenceNumber;

    private LocalDate entryDate;

    private String financialYearId;

    private String narration;

    @NotEmpty(message = "Journal entry must have at least one debit and credit line")
    private List<JournalLineDTO> lines;

    private BigDecimal totalDebit;

    private BigDecimal totalCredit;

    private String status;

    private String approvedBy;

    private Instant approvedAt;

    private String postedBy;

    private Instant postedAt;

    private String reversedJournalId;

    private Instant createdAt;

    private Instant updatedAt;

    private String createdBy;

    private String updatedBy;
}
