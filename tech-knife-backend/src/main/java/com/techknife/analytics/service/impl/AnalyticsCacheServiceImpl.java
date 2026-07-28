package com.techknife.analytics.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.techknife.analytics.entity.AnalyticsCache;
import com.techknife.analytics.repository.AnalyticsCacheRepository;
import com.techknife.analytics.service.AnalyticsCacheService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Slf4j
@Service
@RequiredArgsConstructor
public class AnalyticsCacheServiceImpl implements AnalyticsCacheService {

    private final AnalyticsCacheRepository cacheRepository;
    private final ObjectMapper objectMapper;

    @Override
    public void cacheData(String cacheKey, String cacheGroup, Object data, long ttlSeconds) {
        try {
            String json = objectMapper.writeValueAsString(data);
            AnalyticsCache cache = AnalyticsCache.builder()
                    .cacheKey(cacheKey)
                    .cacheGroup(cacheGroup)
                    .payloadJson(json)
                    .createdAt(Instant.now())
                    .expiresAt(Instant.now().plusSeconds(ttlSeconds))
                    .build();
            cacheRepository.save(cache);
        } catch (Exception e) {
            log.error("Failed to cache analytics data for key: {}", cacheKey, e);
        }
    }

    @Override
    public <T> T getCachedData(String cacheKey, Class<T> clazz) {
        try {
            java.util.Optional<AnalyticsCache> optionalCache = cacheRepository.findById(cacheKey)
                    .filter(cache -> cache.getExpiresAt() != null && cache.getExpiresAt().isAfter(Instant.now()));
            if (optionalCache.isPresent()) {
                return objectMapper.readValue(optionalCache.get().getPayloadJson(), clazz);
            }
            return null;
        } catch (Exception e) {
            log.error("Error retrieving cached analytics data for key: {}", cacheKey, e);
            return null;
        }
    }

    @Override
    public void invalidateGroup(String cacheGroup) {
        cacheRepository.findByCacheGroup(cacheGroup).forEach(cache -> cacheRepository.deleteById(cache.getCacheKey()));
    }

    @Override
    public void clearExpiredCache() {
        cacheRepository.deleteByExpiresAtBefore(Instant.now());
    }
}
