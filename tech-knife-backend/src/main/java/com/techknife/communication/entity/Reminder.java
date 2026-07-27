package com.techknife.communication.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "communication_reminders")
public class Reminder {

    @Id
    private String id;

    private String userId;
    private String title;
    private String description;
    private Instant reminderTime;
    private String status; // PENDING, COMPLETED, CANCELLED, DISMISSED
    private String priority; // LOW, MEDIUM, HIGH
    private String entityType; // TASK, PROJECT, EVENT, CUSTOM
    private String entityId;
    private boolean isRecurring;
    private String recurrenceRule; // DAILY, WEEKLY, MONTHLY

    @CreatedDate
    private Instant createdAt;

    @LastModifiedDate
    private Instant updatedAt;

    @CreatedBy
    private String createdBy;
}
