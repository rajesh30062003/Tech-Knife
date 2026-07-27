package com.techknife.attendance.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.CompoundIndexes;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "monthly_attendance_summaries")
@CompoundIndexes({
    @CompoundIndex(name = "idx_emp_year_month", def = "{'employeeId': 1, 'year': 1, 'month': 1}", unique = true)
})
public class MonthlyAttendanceSummary {

    @Id
    private String id;

    @Indexed
    private String employeeId;

    private String employeeName;

    private String departmentId;

    private Integer year;

    private Integer month;

    private Integer totalDays;

    @Builder.Default
    private Double presentDays = 0.0;

    @Builder.Default
    private Double absentDays = 0.0;

    @Builder.Default
    private Double leaveDays = 0.0;

    @Builder.Default
    private Double wfhDays = 0.0;

    @Builder.Default
    private Integer lateDays = 0;

    @Builder.Default
    private Double overtimeHours = 0.0;

    @Builder.Default
    private Double payableDays = 0.0;

    @Builder.Default
    private Boolean isFrozen = false;

    private Instant frozenAt;

    private String frozenBy;

    @Builder.Default
    private Boolean isPayrollReady = false;

    @CreatedDate
    private Instant createdAt;

    @LastModifiedDate
    private Instant updatedAt;

    @CreatedBy
    private String createdBy;
}
