package com.techknife.crm.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class QuotationItem {
    private String name;
    private String description;
    private Integer quantity;
    private Double unitPrice;
    private Double discount;
    private Double tax;
    private Double totalPrice;
}
