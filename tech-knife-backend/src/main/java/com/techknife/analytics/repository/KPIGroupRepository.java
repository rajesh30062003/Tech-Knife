package com.techknife.analytics.repository;

import com.techknife.analytics.entity.KPICategory;
import com.techknife.analytics.entity.KPIGroup;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface KPIGroupRepository extends MongoRepository<KPIGroup, String> {
    Optional<KPIGroup> findByCode(String code);
    List<KPIGroup> findByCategory(KPICategory category);
    boolean existsByCode(String code);
}
