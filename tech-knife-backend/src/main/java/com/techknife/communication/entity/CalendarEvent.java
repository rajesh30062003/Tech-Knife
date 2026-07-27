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
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "communication_calendar_events")
public class CalendarEvent {

    @Id
    private String id;

    private String calendarId;
    private String title;
    private String description;
    private String location;
    private Instant startTime;
    private Instant endTime;
    private boolean isAllDay;
    private String eventType; // MEETING, HOLIDAY, WORKSHOP, DEADLINE, GENERAL
    private String organizerId;
    private List<String> attendees;
    private String status; // CONFIRMED, CANCELLED, TENTATIVE
    private String meetingLink;
    private Integer reminderMinutesBefore;

    @CreatedDate
    private Instant createdAt;

    @LastModifiedDate
    private Instant updatedAt;

    @CreatedBy
    private String createdBy;
}
