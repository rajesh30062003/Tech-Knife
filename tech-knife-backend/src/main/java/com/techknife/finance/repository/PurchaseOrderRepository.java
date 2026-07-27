package com.techknife.finance.repository;

import com.techknife.finance.entity.PurchaseOrder;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PurchaseOrderRepository extends MongoRepository<PurchaseOrder, String> {

    Optional<PurchaseOrder> findByPoNumber(String poNumber);

    List<PurchaseOrder> findByVendorId(String vendorId);

    List<PurchaseOrder> findByStatus(String status);

    boolean existsByPoNumber(String poNumber);
}
