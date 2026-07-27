package com.techknife.analytics.repository;

import com.techknife.analytics.entity.AnalyticsCache;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;

@Repository
public interface AnalyticsCacheRepository extends MongoRepository<AnalyticsCache, String> {
    List<AnalyticsCache> findByCacheGroup(String cacheGroup);
    void deleteByExpiresAtBefore(Instant now);
}
