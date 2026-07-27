package com.techknife.inventory.repository;

import com.techknife.inventory.entity.StockMovement;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface StockMovementRepository extends MongoRepository<StockMovement, String> {
    List<StockMovement> findByItemId(String itemId);
    List<StockMovement> findBySourceWarehouseIdOrTargetWarehouseId(String sourceWarehouseId, String targetWarehouseId);
}
