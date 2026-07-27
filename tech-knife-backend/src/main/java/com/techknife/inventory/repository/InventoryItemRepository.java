package com.techknife.inventory.repository;

import com.techknife.inventory.entity.InventoryItem;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface InventoryItemRepository extends MongoRepository<InventoryItem, String> {
    Optional<InventoryItem> findByItemCode(String itemCode);
    boolean existsByItemCode(String itemCode);
    List<InventoryItem> findByCategoryId(String categoryId);
    List<InventoryItem> findByStatus(String status);
}
