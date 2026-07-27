package com.techknife.organization.repository;

import com.techknife.organization.entity.OrganizationStatus;
import com.techknife.organization.entity.Team;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TeamRepository extends MongoRepository<Team, String> {
    Optional<Team> findByCode(String code);
    boolean existsByCode(String code);
    List<Team> findByDepartmentId(String departmentId);
    List<Team> findByBranchId(String branchId);
    Page<Team> findByStatus(OrganizationStatus status, Pageable pageable);
}
