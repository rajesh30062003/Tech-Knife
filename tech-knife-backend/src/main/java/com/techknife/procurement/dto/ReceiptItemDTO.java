package com.techknife.procurement.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReceiptItemDTO {
    private String itemId;
    private String itemCode;
    private String itemName;
    private Integer orderedQuantity;
    private Integer receivedQuantity;
    private Integer rejectedQuantity;
    private String remarks;
}
