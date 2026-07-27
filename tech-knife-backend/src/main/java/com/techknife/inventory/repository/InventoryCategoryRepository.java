package com.techknife.inventory.repository;

import com.techknife.inventory.entity.InventoryCategory;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface InventoryCategoryRepository extends MongoRepository<InventoryCategory, String> {
    Optional<InventoryCategory> findByCode(String code);
    boolean existsByCode(String code);
}
