package com.techknife.crm.repository;

import com.techknife.crm.entity.Opportunity;
import com.techknife.crm.entity.SalesStage;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface OpportunityRepository extends MongoRepository<Opportunity, String> {
    Optional<Opportunity> findByOpportunityNumber(String opportunityNumber);
    List<Opportunity> findByLeadId(String leadId);
    List<Opportunity> findByCustomerId(String customerId);
    List<Opportunity> findBySalesStage(SalesStage salesStage);
    List<Opportunity> findByStatus(String status);
    List<Opportunity> findByAssignedEmployeeId(String assignedEmployeeId);
}
