package com.techknife.finance.service.impl;

import com.techknife.finance.dto.PurchaseOrderDTO;
import com.techknife.finance.dto.PurchaseOrderItemDTO;
import com.techknife.finance.entity.PurchaseOrder;
import com.techknife.finance.entity.PurchaseOrderItem;
import com.techknife.finance.repository.PurchaseOrderRepository;
import com.techknife.finance.service.PurchaseOrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PurchaseOrderServiceImpl implements PurchaseOrderService {

    private final PurchaseOrderRepository purchaseOrderRepository;

    @Override
    public List<PurchaseOrderDTO> getAllPurchaseOrders() {
        return purchaseOrderRepository.findAll().stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<PurchaseOrderDTO> getPurchaseOrdersByVendor(String vendorId) {
        return purchaseOrderRepository.findByVendorId(vendorId).stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public PurchaseOrderDTO getPurchaseOrderById(String id) {
        PurchaseOrder po = purchaseOrderRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Purchase order not found with id: " + id));
        return mapToDTO(po);
    }

    @Override
    public PurchaseOrderDTO createPurchaseOrder(PurchaseOrderDTO dto) {
        String poNum = dto.getPoNumber() != null && !dto.getPoNumber().isBlank()
                ? dto.getPoNumber()
                : "PO-" + System.currentTimeMillis() + "-" + UUID.randomUUID().toString().substring(0, 4).toUpperCase();

        List<PurchaseOrderItem> items = calculateItems(dto.getItems());
        BigDecimal subtotal = items.stream().map(PurchaseOrderItem::getTotalAmount).reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal taxTotal = items.stream().map(PurchaseOrderItem::getTaxAmount).reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal total = subtotal.add(taxTotal);

        PurchaseOrder po = PurchaseOrder.builder()
                .poNumber(poNum)
                .vendorId(dto.getVendorId())
                .vendorName(dto.getVendorName())
                .orderDate(dto.getOrderDate() != null ? dto.getOrderDate() : LocalDate.now())
                .expectedDeliveryDate(dto.getExpectedDeliveryDate())
                .financialYearId(dto.getFinancialYearId())
                .costCenterId(dto.getCostCenterId())
                .items(items)
                .subtotal(subtotal)
                .taxTotal(taxTotal)
                .totalAmount(total)
                .status(dto.getStatus() != null ? dto.getStatus() : "DRAFT")
                .notes(dto.getNotes())
                .build();

        PurchaseOrder saved = purchaseOrderRepository.save(po);
        return mapToDTO(saved);
    }

    @Override
    public PurchaseOrderDTO updatePurchaseOrder(String id, PurchaseOrderDTO dto) {
        PurchaseOrder po = purchaseOrderRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Purchase order not found with id: " + id));

        if (dto.getVendorId() != null) po.setVendorId(dto.getVendorId());
        if (dto.getVendorName() != null) po.setVendorName(dto.getVendorName());
        if (dto.getOrderDate() != null) po.setOrderDate(dto.getOrderDate());
        if (dto.getExpectedDeliveryDate() != null) po.setExpectedDeliveryDate(dto.getExpectedDeliveryDate());
        if (dto.getFinancialYearId() != null) po.setFinancialYearId(dto.getFinancialYearId());
        if (dto.getCostCenterId() != null) po.setCostCenterId(dto.getCostCenterId());
        if (dto.getNotes() != null) po.setNotes(dto.getNotes());

        if (dto.getItems() != null) {
            List<PurchaseOrderItem> items = calculateItems(dto.getItems());
            BigDecimal subtotal = items.stream().map(PurchaseOrderItem::getTotalAmount).reduce(BigDecimal.ZERO, BigDecimal::add);
            BigDecimal taxTotal = items.stream().map(PurchaseOrderItem::getTaxAmount).reduce(BigDecimal.ZERO, BigDecimal::add);
            BigDecimal total = subtotal.add(taxTotal);

            po.setItems(items);
            po.setSubtotal(subtotal);
            po.setTaxTotal(taxTotal);
            po.setTotalAmount(total);
        }

        PurchaseOrder saved = purchaseOrderRepository.save(po);
        return mapToDTO(saved);
    }

    @Override
    public PurchaseOrderDTO updatePurchaseOrderStatus(String id, String status) {
        PurchaseOrder po = purchaseOrderRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Purchase order not found with id: " + id));

        po.setStatus(status.toUpperCase());
        PurchaseOrder saved = purchaseOrderRepository.save(po);
        return mapToDTO(saved);
    }

    private List<PurchaseOrderItem> calculateItems(List<PurchaseOrderItemDTO> dtos) {
        if (dtos == null) return new ArrayList<>();
        return dtos.stream().map(dto -> {
            int qty = dto.getQuantity() != null ? dto.getQuantity() : 1;
            BigDecimal price = dto.getUnitPrice() != null ? dto.getUnitPrice() : BigDecimal.ZERO;
            BigDecimal taxRate = dto.getTaxRate() != null ? dto.getTaxRate() : BigDecimal.ZERO;

            BigDecimal sub = price.multiply(BigDecimal.valueOf(qty));
            BigDecimal tax = sub.multiply(taxRate).divide(BigDecimal.valueOf(100));

            return PurchaseOrderItem.builder()
                    .itemName(dto.getItemName())
                    .description(dto.getDescription())
                    .quantity(qty)
                    .unitPrice(price)
                    .taxRate(taxRate)
                    .taxAmount(tax)
                    .totalAmount(sub)
                    .build();
        }).collect(Collectors.toList());
    }

    private PurchaseOrderDTO mapToDTO(PurchaseOrder po) {
        List<PurchaseOrderItemDTO> itemDTOs = po.getItems() != null
                ? po.getItems().stream().map(i -> PurchaseOrderItemDTO.builder()
                .itemName(i.getItemName())
                .description(i.getDescription())
                .quantity(i.getQuantity())
                .unitPrice(i.getUnitPrice())
                .taxRate(i.getTaxRate())
                .taxAmount(i.getTaxAmount())
                .totalAmount(i.getTotalAmount())
                .build()).collect(Collectors.toList())
                : new ArrayList<>();

        return PurchaseOrderDTO.builder()
                .id(po.getId())
                .poNumber(po.getPoNumber())
                .vendorId(po.getVendorId())
                .vendorName(po.getVendorName())
                .orderDate(po.getOrderDate())
                .expectedDeliveryDate(po.getExpectedDeliveryDate())
                .financialYearId(po.getFinancialYearId())
                .costCenterId(po.getCostCenterId())
                .items(itemDTOs)
                .subtotal(po.getSubtotal())
                .taxTotal(po.getTaxTotal())
                .totalAmount(po.getTotalAmount())
                .status(po.getStatus())
                .notes(po.getNotes())
                .createdAt(po.getCreatedAt())
                .updatedAt(po.getUpdatedAt())
                .createdBy(po.getCreatedBy())
                .updatedBy(po.getUpdatedBy())
                .build();
    }
}
