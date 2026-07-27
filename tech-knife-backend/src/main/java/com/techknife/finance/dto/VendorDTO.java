package com.techknife.finance.dto;

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
public class VendorDTO {

    private String id;

    @NotBlank(message = "Vendor code is required")
    private String vendorCode;

    @NotBlank(message = "Vendor name is required")
    private String vendorName;

    private String email;

    private String phone;

    private String address;

    private String gstNumber;

    private String panNumber;

    private String bankName;

    private String accountNumber;

    private String ifscCode;

    private String branchName;

    private BigDecimal outstandingBalance;

    private BigDecimal totalPurchases;

    private String status;

    private Instant createdAt;

    private Instant updatedAt;

    private String createdBy;

    private String updatedBy;
}
