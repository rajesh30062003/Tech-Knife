package com.techknife.finance.service;

import com.techknife.finance.dto.JournalEntryDTO;

import java.util.List;

public interface JournalEntryService {

    List<JournalEntryDTO> getAllJournalEntries();

    List<JournalEntryDTO> getJournalEntriesByFinancialYear(String financialYearId);

    JournalEntryDTO getJournalEntryById(String id);

    JournalEntryDTO createJournalEntry(JournalEntryDTO dto);

    JournalEntryDTO approveJournalEntry(String id, String approvedBy);

    JournalEntryDTO postJournalEntry(String id, String postedBy);

    JournalEntryDTO reverseJournalEntry(String id, String reversedBy);
}
