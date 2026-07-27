package com.techknife.asset.dto;

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
public class SoftwareLicenseDTO {

    private String id;

    @NotBlank(message = "License key is required")
    private String licenseKey;

    @NotBlank(message = "Software name is required")
    private String softwareName;

    private String vendor;

    private LocalDate purchaseDate;

    private LocalDate expiryDate;

    private Integer seatsPurchased;

    private Integer seatsUsed;

    private BigDecimal cost;

    private String status;

    private List<String> assignedEmployeeIds;

    private List<String> assignedEmployeeNames;

    private String notes;

    private Instant createdAt;

    private Instant updatedAt;
}
