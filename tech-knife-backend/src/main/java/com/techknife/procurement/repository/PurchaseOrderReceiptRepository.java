package com.techknife.procurement.repository;

import com.techknife.procurement.entity.PurchaseOrderReceipt;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PurchaseOrderReceiptRepository extends MongoRepository<PurchaseOrderReceipt, String> {
    Optional<PurchaseOrderReceipt> findByReceiptNumber(String receiptNumber);
    boolean existsByReceiptNumber(String receiptNumber);
    List<PurchaseOrderReceipt> findByPurchaseOrderId(String purchaseOrderId);
}
