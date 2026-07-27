package com.techknife.github.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "github_webhooks")
public class GitHubWebhook {

    @Id
    private String id;

    private String repositoryId;

    private String webhookId; // GitHub Webhook ID

    private String payloadUrl;

    private String secret;

    @Builder.Default
    private List<String> events = new ArrayList<>();

    @Builder.Default
    private boolean active = true;

    private Instant lastTriggeredAt;

    @CreatedDate
    private Instant createdAt;
}
