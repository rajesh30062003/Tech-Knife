package com.techknife.inventory.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "inv_stocks")
public class InventoryStock {

    @Id
    private String id;

    private String itemId;

    private String itemCode;

    private String itemName;

    private String warehouseId;

    private String warehouseName;

    @Builder.Default
    private Integer quantity = 0;

    @Builder.Default
    private Integer availableQuantity = 0;

    @Builder.Default
    private Integer reservedQuantity = 0;

    private Instant lastUpdated;

    @CreatedDate
    private Instant createdAt;

    @LastModifiedDate
    private Instant updatedAt;
}
