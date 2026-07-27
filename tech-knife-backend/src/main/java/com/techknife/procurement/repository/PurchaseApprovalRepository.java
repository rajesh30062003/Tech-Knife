package com.techknife.procurement.repository;

import com.techknife.procurement.entity.PurchaseApproval;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PurchaseApprovalRepository extends MongoRepository<PurchaseApproval, String> {
    List<PurchaseApproval> findByPurchaseRequestId(String purchaseRequestId);
    List<PurchaseApproval> findByApproverId(String approverId);
}
