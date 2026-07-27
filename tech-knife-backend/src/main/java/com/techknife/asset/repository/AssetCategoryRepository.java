package com.techknife.asset.repository;

import com.techknife.asset.entity.AssetCategory;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AssetCategoryRepository extends MongoRepository<AssetCategory, String> {
    Optional<AssetCategory> findByCode(String code);
    boolean existsByCode(String code);
}
