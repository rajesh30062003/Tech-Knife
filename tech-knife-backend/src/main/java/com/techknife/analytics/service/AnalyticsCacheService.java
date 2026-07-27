package com.techknife.analytics.service;

public interface AnalyticsCacheService {
    void cacheData(String cacheKey, String cacheGroup, Object data, long ttlSeconds);
    <T> T getCachedData(String cacheKey, Class<T> clazz);
    void invalidateGroup(String cacheGroup);
    void clearExpiredCache();
}
