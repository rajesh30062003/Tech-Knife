package com.techknife.finance.repository;

import com.techknife.finance.entity.ChartOfAccount;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ChartOfAccountRepository extends MongoRepository<ChartOfAccount, String> {

    Optional<ChartOfAccount> findByAccountCode(String accountCode);

    List<ChartOfAccount> findByAccountType(String accountType);

    List<ChartOfAccount> findByAccountGroupId(String accountGroupId);

    List<ChartOfAccount> findByParentAccountId(String parentAccountId);

    boolean existsByAccountCode(String accountCode);

    List<ChartOfAccount> findByAccountNameContainingIgnoreCaseOrAccountCodeContainingIgnoreCase(String nameKeyword, String codeKeyword);
}
