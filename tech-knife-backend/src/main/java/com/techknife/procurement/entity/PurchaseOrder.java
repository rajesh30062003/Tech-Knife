package com.techknife.procurement.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "pro_purchase_orders")
public class PurchaseOrder {

    @Id
    private String id;

    @Indexed(unique = true)
    private String poNumber;

    private String purchaseRequestId;

    private String supplierId;

    private String supplierName;

    private LocalDate orderDate;

    private LocalDate expectedDeliveryDate;

    @Builder.Default
    private String status = "DRAFT"; // DRAFT, ISSUED, PARTIALLY_RECEIVED, RECEIVED, CANCELLED

    @Builder.Default
    private List<PurchaseOrderItem> items = new ArrayList<>();

    @Builder.Default
    private BigDecimal subtotal = BigDecimal.ZERO;

    @Builder.Default
    private BigDecimal taxAmount = BigDecimal.ZERO;

    @Builder.Default
    private BigDecimal totalAmount = BigDecimal.ZERO;

    private String shippingAddress;

    private String paymentTerms;

    @CreatedDate
    private Instant createdAt;

    @LastModifiedDate
    private Instant updatedAt;

    @CreatedBy
    private String createdBy;

    @LastModifiedBy
    private String updatedBy;
}
