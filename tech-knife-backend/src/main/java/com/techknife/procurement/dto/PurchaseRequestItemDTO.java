package com.techknife.procurement.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PurchaseRequestItemDTO {
    private String itemId;
    private String itemCode;
    private String itemName;
    private Integer quantity;
    private BigDecimal estimatedUnitPrice;
    private BigDecimal estimatedTotalPrice;
    private String remarks;
}
