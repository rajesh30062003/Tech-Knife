package com.techknife.inventory.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StockMovementDTO {

    private String id;

    @NotBlank(message = "Item ID is required")
    private String itemId;

    private String itemCode;

    private String itemName;

    private String sourceWarehouseId;

    private String sourceWarehouseName;

    private String targetWarehouseId;

    private String targetWarehouseName;

    @NotBlank(message = "Movement type is required")
    private String movementType;

    @NotNull(message = "Quantity is required")
    @Min(value = 1, message = "Quantity must be greater than zero")
    private Integer quantity;

    private String referenceNumber;

    private String performedBy;

    private String reason;

    private LocalDate movementDate;

    private Instant createdAt;
}
