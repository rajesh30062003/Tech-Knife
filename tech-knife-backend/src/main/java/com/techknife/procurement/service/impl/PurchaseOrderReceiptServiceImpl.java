package com.techknife.procurement.service.impl;

import com.techknife.procurement.dto.PurchaseOrderReceiptDTO;
import com.techknife.procurement.dto.ReceiptItemDTO;
import com.techknife.procurement.entity.PurchaseOrder;
import com.techknife.procurement.entity.PurchaseOrderReceipt;
import com.techknife.procurement.entity.ReceiptItem;
import com.techknife.procurement.repository.PurchaseOrderReceiptRepository;
import com.techknife.procurement.repository.PurchaseOrderRepository;
import com.techknife.procurement.service.PurchaseOrderReceiptService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PurchaseOrderReceiptServiceImpl implements PurchaseOrderReceiptService {

    private final PurchaseOrderReceiptRepository receiptRepository;
    private final PurchaseOrderRepository purchaseOrderRepository;

    @Override
    public List<PurchaseOrderReceiptDTO> getAllReceipts() {
        return receiptRepository.findAll().stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public PurchaseOrderReceiptDTO getReceiptById(String id) {
        PurchaseOrderReceipt receipt = receiptRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Goods receipt not found with id: " + id));
        return mapToDTO(receipt);
    }

    @Override
    public PurchaseOrderReceiptDTO createReceipt(PurchaseOrderReceiptDTO dto) {
        if (receiptRepository.existsByReceiptNumber(dto.getReceiptNumber())) {
            throw new IllegalArgumentException("Goods receipt already exists with receipt number: " + dto.getReceiptNumber());
        }

        PurchaseOrder po = null;
        if (dto.getPurchaseOrderId() != null) {
            po = purchaseOrderRepository.findById(dto.getPurchaseOrderId()).orElse(null);
        }

        List<ReceiptItem> items = new ArrayList<>();
        if (dto.getItems() != null) {
            for (ReceiptItemDTO itemDto : dto.getItems()) {
                items.add(ReceiptItem.builder()
                        .itemId(itemDto.getItemId())
                        .itemCode(itemDto.getItemCode())
                        .itemName(itemDto.getItemName())
                        .orderedQuantity(itemDto.getOrderedQuantity())
                        .receivedQuantity(itemDto.getReceivedQuantity())
                        .rejectedQuantity(itemDto.getRejectedQuantity() != null ? itemDto.getRejectedQuantity() : 0)
                        .remarks(itemDto.getRemarks())
                        .build());
            }
        }

        PurchaseOrderReceipt receipt = PurchaseOrderReceipt.builder()
                .receiptNumber(dto.getReceiptNumber())
                .purchaseOrderId(dto.getPurchaseOrderId())
                .poNumber(po != null ? po.getPoNumber() : dto.getPoNumber())
                .supplierId(dto.getSupplierId() != null ? dto.getSupplierId() : (po != null ? po.getSupplierId() : null))
                .supplierName(dto.getSupplierName() != null ? dto.getSupplierName() : (po != null ? po.getSupplierName() : null))
                .receivedDate(dto.getReceivedDate() != null ? dto.getReceivedDate() : LocalDate.now())
                .receivedById(dto.getReceivedById())
                .receivedByName(dto.getReceivedByName())
                .items(items)
                .deliveryNoteNumber(dto.getDeliveryNoteNumber())
                .invoiceReference(dto.getInvoiceReference())
                .remarks(dto.getRemarks())
                .status(dto.getStatus() != null ? dto.getStatus() : "RECEIVED")
                .build();

        PurchaseOrderReceipt saved = receiptRepository.save(receipt);

        if (po != null) {
            po.setStatus("RECEIVED");
            purchaseOrderRepository.save(po);
        }

        return mapToDTO(saved);
    }

    @Override
    public List<PurchaseOrderReceiptDTO> getReceiptsByPurchaseOrder(String purchaseOrderId) {
        return receiptRepository.findByPurchaseOrderId(purchaseOrderId).stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    private PurchaseOrderReceiptDTO mapToDTO(PurchaseOrderReceipt r) {
        List<ReceiptItemDTO> itemDtos = r.getItems() != null ? r.getItems().stream()
                .map(i -> ReceiptItemDTO.builder()
                        .itemId(i.getItemId())
                        .itemCode(i.getItemCode())
                        .itemName(i.getItemName())
                        .orderedQuantity(i.getOrderedQuantity())
                        .receivedQuantity(i.getReceivedQuantity())
                        .rejectedQuantity(i.getRejectedQuantity())
                        .remarks(i.getRemarks())
                        .build())
                .collect(Collectors.toList()) : new ArrayList<>();

        return PurchaseOrderReceiptDTO.builder()
                .id(r.getId())
                .receiptNumber(r.getReceiptNumber())
                .purchaseOrderId(r.getPurchaseOrderId())
                .poNumber(r.getPoNumber())
                .supplierId(r.getSupplierId())
                .supplierName(r.getSupplierName())
                .receivedDate(r.getReceivedDate())
                .receivedById(r.getReceivedById())
                .receivedByName(r.getReceivedByName())
                .items(itemDtos)
                .deliveryNoteNumber(r.getDeliveryNoteNumber())
                .invoiceReference(r.getInvoiceReference())
                .remarks(r.getRemarks())
                .status(r.getStatus())
                .createdAt(r.getCreatedAt())
                .updatedAt(r.getUpdatedAt())
                .createdBy(r.getCreatedBy())
                .updatedBy(r.getUpdatedBy())
                .build();
    }
}
