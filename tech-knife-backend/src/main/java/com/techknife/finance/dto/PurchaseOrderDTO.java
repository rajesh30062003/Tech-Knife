package com.techknife.finance.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PurchaseOrderDTO {

    private String id;

    private String poNumber;

    @NotBlank(message = "Vendor ID is required")
    private String vendorId;

    private String vendorName;

    private LocalDate orderDate;

    private LocalDate expectedDeliveryDate;

    private String financialYearId;

    private String costCenterId;

    private List<PurchaseOrderItemDTO> items;

    private BigDecimal subtotal;

    private BigDecimal taxTotal;

    private BigDecimal totalAmount;

    private String status;

    private String notes;

    private Instant createdAt;

    private Instant updatedAt;

    private String createdBy;

    private String updatedBy;
}
