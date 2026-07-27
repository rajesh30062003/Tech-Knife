package com.techknife.inventory.entity;

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

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "inv_items")
public class InventoryItem {

    @Id
    private String id;

    @Indexed(unique = true)
    private String itemCode;

    private String name;

    private String description;

    private String categoryId;

    private String categoryName;

    private String unitOfMeasure; // PCS, KG, LTR, BOX, PKT

    @Builder.Default
    private Integer reorderLevel = 10;

    @Builder.Default
    private Integer minimumStock = 5;

    @Builder.Default
    private Integer maximumStock = 1000;

    private BigDecimal purchasePrice;

    private BigDecimal sellingPrice;

    private String defaultSupplierId;

    private String defaultSupplierName;

    @Builder.Default
    private String status = "ACTIVE";

    @CreatedDate
    private Instant createdAt;

    @LastModifiedDate
    private Instant updatedAt;

    @CreatedBy
    private String createdBy;

    @LastModifiedBy
    private String updatedBy;
}
