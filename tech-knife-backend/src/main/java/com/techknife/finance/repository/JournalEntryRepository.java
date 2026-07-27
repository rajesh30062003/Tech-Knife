package com.techknife.finance.repository;

import com.techknife.finance.entity.JournalEntry;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface JournalEntryRepository extends MongoRepository<JournalEntry, String> {

    Optional<JournalEntry> findByJournalNumber(String journalNumber);

    List<JournalEntry> findByFinancialYearId(String financialYearId);

    List<JournalEntry> findByStatus(String status);

    boolean existsByJournalNumber(String journalNumber);

    List<JournalEntry> findByJournalNumberContainingIgnoreCaseOrReferenceNumberContainingIgnoreCaseOrNarrationContainingIgnoreCase(
            String num, String ref, String narration);

    List<JournalEntry> findByEntryDateBetween(LocalDate startDate, LocalDate endDate);
}
