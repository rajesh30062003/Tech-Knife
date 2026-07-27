package com.techknife.procurement.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PurchaseOrderDTO {

    private String id;

    @NotBlank(message = "PO Number is required")
    private String poNumber;

    private String purchaseRequestId;

    private String supplierId;

    private String supplierName;

    private LocalDate orderDate;

    private LocalDate expectedDeliveryDate;

    private String status;

    private List<PurchaseOrderItemDTO> items;

    private BigDecimal subtotal;

    private BigDecimal taxAmount;

    private BigDecimal totalAmount;

    private String shippingAddress;

    private String paymentTerms;

    private Instant createdAt;

    private Instant updatedAt;

    private String createdBy;

    private String updatedBy;
}
