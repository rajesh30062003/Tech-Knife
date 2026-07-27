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
public class NotificationPreferenceDTO {

    private String id;

    @NotBlank(message = "User ID is required")
    private String userId;

    private String userEmail;
    private boolean emailEnabled;
    private boolean pushEnabled;
    private boolean inAppEnabled;
    private boolean smsEnabled;
    private Map<String, Boolean> categorySettings;
    private Instant createdAt;
    private Instant updatedAt;
}
