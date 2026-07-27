package com.techknife.finance.service;

import com.techknife.finance.dto.PurchaseOrderDTO;

import java.util.List;

public interface PurchaseOrderService {

    List<PurchaseOrderDTO> getAllPurchaseOrders();

    List<PurchaseOrderDTO> getPurchaseOrdersByVendor(String vendorId);

    PurchaseOrderDTO getPurchaseOrderById(String id);

    PurchaseOrderDTO createPurchaseOrder(PurchaseOrderDTO dto);

    PurchaseOrderDTO updatePurchaseOrder(String id, PurchaseOrderDTO dto);

    PurchaseOrderDTO updatePurchaseOrderStatus(String id, String status);
}
