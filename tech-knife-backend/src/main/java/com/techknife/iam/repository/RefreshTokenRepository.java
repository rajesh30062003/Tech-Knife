package com.techknife.iam.repository;

import com.techknife.iam.entity.RefreshToken;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

/**
 * Spring Data MongoDB repository for JWT refresh token persistence and active session lifecycle tracking.
 */
@Repository
public interface RefreshTokenRepository extends MongoRepository<RefreshToken, String> {

    Optional<RefreshToken> findByToken(String token);

    @Query("{ 'token': ?0, 'revoked': false, 'expiry': { '$gt': ?1 } }")
    Optional<RefreshToken> findValidToken(String token, Instant now);

    List<RefreshToken> findByUserId(String userId);

    @Query("{ 'userId': ?0 }")
    List<RefreshToken> findByUser(String userId);

    long deleteByExpiryBefore(Instant now);

    @Query(value = "{ 'expiry': { '$lt': ?0 } }", delete = true)
    long deleteExpiredTokens(Instant now);
}
