package com.techknife.procurement.service.impl;

import com.techknife.procurement.dto.PurchaseOrderDTO;
import com.techknife.procurement.dto.PurchaseOrderItemDTO;
import com.techknife.procurement.entity.PurchaseOrder;
import com.techknife.procurement.entity.PurchaseOrderItem;
import com.techknife.procurement.repository.PurchaseOrderRepository;
import com.techknife.procurement.service.PurchaseOrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
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
    public PurchaseOrderDTO getPurchaseOrderById(String id) {
        PurchaseOrder po = purchaseOrderRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Purchase order not found with id: " + id));
        return mapToDTO(po);
    }

    @Override
    public PurchaseOrderDTO createPurchaseOrder(PurchaseOrderDTO dto) {
        if (purchaseOrderRepository.existsByPoNumber(dto.getPoNumber())) {
            throw new IllegalArgumentException("Purchase order already exists with PO Number: " + dto.getPoNumber());
        }

        List<PurchaseOrderItem> items = new ArrayList<>();
        BigDecimal subtotal = BigDecimal.ZERO;
        BigDecimal taxTotal = BigDecimal.ZERO;

        if (dto.getItems() != null) {
            for (PurchaseOrderItemDTO itemDto : dto.getItems()) {
                BigDecimal unitPrice = itemDto.getUnitPrice() != null ? itemDto.getUnitPrice() : BigDecimal.ZERO;
                int qty = itemDto.getQuantity() != null ? itemDto.getQuantity() : 1;
                BigDecimal itemTotal = unitPrice.multiply(BigDecimal.valueOf(qty));
                BigDecimal taxRate = itemDto.getTaxRate() != null ? itemDto.getTaxRate() : BigDecimal.ZERO;
                BigDecimal itemTax = itemTotal.multiply(taxRate).divide(BigDecimal.valueOf(100), 2, BigDecimal.ROUND_HALF_UP);
                BigDecimal grandTotal = itemTotal.add(itemTax);

                subtotal = subtotal.add(itemTotal);
                taxTotal = taxTotal.add(itemTax);

                items.add(PurchaseOrderItem.builder()
                        .itemId(itemDto.getItemId())
                        .itemCode(itemDto.getItemCode())
                        .itemName(itemDto.getItemName())
                        .quantity(qty)
                        .unitPrice(unitPrice)
                        .taxRate(taxRate)
                        .taxAmount(itemTax)
                        .totalPrice(grandTotal)
                        .build());
            }
        }

        BigDecimal totalAmount = subtotal.add(taxTotal);

        PurchaseOrder po = PurchaseOrder.builder()
                .poNumber(dto.getPoNumber())
                .purchaseRequestId(dto.getPurchaseRequestId())
                .supplierId(dto.getSupplierId())
                .supplierName(dto.getSupplierName())
                .orderDate(dto.getOrderDate() != null ? dto.getOrderDate() : LocalDate.now())
                .expectedDeliveryDate(dto.getExpectedDeliveryDate())
                .status(dto.getStatus() != null ? dto.getStatus() : "ISSUED")
                .items(items)
                .subtotal(subtotal)
                .taxAmount(taxTotal)
                .totalAmount(totalAmount)
                .shippingAddress(dto.getShippingAddress())
                .paymentTerms(dto.getPaymentTerms())
                .build();

        PurchaseOrder saved = purchaseOrderRepository.save(po);
        return mapToDTO(saved);
    }

    @Override
    public PurchaseOrderDTO updatePurchaseOrder(String id, PurchaseOrderDTO dto) {
        PurchaseOrder po = purchaseOrderRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Purchase order not found with id: " + id));

        if (dto.getSupplierId() != null) po.setSupplierId(dto.getSupplierId());
        if (dto.getSupplierName() != null) po.setSupplierName(dto.getSupplierName());
        if (dto.getExpectedDeliveryDate() != null) po.setExpectedDeliveryDate(dto.getExpectedDeliveryDate());
        if (dto.getStatus() != null) po.setStatus(dto.getStatus());
        if (dto.getShippingAddress() != null) po.setShippingAddress(dto.getShippingAddress());
        if (dto.getPaymentTerms() != null) po.setPaymentTerms(dto.getPaymentTerms());

        if (dto.getItems() != null) {
            List<PurchaseOrderItem> items = new ArrayList<>();
            BigDecimal subtotal = BigDecimal.ZERO;
            BigDecimal taxTotal = BigDecimal.ZERO;

            for (PurchaseOrderItemDTO itemDto : dto.getItems()) {
                BigDecimal unitPrice = itemDto.getUnitPrice() != null ? itemDto.getUnitPrice() : BigDecimal.ZERO;
                int qty = itemDto.getQuantity() != null ? itemDto.getQuantity() : 1;
                BigDecimal itemTotal = unitPrice.multiply(BigDecimal.valueOf(qty));
                BigDecimal taxRate = itemDto.getTaxRate() != null ? itemDto.getTaxRate() : BigDecimal.ZERO;
                BigDecimal itemTax = itemTotal.multiply(taxRate).divide(BigDecimal.valueOf(100), 2, BigDecimal.ROUND_HALF_UP);
                BigDecimal grandTotal = itemTotal.add(itemTax);

                subtotal = subtotal.add(itemTotal);
                taxTotal = taxTotal.add(itemTax);

                items.add(PurchaseOrderItem.builder()
                        .itemId(itemDto.getItemId())
                        .itemCode(itemDto.getItemCode())
                        .itemName(itemDto.getItemName())
                        .quantity(qty)
                        .unitPrice(unitPrice)
                        .taxRate(taxRate)
                        .taxAmount(itemTax)
                        .totalPrice(grandTotal)
                        .build());
            }

            po.setItems(items);
            po.setSubtotal(subtotal);
            po.setTaxAmount(taxTotal);
            po.setTotalAmount(subtotal.add(taxTotal));
        }

        PurchaseOrder saved = purchaseOrderRepository.save(po);
        return mapToDTO(saved);
    }

    @Override
    public void deletePurchaseOrder(String id) {
        if (!purchaseOrderRepository.existsById(id)) {
            throw new IllegalArgumentException("Purchase order not found with id: " + id);
        }
        purchaseOrderRepository.deleteById(id);
    }

    private PurchaseOrderDTO mapToDTO(PurchaseOrder po) {
        List<PurchaseOrderItemDTO> itemDtos = po.getItems() != null ? po.getItems().stream()
                .map(i -> PurchaseOrderItemDTO.builder()
                        .itemId(i.getItemId())
                        .itemCode(i.getItemCode())
                        .itemName(i.getItemName())
                        .quantity(i.getQuantity())
                        .unitPrice(i.getUnitPrice())
                        .taxRate(i.getTaxRate())
                        .taxAmount(i.getTaxAmount())
                        .totalPrice(i.getTotalPrice())
                        .build())
                .collect(Collectors.toList()) : new ArrayList<>();

        return PurchaseOrderDTO.builder()
                .id(po.getId())
                .poNumber(po.getPoNumber())
                .purchaseRequestId(po.getPurchaseRequestId())
                .supplierId(po.getSupplierId())
                .supplierName(po.getSupplierName())
                .orderDate(po.getOrderDate())
                .expectedDeliveryDate(po.getExpectedDeliveryDate())
                .status(po.getStatus())
                .items(itemDtos)
                .subtotal(po.getSubtotal())
                .taxAmount(po.getTaxAmount())
                .totalAmount(po.getTotalAmount())
                .shippingAddress(po.getShippingAddress())
                .paymentTerms(po.getPaymentTerms())
                .createdAt(po.getCreatedAt())
                .updatedAt(po.getUpdatedAt())
                .createdBy(po.getCreatedBy())
                .updatedBy(po.getUpdatedBy())
                .build();
    }
}
