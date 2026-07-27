package com.techknife.iam.repository;

import com.techknife.iam.entity.OtpVerification;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.Optional;

/**
 * Spring Data MongoDB repository for multi-factor OTP challenges and verification lifecycle.
 */
@Repository
public interface OtpVerificationRepository extends MongoRepository<OtpVerification, String> {

    Optional<OtpVerification> findFirstByUserIdAndPurposeOrderByCreatedAtDesc(String userId, String purpose);

    @Query(value = "{ 'userId': ?0, 'purpose': ?1 }", sort = "{ 'createdAt': -1 }")
    Optional<OtpVerification> findLatestOtp(String userId, String purpose);

    @Query("{ 'userId': ?0, 'otp': ?1, 'purpose': ?2, 'verified': false, 'expiry': { '$gt': ?3 } }")
    Optional<OtpVerification> findValidOtp(String userId, String otp, String purpose, Instant now);

    long deleteByExpiryBefore(Instant now);

    @Query(value = "{ 'expiry': { '$lt': ?0 } }", delete = true)
    long deleteExpiredOtp(Instant now);
}
