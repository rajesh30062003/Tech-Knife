package com.techknife.leave.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * MongoDB Document for employee leave applications.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "leave_requests")
public class LeaveRequest {

    @Id
    private String id;

    @Indexed
    private String employeeId;

    private String employeeName;

    private String departmentId;

    @Indexed
    private String leaveTypeId;

    private String leaveTypeName;

    private LocalDate startDate;

    private LocalDate endDate;

    @Builder.Default
    private Double totalDays = 0.0;

    @Builder.Default
    private HalfDayType halfDayType = HalfDayType.NONE;

    private String reason;

    private String attachmentUrl;

    @Indexed
    @Builder.Default
    private LeaveStatus status = LeaveStatus.PENDING;

    @Builder.Default
    private List<LeaveApproval> approvals = new ArrayList<>();

    private String currentApproverId;

    private String cancellationReason;

    private Instant cancelledAt;

    @CreatedDate
    private Instant createdAt;

    @LastModifiedDate
    private Instant updatedAt;

    @CreatedBy
    private String createdBy;

    @LastModifiedBy
    private String updatedBy;
}
