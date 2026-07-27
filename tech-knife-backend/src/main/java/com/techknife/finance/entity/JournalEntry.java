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
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "fin_journal_entries")
public class JournalEntry {

    @Id
    private String id;

    @Indexed(unique = true)
    private String journalNumber;

    private String referenceNumber;

    private LocalDate entryDate;

    private String financialYearId;

    private String narration;

    @Builder.Default
    private List<JournalLine> lines = new ArrayList<>();

    @Builder.Default
    private BigDecimal totalDebit = BigDecimal.ZERO;

    @Builder.Default
    private BigDecimal totalCredit = BigDecimal.ZERO;

    @Builder.Default
    private String status = "DRAFT"; // DRAFT, APPROVED, POSTED, REVERSED

    private String approvedBy;

    private Instant approvedAt;

    private String postedBy;

    private Instant postedAt;

    private String reversedJournalId;

    @CreatedDate
    private Instant createdAt;

    @LastModifiedDate
    private Instant updatedAt;

    @CreatedBy
    private String createdBy;

    @LastModifiedBy
    private String updatedBy;
}
