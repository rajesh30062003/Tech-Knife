package com.techknife.intern.entity;

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
 * MongoDB Document summarizing monthly attendance metrics for an Intern.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "intern_attendance_summaries")
public class InternAttendanceSummary {

    @Id
    private String id;

    @Indexed
    private String internId;

    private String monthYear; // e.g., "2026-07"

    private Integer totalWorkingDays;

    private Integer presentDays;

    private Integer absentDays;

    private Integer leaveDays;

    private Double attendancePercentage;

    @CreatedDate
    private Instant createdAt;

    @LastModifiedDate
    private Instant updatedAt;

    @CreatedBy
    private String createdBy;

    @LastModifiedBy
    private String updatedBy;
}
