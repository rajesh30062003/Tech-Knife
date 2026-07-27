package com.techknife.crm.repository;

import com.techknife.crm.entity.Quotation;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface QuotationRepository extends MongoRepository<Quotation, String> {
    Optional<Quotation> findByQuotationNumber(String quotationNumber);
    List<Quotation> findByCustomerId(String customerId);
    List<Quotation> findByOpportunityId(String opportunityId);
    List<Quotation> findByLeadId(String leadId);
    List<Quotation> findByApprovalStatus(String approvalStatus);
}
