package com.techknife.procurement.dto;

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
public class PurchaseRequestDTO {

    private String id;

    @NotBlank(message = "Request number is required")
    private String requestNumber;

    private String requestedById;

    private String requestedByName;

    private String departmentId;

    private String departmentName;

    private LocalDate requestDate;

    private LocalDate requiredDate;

    private String priority;

    private String status;

    private List<PurchaseRequestItemDTO> items;

    private BigDecimal totalEstimatedAmount;

    private String justification;

    private Instant createdAt;

    private Instant updatedAt;

    private String createdBy;

    private String updatedBy;
}
