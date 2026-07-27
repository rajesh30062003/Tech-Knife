package com.techknife.inventory.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.Instant;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class InventoryItemDTO {

    private String id;

    @NotBlank(message = "Item code is required")
    private String itemCode;

    @NotBlank(message = "Item name is required")
    private String name;

    private String description;

    private String categoryId;

    private String categoryName;

    private String unitOfMeasure;

    private Integer reorderLevel;

    private Integer minimumStock;

    private Integer maximumStock;

    private BigDecimal purchasePrice;

    private BigDecimal sellingPrice;

    private String defaultSupplierId;

    private String defaultSupplierName;

    private String status;

    private Instant createdAt;

    private Instant updatedAt;

    private String createdBy;

    private String updatedBy;
}
