package com.techknife.procurement.service;

import com.techknife.procurement.dto.PurchaseApprovalDTO;
import com.techknife.procurement.dto.PurchaseRequestDTO;

import java.util.List;

public interface PurchaseRequestService {
    List<PurchaseRequestDTO> getAllPurchaseRequests();
    PurchaseRequestDTO getPurchaseRequestById(String id);
    PurchaseRequestDTO createPurchaseRequest(PurchaseRequestDTO dto);
    PurchaseRequestDTO updatePurchaseRequest(String id, PurchaseRequestDTO dto);
    PurchaseApprovalDTO approvePurchaseRequest(String id, String approverId, String approverName, boolean approved, String comments);
    List<PurchaseApprovalDTO> getApprovalsByPurchaseRequest(String purchaseRequestId);
}
