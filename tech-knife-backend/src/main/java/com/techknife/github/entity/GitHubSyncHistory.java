package com.techknife.github.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "github_sync_history")
public class GitHubSyncHistory {

    @Id
    private String id;

    private String repositoryId;

    private String syncType; // MANUAL, SCHEDULED, INCREMENTAL, WEBHOOK

    private String status; // IN_PROGRESS, SUCCESS, FAILED

    private int itemsSynced;

    @Builder.Default
    private List<String> errorLogs = new ArrayList<>();

    private Instant startedAt;

    private Instant completedAt;

    private String triggeredBy;
}
