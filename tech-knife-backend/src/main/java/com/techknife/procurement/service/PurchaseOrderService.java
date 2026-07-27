package com.techknife.procurement.service;

import com.techknife.procurement.dto.PurchaseOrderDTO;

import java.util.List;

public interface PurchaseOrderService {
    List<PurchaseOrderDTO> getAllPurchaseOrders();
    PurchaseOrderDTO getPurchaseOrderById(String id);
    PurchaseOrderDTO createPurchaseOrder(PurchaseOrderDTO dto);
    PurchaseOrderDTO updatePurchaseOrder(String id, PurchaseOrderDTO dto);
    void deletePurchaseOrder(String id);
}
