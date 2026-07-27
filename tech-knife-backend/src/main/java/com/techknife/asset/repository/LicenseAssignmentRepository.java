package com.techknife.asset.repository;

import com.techknife.asset.entity.LicenseAssignment;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface LicenseAssignmentRepository extends MongoRepository<LicenseAssignment, String> {
    List<LicenseAssignment> findByLicenseId(String licenseId);
    List<LicenseAssignment> findByEmployeeId(String employeeId);
    Optional<LicenseAssignment> findByLicenseIdAndEmployeeIdAndStatus(String licenseId, String employeeId, String status);
}
