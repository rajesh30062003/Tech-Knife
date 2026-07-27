package com.techknife.finance.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PurchaseOrderItem {

    private String itemName;

    private String description;

    @Builder.Default
    private Integer quantity = 1;

    @Builder.Default
    private BigDecimal unitPrice = BigDecimal.ZERO;

    @Builder.Default
    private BigDecimal taxRate = BigDecimal.ZERO;

    @Builder.Default
    private BigDecimal taxAmount = BigDecimal.ZERO;

    @Builder.Default
    private BigDecimal totalAmount = BigDecimal.ZERO;
}
