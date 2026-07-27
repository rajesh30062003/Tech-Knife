package com.techknife.inventory.repository;

import com.techknife.inventory.entity.InventoryStock;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface InventoryStockRepository extends MongoRepository<InventoryStock, String> {
    Optional<InventoryStock> findByItemIdAndWarehouseId(String itemId, String warehouseId);
    List<InventoryStock> findByItemId(String itemId);
    List<InventoryStock> findByWarehouseId(String warehouseId);
}
