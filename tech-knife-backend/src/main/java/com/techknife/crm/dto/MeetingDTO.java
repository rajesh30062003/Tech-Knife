package com.techknife.crm.dto;

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
public class MeetingDTO {
    private String id;
    private String title;
    private String entityType;
    private String entityId;
    private String agenda;
    private List<String> participants;
    private Instant meetingTime;
    private String meetingNotes;
    private String outcome;
    private List<String> followUpTasks;
    private String status;
    private String organizerId;
    private String meetingLink;
    private Instant createdAt;
    private Instant updatedAt;
}
