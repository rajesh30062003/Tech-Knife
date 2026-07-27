package com.techknife.communication.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;

import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "communication_notifications")
public class Notification {

    @Id
    private String id;

    private String recipientId;
    private String recipientEmail;
    private String title;
    private String body;
    private String type; // EMAIL, IN_APP, SMS, PUSH
    private String category;
    private String status; // UNREAD, READ, ARCHIVED
    private String linkUrl;
    private Map<String, Object> metadata;
    private Instant readAt;

    @CreatedDate
    private Instant createdAt;

    @CreatedBy
    private String createdBy;
}
