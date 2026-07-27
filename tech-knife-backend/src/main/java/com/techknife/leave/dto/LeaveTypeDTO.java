package com.techknife.leave.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LeaveTypeDTO {

    private String id;

    @NotBlank(message = "Leave type code is required")
    private String code;

    @NotBlank(message = "Leave type name is required")
    private String name;

    private String description;

    @NotNull(message = "Default annual quota is required")
    private Double defaultAnnualQuota;

    private Boolean carryForwardAllowed;

    private Double maxCarryForwardDays;

    private Boolean encashable;

    private Boolean requiresAttachment;

    private Boolean active;

    private Instant createdAt;

    private Instant updatedAt;
}
