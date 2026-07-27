package com.techknife.communication.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CalendarEventDTO {

    private String id;

    @NotBlank(message = "Calendar ID is required")
    private String calendarId;

    @NotBlank(message = "Event title is required")
    private String title;

    private String description;
    private String location;

    @NotNull(message = "Start time is required")
    private Instant startTime;

    @NotNull(message = "End time is required")
    private Instant endTime;

    private boolean isAllDay;
    private String eventType;
    private String organizerId;
    private List<String> attendees;
    private String status;
    private String meetingLink;
    private Integer reminderMinutesBefore;
    private Instant createdAt;
    private Instant updatedAt;
    private String createdBy;
}
