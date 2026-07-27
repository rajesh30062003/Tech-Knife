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

import java.time.Instant;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "pro_goods_receipts")
public class PurchaseOrderReceipt {

    @Id
    private String id;

    @Indexed(unique = true)
    private String receiptNumber;

    private String purchaseOrderId;

    private String poNumber;

    private String supplierId;

    private String supplierName;

    private LocalDate receivedDate;

    private String receivedById;

    private String receivedByName;

    @Builder.Default
    private List<ReceiptItem> items = new ArrayList<>();

    private String deliveryNoteNumber;

    private String invoiceReference;

    private String remarks;

    @Builder.Default
    private String status = "RECEIVED"; // RECEIVED, INSPECTED, REJECTED

    @CreatedDate
    private Instant createdAt;

    @LastModifiedDate
    private Instant updatedAt;

    @CreatedBy
    private String createdBy;

    @LastModifiedBy
    private String updatedBy;
}
