package com.techknife.leave.dto;

import com.techknife.leave.entity.WFHStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WFHRequestDTO {

    private String id;

    private String employeeId;

    private String employeeName;

    private String departmentId;

    private LocalDate startDate;

    private LocalDate endDate;

    private Double totalDays;

    private String reason;

    private String workPlan;

    private WFHStatus status;

    private String approverId;

    private String approverName;

    private String approverComments;

    private Instant actionedAt;

    private Instant createdAt;

    private Instant updatedAt;
}
