package com.techknife.inventory.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;
import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "inv_stock_movements")
public class StockMovement {

    @Id
    private String id;

    private String itemId;

    private String itemCode;

    private String itemName;

    private String sourceWarehouseId;

    private String sourceWarehouseName;

    private String targetWarehouseId;

    private String targetWarehouseName;

    private String movementType; // RECEIVE, ISSUE, TRANSFER, RETURN, DAMAGE, ADJUSTMENT

    private Integer quantity;

    private String referenceNumber;

    private String performedBy;

    private String reason;

    private LocalDate movementDate;

    @CreatedDate
    private Instant createdAt;
}
