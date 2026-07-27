package com.techknife.iam.repository;

import com.techknife.iam.entity.LoginHistory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Spring Data MongoDB repository for user authentication audit history and device telemetry analytics.
 */
@Repository
public interface LoginHistoryRepository extends MongoRepository<LoginHistory, String> {

    List<LoginHistory> findByUserId(String userId);

    @Query("{ 'userId': ?0 }")
    List<LoginHistory> findByUser(String userId);

    Page<LoginHistory> findByUserIdOrderByLoginTimeDesc(String userId, Pageable pageable);

    @Query("{ 'userId': ?0 }")
    Page<LoginHistory> findRecentLogins(String userId, Pageable pageable);

    @Query("{ 'userId': ?0, 'status': 'FAILED' }")
    List<LoginHistory> findFailedLogins(String userId);
}
