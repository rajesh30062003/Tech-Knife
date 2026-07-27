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
public class GitHubAccountDTO {
    private String id;
    private String username;
    private String githubUserId;
    private String name;
    private String email;
    private String avatarUrl;
    private String accessToken;
    private String tokenType; // OAUTH / PAT
    private boolean connected;
    private String connectedEmployeeId;
    private List<String> organizations;
    private Instant createdAt;
    private Instant updatedAt;
}
