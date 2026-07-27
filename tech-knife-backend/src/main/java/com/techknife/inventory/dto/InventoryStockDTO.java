package com.techknife.inventory.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class InventoryStockDTO {

    private String id;

    @NotBlank(message = "Item ID is required")
    private String itemId;

    private String itemCode;

    private String itemName;

    @NotBlank(message = "Warehouse ID is required")
    private String warehouseId;

    private String warehouseName;

    private Integer quantity;

    private Integer availableQuantity;

    private Integer reservedQuantity;

    private Instant lastUpdated;

    private Instant createdAt;

    private Instant updatedAt;
}
