package com.techknife.analytics.repository;

import com.techknife.analytics.entity.SystemHealth;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface SystemHealthRepository extends MongoRepository<SystemHealth, String> {
    Optional<SystemHealth> findFirstByOrderByTimestampDesc();
}
