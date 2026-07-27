package com.techknife.employee.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;
import java.time.LocalDate;

/**
 * MongoDB document tracking historic timeline changes for an Employee record.
 * Captures changes in department, designation, manager, salary grade, branch, and status.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "employee_timelines")
public class EmployeeTimelineRecord {

    @Id
    private String id;

    @Indexed
    private String employeeId;

    private String changeType; // e.g. DEPARTMENT_CHANGE, DESIGNATION_CHANGE, SALARY_GRADE_CHANGE, MANAGER_CHANGE, BRANCH_TRANSFER, STATUS_CHANGE

    private String oldValue;

    private String newValue;

    private String description;

    private LocalDate effectiveDate;

    @CreatedBy
    private String changedBy;

    @CreatedDate
    private Instant createdAt;
}
