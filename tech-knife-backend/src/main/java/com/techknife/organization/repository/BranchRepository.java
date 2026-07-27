package com.techknife.organization.repository;

import com.techknife.organization.entity.Branch;
import com.techknife.organization.entity.OrganizationStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BranchRepository extends MongoRepository<Branch, String> {
    Optional<Branch> findByCode(String code);
    boolean existsByCode(String code);
    List<Branch> findByCompanyId(String companyId);
    Page<Branch> findByCompanyId(String companyId, Pageable pageable);
    Page<Branch> findByStatus(OrganizationStatus status, Pageable pageable);
}
