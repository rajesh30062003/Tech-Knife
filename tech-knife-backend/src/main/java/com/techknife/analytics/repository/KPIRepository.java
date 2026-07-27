package com.techknife.analytics.repository;

import com.techknife.analytics.entity.KPI;
import com.techknife.analytics.entity.KPICategory;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface KPIRepository extends MongoRepository<KPI, String> {
    Optional<KPI> findByCode(String code);
    List<KPI> findByCategory(KPICategory category);
    List<KPI> findByGroupId(String groupId);
    List<KPI> findByActiveTrue();
    boolean existsByCode(String code);
}
