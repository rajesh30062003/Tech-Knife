package com.techknife.finance.repository;

import com.techknife.finance.entity.GeneralLedger;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface GeneralLedgerRepository extends MongoRepository<GeneralLedger, String> {

    List<GeneralLedger> findByAccountId(String accountId);

    List<GeneralLedger> findByAccountIdAndTransactionDateBetween(String accountId, LocalDate startDate, LocalDate endDate);

    List<GeneralLedger> findByFinancialYearId(String financialYearId);

    List<GeneralLedger> findByJournalEntryId(String journalEntryId);

    List<GeneralLedger> findByAccountCodeContainingIgnoreCaseOrAccountNameContainingIgnoreCaseOrNarrationContainingIgnoreCase(
            String code, String name, String narration);
}
