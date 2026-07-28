package com.techknife.backend.repository;

import com.techknife.backend.entity.RefreshToken;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository("backendRefreshTokenRepository")
public interface RefreshTokenRepository extends MongoRepository<RefreshToken, String> {

    Optional<RefreshToken> findByToken(String token);
    void deleteByUserId(String userId);
}
