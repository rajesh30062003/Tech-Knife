package com.techknife.holiday.entity;

import com.techknife.leave.entity.LeaveStatus;
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

/**
 * MongoDB Document for Restricted Holiday opting requests by employees.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "restricted_holiday_requests")
public class RestrictedHolidayRequest {

    @Id
    private String id;

    @Indexed
    private String employeeId;

    private String employeeName;

    @Indexed
    private String holidayId;

    private String holidayName;

    private String holidayDate;

    private Integer year;

    private String reason;

    @Indexed
    @Builder.Default
    private LeaveStatus status = LeaveStatus.PENDING;

    private String approverId;

    private String approverName;

    private String comments;

    private Instant actionedAt;

    @CreatedDate
    private Instant createdAt;

    @LastModifiedDate
    private Instant updatedAt;

    @CreatedBy
    private String createdBy;

    @LastModifiedBy
    private String updatedBy;
}
