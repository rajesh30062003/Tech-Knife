package com.techknife.asset.repository;

import com.techknife.asset.entity.SoftwareLicense;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface SoftwareLicenseRepository extends MongoRepository<SoftwareLicense, String> {
    Optional<SoftwareLicense> findByLicenseKey(String licenseKey);
    boolean existsByLicenseKey(String licenseKey);
}
