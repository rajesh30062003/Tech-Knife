package com.techknife.procurement.service;

import com.techknife.procurement.dto.PurchaseOrderReceiptDTO;

import java.util.List;

public interface PurchaseOrderReceiptService {
    List<PurchaseOrderReceiptDTO> getAllReceipts();
    PurchaseOrderReceiptDTO getReceiptById(String id);
    PurchaseOrderReceiptDTO createReceipt(PurchaseOrderReceiptDTO dto);
    List<PurchaseOrderReceiptDTO> getReceiptsByPurchaseOrder(String purchaseOrderId);
}
