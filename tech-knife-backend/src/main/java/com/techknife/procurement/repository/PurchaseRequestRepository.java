package com.techknife.procurement.repository;

import com.techknife.procurement.entity.PurchaseRequest;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PurchaseRequestRepository extends MongoRepository<PurchaseRequest, String> {
    Optional<PurchaseRequest> findByRequestNumber(String requestNumber);
    boolean existsByRequestNumber(String requestNumber);
    List<PurchaseRequest> findByRequestedById(String requestedById);
    List<PurchaseRequest> findByStatus(String status);
}
