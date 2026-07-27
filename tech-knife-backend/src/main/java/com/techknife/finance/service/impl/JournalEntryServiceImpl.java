package com.techknife.finance.service.impl;

import com.techknife.finance.dto.JournalEntryDTO;
import com.techknife.finance.dto.JournalLineDTO;
import com.techknife.finance.entity.ChartOfAccount;
import com.techknife.finance.entity.FinancialYear;
import com.techknife.finance.entity.GeneralLedger;
import com.techknife.finance.entity.JournalEntry;
import com.techknife.finance.entity.JournalLine;
import com.techknife.finance.repository.ChartOfAccountRepository;
import com.techknife.finance.repository.FinancialYearRepository;
import com.techknife.finance.repository.GeneralLedgerRepository;
import com.techknife.finance.repository.JournalEntryRepository;
import com.techknife.finance.service.JournalEntryService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class JournalEntryServiceImpl implements JournalEntryService {

    private final JournalEntryRepository journalEntryRepository;
    private final ChartOfAccountRepository chartOfAccountRepository;
    private final GeneralLedgerRepository generalLedgerRepository;
    private final FinancialYearRepository financialYearRepository;

    @Override
    public List<JournalEntryDTO> getAllJournalEntries() {
        return journalEntryRepository.findAll().stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<JournalEntryDTO> getJournalEntriesByFinancialYear(String financialYearId) {
        return journalEntryRepository.findByFinancialYearId(financialYearId).stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public JournalEntryDTO getJournalEntryById(String id) {
        JournalEntry entry = journalEntryRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Journal Entry not found with id: " + id));
        return mapToDTO(entry);
    }

    @Override
    public JournalEntryDTO createJournalEntry(JournalEntryDTO dto) {
        // Validate Financial Year Lock
        if (dto.getFinancialYearId() != null) {
            checkFinancialYearNotLocked(dto.getFinancialYearId());
        }

        // Validate lines and balance
        List<JournalLine> lines = mapLinesToEntity(dto.getLines());
        BigDecimal totalDebit = lines.stream()
                .map(l -> l.getDebitAmount() != null ? l.getDebitAmount() : BigDecimal.ZERO)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal totalCredit = lines.stream()
                .map(l -> l.getCreditAmount() != null ? l.getCreditAmount() : BigDecimal.ZERO)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        if (totalDebit.compareTo(totalCredit) != 0) {
            throw new IllegalArgumentException("Unbalanced Journal Entry! Total Debit (" + totalDebit + ") must equal Total Credit (" + totalCredit + ")");
        }

        String journalNum = dto.getJournalNumber() != null && !dto.getJournalNumber().isBlank()
                ? dto.getJournalNumber()
                : "JV-" + System.currentTimeMillis() + "-" + UUID.randomUUID().toString().substring(0, 4).toUpperCase();

        JournalEntry entry = JournalEntry.builder()
                .journalNumber(journalNum)
                .referenceNumber(dto.getReferenceNumber())
                .entryDate(dto.getEntryDate() != null ? dto.getEntryDate() : LocalDate.now())
                .financialYearId(dto.getFinancialYearId())
                .narration(dto.getNarration())
                .lines(lines)
                .totalDebit(totalDebit)
                .totalCredit(totalCredit)
                .status("DRAFT")
                .build();

        JournalEntry saved = journalEntryRepository.save(entry);
        return mapToDTO(saved);
    }

    @Override
    public JournalEntryDTO approveJournalEntry(String id, String approvedBy) {
        JournalEntry entry = journalEntryRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Journal Entry not found with id: " + id));

        entry.setStatus("APPROVED");
        entry.setApprovedBy(approvedBy != null ? approvedBy : "SYSTEM");
        entry.setApprovedAt(Instant.now());

        JournalEntry saved = journalEntryRepository.save(entry);
        return mapToDTO(saved);
    }

    @Override
    public JournalEntryDTO postJournalEntry(String id, String postedBy) {
        JournalEntry entry = journalEntryRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Journal Entry not found with id: " + id));

        if (entry.getFinancialYearId() != null) {
            checkFinancialYearNotLocked(entry.getFinancialYearId());
        }

        if ("POSTED".equalsIgnoreCase(entry.getStatus())) {
            throw new IllegalArgumentException("Journal Entry is already posted!");
        }

        entry.setStatus("POSTED");
        entry.setPostedBy(postedBy != null ? postedBy : "SYSTEM");
        entry.setPostedAt(Instant.now());

        // Automatic Ledger Posting
        for (JournalLine line : entry.getLines()) {
            BigDecimal debit = line.getDebitAmount() != null ? line.getDebitAmount() : BigDecimal.ZERO;
            BigDecimal credit = line.getCreditAmount() != null ? line.getCreditAmount() : BigDecimal.ZERO;

            ChartOfAccount acc = null;
            if (line.getAccountId() != null) {
                acc = chartOfAccountRepository.findById(line.getAccountId()).orElse(null);
            } else if (line.getAccountCode() != null) {
                acc = chartOfAccountRepository.findByAccountCode(line.getAccountCode()).orElse(null);
            }

            BigDecimal currentBal = acc != null ? acc.getCurrentBalance() : BigDecimal.ZERO;
            BigDecimal newBal = currentBal;

            if (acc != null) {
                String type = acc.getAccountType() != null ? acc.getAccountType().toUpperCase() : "ASSETS";
                if ("ASSETS".equals(type) || "EXPENSES".equals(type)) {
                    newBal = currentBal.add(debit).subtract(credit);
                } else {
                    newBal = currentBal.add(credit).subtract(debit);
                }
                acc.setCurrentBalance(newBal);
                chartOfAccountRepository.save(acc);
            }

            GeneralLedger gl = GeneralLedger.builder()
                    .accountId(acc != null ? acc.getId() : line.getAccountId())
                    .accountCode(line.getAccountCode())
                    .accountName(line.getAccountName())
                    .journalEntryId(entry.getId())
                    .journalNumber(entry.getJournalNumber())
                    .referenceNumber(entry.getReferenceNumber())
                    .transactionDate(entry.getEntryDate())
                    .financialYearId(entry.getFinancialYearId())
                    .costCenterId(line.getCostCenterId())
                    .debitAmount(debit)
                    .creditAmount(credit)
                    .openingBalance(currentBal)
                    .closingBalance(newBal)
                    .narration(line.getNarration() != null ? line.getNarration() : entry.getNarration())
                    .build();

            generalLedgerRepository.save(gl);
        }

        JournalEntry saved = journalEntryRepository.save(entry);
        return mapToDTO(saved);
    }

    @Override
    public JournalEntryDTO reverseJournalEntry(String id, String reversedBy) {
        JournalEntry original = journalEntryRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Journal Entry not found with id: " + id));

        if (!"POSTED".equalsIgnoreCase(original.getStatus())) {
            throw new IllegalArgumentException("Only posted journal entries can be reversed!");
        }

        if (original.getFinancialYearId() != null) {
            checkFinancialYearNotLocked(original.getFinancialYearId());
        }

        // Create reversed journal entry
        List<JournalLine> reversedLines = new ArrayList<>();
        for (JournalLine line : original.getLines()) {
            reversedLines.add(JournalLine.builder()
                    .accountId(line.getAccountId())
                    .accountCode(line.getAccountCode())
                    .accountName(line.getAccountName())
                    .costCenterId(line.getCostCenterId())
                    .debitAmount(line.getCreditAmount()) // Swap debit and credit
                    .creditAmount(line.getDebitAmount())
                    .narration("Reversal of " + original.getJournalNumber() + ": " + line.getNarration())
                    .build());
        }

        String revNum = "REV-" + original.getJournalNumber();

        JournalEntry reversalEntry = JournalEntry.builder()
                .journalNumber(revNum)
                .referenceNumber("REV-" + (original.getReferenceNumber() != null ? original.getReferenceNumber() : ""))
                .entryDate(LocalDate.now())
                .financialYearId(original.getFinancialYearId())
                .narration("Reversal entry for " + original.getJournalNumber())
                .lines(reversedLines)
                .totalDebit(original.getTotalCredit())
                .totalCredit(original.getTotalDebit())
                .status("DRAFT")
                .build();

        JournalEntry savedReversal = journalEntryRepository.save(reversalEntry);

        // Mark original as REVERSED
        original.setStatus("REVERSED");
        original.setReversedJournalId(savedReversal.getId());
        journalEntryRepository.save(original);

        // Auto post reversal
        return postJournalEntry(savedReversal.getId(), reversedBy);
    }

    private void checkFinancialYearNotLocked(String financialYearId) {
        FinancialYear fy = financialYearRepository.findById(financialYearId).orElse(null);
        if (fy != null && (Boolean.TRUE.equals(fy.getIsLocked()) || "LOCKED".equalsIgnoreCase(fy.getStatus()) || "CLOSED".equalsIgnoreCase(fy.getStatus()))) {
            throw new IllegalStateException("Financial Year " + fy.getYearCode() + " is LOCKED or CLOSED. Transactions are prohibited!");
        }
    }

    private List<JournalLine> mapLinesToEntity(List<JournalLineDTO> lineDTOs) {
        if (lineDTOs == null) return new ArrayList<>();
        return lineDTOs.stream().map(dto -> {
            ChartOfAccount acc = null;
            if (dto.getAccountId() != null) {
                acc = chartOfAccountRepository.findById(dto.getAccountId()).orElse(null);
            } else if (dto.getAccountCode() != null) {
                acc = chartOfAccountRepository.findByAccountCode(dto.getAccountCode()).orElse(null);
            }

            return JournalLine.builder()
                    .accountId(acc != null ? acc.getId() : dto.getAccountId())
                    .accountCode(acc != null ? acc.getAccountCode() : dto.getAccountCode())
                    .accountName(acc != null ? acc.getAccountName() : dto.getAccountName())
                    .costCenterId(dto.getCostCenterId())
                    .debitAmount(dto.getDebitAmount() != null ? dto.getDebitAmount() : BigDecimal.ZERO)
                    .creditAmount(dto.getCreditAmount() != null ? dto.getCreditAmount() : BigDecimal.ZERO)
                    .narration(dto.getNarration())
                    .build();
        }).collect(Collectors.toList());
    }

    private JournalEntryDTO mapToDTO(JournalEntry entry) {
        List<JournalLineDTO> lineDTOs = entry.getLines() != null
                ? entry.getLines().stream().map(l -> JournalLineDTO.builder()
                .accountId(l.getAccountId())
                .accountCode(l.getAccountCode())
                .accountName(l.getAccountName())
                .costCenterId(l.getCostCenterId())
                .debitAmount(l.getDebitAmount())
                .creditAmount(l.getCreditAmount())
                .narration(l.getNarration())
                .build()).collect(Collectors.toList())
                : new ArrayList<>();

        return JournalEntryDTO.builder()
                .id(entry.getId())
                .journalNumber(entry.getJournalNumber())
                .referenceNumber(entry.getReferenceNumber())
                .entryDate(entry.getEntryDate())
                .financialYearId(entry.getFinancialYearId())
                .narration(entry.getNarration())
                .lines(lineDTOs)
                .totalDebit(entry.getTotalDebit())
                .totalCredit(entry.getTotalCredit())
                .status(entry.getStatus())
                .approvedBy(entry.getApprovedBy())
                .approvedAt(entry.getApprovedAt())
                .postedBy(entry.getPostedBy())
                .postedAt(entry.getPostedAt())
                .reversedJournalId(entry.getReversedJournalId())
                .createdAt(entry.getCreatedAt())
                .updatedAt(entry.getUpdatedAt())
                .createdBy(entry.getCreatedBy())
                .updatedBy(entry.getUpdatedBy())
                .build();
    }
}
