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
public class LeavePolicyDTO {

    private String id;

    @NotBlank(message = "Policy code is required")
    private String code;

    @NotBlank(message = "Policy name is required")
    private String name;

    @NotBlank(message = "Leave type ID is required")
    private String leaveTypeId;

    private String departmentId;

    private String designationId;

    private String branchId;

    private String employmentType;

    private String gender;

    @NotNull(message = "Annual quota is required")
    private Double annualQuota;

    private Integer maxConsecutiveDays;

    private Integer minNoticeDays;

    private Boolean allowHalfDay;

    private Boolean active;

    private Instant createdAt;

    private Instant updatedAt;
}
