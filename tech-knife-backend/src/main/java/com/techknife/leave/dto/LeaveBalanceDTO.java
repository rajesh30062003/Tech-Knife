package com.techknife.leave.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LeaveBalanceDTO {

    private String id;

    private String employeeId;

    private String leaveTypeId;

    private String leaveTypeName;

    private Integer year;

    private Double allocatedDays;

    private Double accruedDays;

    private Double carryForwardDays;

    private Double usedDays;

    private Double pendingDays;

    private Double lapsedDays;

    private Double availableDays;

    private Instant createdAt;

    private Instant updatedAt;
}
