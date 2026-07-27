package com.techknife.iam.repository;

import com.techknife.iam.entity.EmailVerificationToken;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

/**
 * Spring Data MongoDB repository for registration and email update verification tokens.
 */
@Repository
public interface EmailVerificationTokenRepository extends MongoRepository<EmailVerificationToken, String> {

    Optional<EmailVerificationToken> findByToken(String token);

    @Query("{ 'token': ?0, 'verified': false, 'expiry': { '$gt': ?1 } }")
    Optional<EmailVerificationToken> findValidToken(String token, Instant now);

    List<EmailVerificationToken> findByUserId(String userId);
}
