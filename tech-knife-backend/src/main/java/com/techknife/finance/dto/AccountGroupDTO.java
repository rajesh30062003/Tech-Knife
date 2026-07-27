package com.techknife.finance.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AccountGroupDTO {

    private String id;

    @NotBlank(message = "Group code is required")
    private String groupCode;

    @NotBlank(message = "Group name is required")
    private String groupName;

    @NotBlank(message = "Account type is required")
    private String accountType; // ASSETS, LIABILITIES, EQUITY, REVENUE, EXPENSES

    private String parentGroupId;

    private String description;

    private String status;

    private Instant createdAt;

    private Instant updatedAt;

    private String createdBy;

    private String updatedBy;
}
