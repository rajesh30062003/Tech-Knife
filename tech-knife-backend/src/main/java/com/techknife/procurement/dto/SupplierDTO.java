package com.techknife.procurement.dto;

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
public class SupplierDTO {

    private String id;

    @NotBlank(message = "Supplier code is required")
    private String supplierCode;

    @NotBlank(message = "Company name is required")
    private String companyName;

    private String contactPerson;

    private String email;

    private String phone;

    private String gstNumber;

    private String panNumber;

    private String address;

    private Double rating;

    private BigDecimal outstandingBalance;

    private String status;

    private Instant createdAt;

    private Instant updatedAt;

    private String createdBy;

    private String updatedBy;
}
