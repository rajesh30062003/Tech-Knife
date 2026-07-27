package com.techknife.crm.repository;

import com.techknife.crm.entity.Lead;
import com.techknife.crm.entity.LeadStatus;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface LeadRepository extends MongoRepository<Lead, String> {
    Optional<Lead> findByLeadNumber(String leadNumber);
    boolean existsByEmail(String email);
    boolean existsByPhone(String phone);
    boolean existsByCompanyName(String companyName);
    List<Lead> findByLeadStatus(LeadStatus leadStatus);
    List<Lead> findByAssignedEmployeeId(String assignedEmployeeId);
    List<Lead> findByCompanyNameContainingIgnoreCaseOrContactPersonContainingIgnoreCaseOrEmailContainingIgnoreCase(
            String companyName, String contactPerson, String email);
}
