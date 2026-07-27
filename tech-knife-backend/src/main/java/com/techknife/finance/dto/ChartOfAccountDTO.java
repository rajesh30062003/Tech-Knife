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
public class ChartOfAccountDTO {

    private String id;

    @NotBlank(message = "Account code is required")
    private String accountCode;

    @NotBlank(message = "Account name is required")
    private String accountName;

    @NotBlank(message = "Account type is required")
    private String accountType; // ASSETS, LIABILITIES, EQUITY, REVENUE, EXPENSES

    private String accountGroupId;

    private String parentAccountId;

    private Boolean isSubAccount;

    private BigDecimal openingBalance;

    private BigDecimal currentBalance;

    private String currency;

    private String description;

    private String status;

    private Instant createdAt;

    private Instant updatedAt;

    private String createdBy;

    private String updatedBy;
}
