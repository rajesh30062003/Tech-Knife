package com.techknife.attendance.entity;

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

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "attendance_regularizations")
public class AttendanceRegularization {

    @Id
    private String id;

    @Indexed
    private String employeeId;

    private String employeeName;

    private String departmentId;

    private String attendanceRecordId;

    @Indexed
    private LocalDate date;

    private RegularizationType type;

    private Instant requestedCheckIn;

    private Instant requestedCheckOut;

    private String reason;

    @Indexed
    @Builder.Default
    private RegularizationStatus status = RegularizationStatus.PENDING;

    private String approverId;

    private String approverName;

    private String approverComments;

    private Instant approvedOrRejectedAt;

    @CreatedDate
    private Instant createdAt;

    @LastModifiedDate
    private Instant updatedAt;

    @CreatedBy
    private String createdBy;

    @LastModifiedBy
    private String updatedBy;
}
