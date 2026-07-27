package com.techknife.crm.repository;

import com.techknife.crm.entity.Contract;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ContractRepository extends MongoRepository<Contract, String> {
    Optional<Contract> findByContractNumber(String contractNumber);
    List<Contract> findByCustomerId(String customerId);
    List<Contract> findByOpportunityId(String opportunityId);
    List<Contract> findByStatus(String status);
}
