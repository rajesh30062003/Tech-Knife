package com.techknife.crm.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FollowUpDTO {
    private String id;
    private String entityType;
    private String entityId;
    private String title;
    private String description;
    private Instant reminderDate;
    private String priority;
    private String status;
    private String assignedEmployeeId;
    private Instant createdAt;
    private Instant updatedAt;
}
