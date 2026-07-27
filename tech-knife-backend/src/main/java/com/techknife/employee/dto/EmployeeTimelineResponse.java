package com.techknife.employee.dto;

import com.techknife.employee.entity.TimelineEventType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EmployeeTimelineResponse {
    private String id;
    private String employeeId;
    private TimelineEventType eventType;
    private String oldValue;
    private String newValue;
    private String description;
    private String changedBy;
    private Instant timestamp;
}
