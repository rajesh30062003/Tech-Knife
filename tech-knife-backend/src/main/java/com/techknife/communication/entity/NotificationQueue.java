package com.techknife.communication.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "communication_notification_queues")
public class NotificationQueue {

    @Id
    private String id;

    private String notificationId;
    private String recipient;
    private String channel; // EMAIL, SMS, PUSH, IN_APP
    private String status; // PENDING, PROCESSING, SENT, FAILED
    private int retries;
    private int maxRetries;
    private String errorMessage;
    private Instant scheduledAt;
    private Instant processedAt;

    @CreatedDate
    private Instant createdAt;
}
