package com.techknife.leave.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

/**
 * Embedded approval step for Leave Requests.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LeaveApproval {

    private Integer level; // 1 for Manager, 2 for Skip-level/HR

    private String approverId;

    private String approverName;

    private String approverRole; // MANAGER, HR, ADMIN

    private LeaveStatus status; // APPROVED, REJECTED, PENDING

    private String comments;

    private Instant actionedAt;
}
