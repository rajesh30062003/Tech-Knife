package com.techknife.intern.repository;

import com.techknife.intern.entity.InternCertificate;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface InternCertificateRepository extends MongoRepository<InternCertificate, String> {
    Optional<InternCertificate> findByInternId(String internId);
    Optional<InternCertificate> findByCertificateNumber(String certificateNumber);
    Optional<InternCertificate> findByVerificationCode(String verificationCode);
}
