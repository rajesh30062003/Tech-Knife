package com.techknife.crm.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "crm_meetings")
public class Meeting {

    @Id
    private String id;

    private String title;

    private String entityType; // LEAD, CUSTOMER, OPPORTUNITY

    @Indexed
    private String entityId;

    private String agenda;

    @Builder.Default
    private List<String> participants = new ArrayList<>();

    private Instant meetingTime;

    private String meetingNotes;

    private String outcome;

    @Builder.Default
    private List<String> followUpTasks = new ArrayList<>();

    @Builder.Default
    private String status = "SCHEDULED"; // SCHEDULED, COMPLETED, CANCELLED

    private String organizerId;

    private String meetingLink;

    @CreatedDate
    private Instant createdAt;

    @LastModifiedDate
    private Instant updatedAt;
}
