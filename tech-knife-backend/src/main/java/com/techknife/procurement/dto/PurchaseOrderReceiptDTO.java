package com.techknife.procurement.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.time.LocalDate;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PurchaseOrderReceiptDTO {

    private String id;

    @NotBlank(message = "Receipt number is required")
    private String receiptNumber;

    private String purchaseOrderId;

    private String poNumber;

    private String supplierId;

    private String supplierName;

    private LocalDate receivedDate;

    private String receivedById;

    private String receivedByName;

    private List<ReceiptItemDTO> items;

    private String deliveryNoteNumber;

    private String invoiceReference;

    private String remarks;

    private String status;

    private Instant createdAt;

    private Instant updatedAt;

    private String createdBy;

    private String updatedBy;
}
