package com.techknife.organization.repository;

import com.techknife.organization.entity.Company;
import com.techknife.organization.entity.OrganizationStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CompanyRepository extends MongoRepository<Company, String> {
    Optional<Company> findByCode(String code);
    boolean existsByCode(String code);
    Page<Company> findByStatus(OrganizationStatus status, Pageable pageable);
    Page<Company> findByNameContainingIgnoreCaseOrCodeContainingIgnoreCase(String name, String code, Pageable pageable);
}
