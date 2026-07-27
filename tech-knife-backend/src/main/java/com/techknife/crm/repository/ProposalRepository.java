package com.techknife.crm.repository;

import com.techknife.crm.entity.Proposal;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProposalRepository extends MongoRepository<Proposal, String> {
    Optional<Proposal> findByProposalNumber(String proposalNumber);
    List<Proposal> findByOpportunityId(String opportunityId);
    List<Proposal> findByCustomerId(String customerId);
    List<Proposal> findByLeadId(String leadId);
    List<Proposal> findByStatus(String status);
}
