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

/**
 * MongoDB Document tracking lifecycle timeline events for Employees.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "employee_timelines")
public class EmployeeTimeline {

    @Id
    private String id;

    @Indexed
    private String employeeId;

    private TimelineEventType eventType;

    private String oldValue;

    private String newValue;

    private String description;

    private String changedBy;

    @CreatedDate
    private Instant timestamp;

    @CreatedDate
    private Instant createdAt;

}
