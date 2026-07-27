package com.techknife.organization.repository;

import com.techknife.organization.entity.Department;
import com.techknife.organization.entity.OrganizationStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface DepartmentRepository extends MongoRepository<Department, String> {
    Optional<Department> findByCode(String code);
    boolean existsByCode(String code);
    List<Department> findByCompanyId(String companyId);
    List<Department> findByBranchId(String branchId);
    Page<Department> findByCompanyId(String companyId, Pageable pageable);
    Page<Department> findByStatus(OrganizationStatus status, Pageable pageable);
}
