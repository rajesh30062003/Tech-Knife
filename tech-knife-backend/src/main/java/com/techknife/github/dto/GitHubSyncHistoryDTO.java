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
public class GitHubSyncHistoryDTO {
    private String id;
    private String repositoryId;
    private String syncType;
    private String status;
    private int itemsSynced;
    private List<String> errorLogs;
    private Instant startedAt;
    private Instant completedAt;
    private String triggeredBy;
}
