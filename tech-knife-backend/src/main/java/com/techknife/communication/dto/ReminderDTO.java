package com.techknife.communication.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReminderDTO {

    private String id;
    private String userId;

    @NotBlank(message = "Title is required")
    private String title;

    private String description;

    @NotNull(message = "Reminder time is required")
    private Instant reminderTime;

    private String status;
    private String priority;
    private String entityType;
    private String entityId;
    private boolean isRecurring;
    private String recurrenceRule;
    private Instant createdAt;
    private Instant updatedAt;
    private String createdBy;
}
