package com.techknife.leave.dto;

import com.techknife.leave.entity.HalfDayType;
import com.techknife.leave.entity.LeaveApproval;
import com.techknife.leave.entity.LeaveStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.time.LocalDate;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LeaveRequestDTO {

    private String id;

    private String employeeId;

    private String employeeName;

    private String departmentId;

    private String leaveTypeId;

    private String leaveTypeName;

    private LocalDate startDate;

    private LocalDate endDate;

    private Double totalDays;

    private HalfDayType halfDayType;

    private String reason;

    private String attachmentUrl;

    private LeaveStatus status;

    private List<LeaveApproval> approvals;

    private String currentApproverId;

    private String cancellationReason;

    private Instant cancelledAt;

    private Instant createdAt;

    private Instant updatedAt;
}
