package com.techknife.communication.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NotificationQueueDTO {

    private String id;
    private String notificationId;
    private String recipient;
    private String channel;
    private String status;
    private int retries;
    private int maxRetries;
    private String errorMessage;
    private Instant scheduledAt;
    private Instant processedAt;
    private Instant createdAt;
}
