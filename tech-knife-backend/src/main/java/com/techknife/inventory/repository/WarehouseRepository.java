package com.techknife.inventory.repository;

import com.techknife.inventory.entity.Warehouse;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface WarehouseRepository extends MongoRepository<Warehouse, String> {
    Optional<Warehouse> findByCode(String code);
    boolean existsByCode(String code);
}
