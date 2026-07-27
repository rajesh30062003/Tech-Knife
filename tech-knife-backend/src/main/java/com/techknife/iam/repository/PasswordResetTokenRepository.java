package com.techknife.iam.repository;

import com.techknife.iam.entity.PasswordResetToken;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

/**
 * Spring Data MongoDB repository for secure password reset tokens.
 */
@Repository
public interface PasswordResetTokenRepository extends MongoRepository<PasswordResetToken, String> {

    Optional<PasswordResetToken> findByToken(String token);

    @Query("{ 'token': ?0, 'used': false, 'expiry': { '$gt': ?1 } }")
    Optional<PasswordResetToken> findValidToken(String token, Instant now);

    List<PasswordResetToken> findByUserId(String userId);
}
