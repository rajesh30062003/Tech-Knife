package com.techknife.organization.repository;

import com.techknife.organization.entity.Designation;
import com.techknife.organization.entity.OrganizationStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface DesignationRepository extends MongoRepository<Designation, String> {
    Optional<Designation> findByCode(String code);
    boolean existsByCode(String code);
    List<Designation> findByDepartmentId(String departmentId);
    List<Designation> findByCompanyId(String companyId);
    Page<Designation> findByStatus(OrganizationStatus status, Pageable pageable);
}
