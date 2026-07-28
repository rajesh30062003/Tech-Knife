package com.techknife.procurement.repository;

import com.techknife.procurement.entity.PurchaseOrder;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository("procurementPurchaseOrderRepository")
public interface PurchaseOrderRepository extends MongoRepository<PurchaseOrder, String> {

    Optional<PurchaseOrder> findByPoNumber(String poNumber);
    boolean existsByPoNumber(String poNumber);
    List<PurchaseOrder> findBySupplierId(String supplierId);
    List<PurchaseOrder> findByStatus(String status);
}
