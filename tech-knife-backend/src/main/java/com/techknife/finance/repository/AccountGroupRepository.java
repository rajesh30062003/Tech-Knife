package com.techknife.finance.repository;

import com.techknife.finance.entity.AccountGroup;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AccountGroupRepository extends MongoRepository<AccountGroup, String> {

    Optional<AccountGroup> findByGroupCode(String groupCode);

    List<AccountGroup> findByAccountType(String accountType);

    List<AccountGroup> findByParentGroupId(String parentGroupId);

    boolean existsByGroupCode(String groupCode);
}
