package com.techknife.communication.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NotificationDTO {

    private String id;

    @NotBlank(message = "Recipient ID is required")
    private String recipientId;

    private String recipientEmail;

    @NotBlank(message = "Title is required")
    private String title;

    @NotBlank(message = "Body is required")
    private String body;

    private String type;
    private String category;
    private String status;
    private String linkUrl;
    private Map<String, Object> metadata;
    private Instant readAt;
    private Instant createdAt;
    private String createdBy;
}
