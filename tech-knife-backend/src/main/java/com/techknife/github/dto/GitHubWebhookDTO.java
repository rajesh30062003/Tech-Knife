package com.techknife.github.dto;

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
public class GitHubWebhookDTO {
    private String id;
    private String repositoryId;
    private String webhookId;
    private String payloadUrl;
    private String secret;
    private List<String> events;
    private boolean active;
    private Instant lastTriggeredAt;
    private Instant createdAt;
}
