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
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.CompoundIndexes;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;
import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "attendance_records")
@CompoundIndexes({
    @CompoundIndex(name = "idx_emp_date", def = "{'employeeId': 1, 'date': 1}"),
    @CompoundIndex(name = "idx_dept_date", def = "{'departmentId': 1, 'date': 1}"),
    @CompoundIndex(name = "idx_date_status", def = "{'date': 1, 'status': 1}")
})
public class AttendanceRecord {

    @Id
    private String id;

    @Indexed
    private String employeeId;

    private String employeeName;

    @Indexed
    private String departmentId;

    private String branchId;

    private String shiftId;

    @Indexed
    private LocalDate date;

    private Instant checkIn;

    private Instant checkOut;

    @Indexed
    @Builder.Default
    private AttendanceStatus status = AttendanceStatus.PRESENT;

    @Builder.Default
    private Double workHours = 0.0;

    @Builder.Default
    private Double overtimeHours = 0.0;

    @Builder.Default
    private Boolean isLate = false;

    @Builder.Default
    private Boolean isEarlyExit = false;

    @Builder.Default
    private Integer lateMinutes = 0;

    @Builder.Default
    private Integer earlyExitMinutes = 0;

    @Builder.Default
    private Boolean isRegularized = false;

    @Builder.Default
    private Boolean isFrozen = false;

    private String remarks;

    @CreatedDate
    private Instant createdAt;

    @LastModifiedDate
    private Instant updatedAt;

    @CreatedBy
    private String createdBy;

    @LastModifiedBy
    private String updatedBy;
}
