package com.techknife.backend.github;

import com.techknife.backend.config.GitHubConfig;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class GitHubIntegrationService {

    private final GitHubConfig gitHubConfig;

    public Map<String, Object> syncOrganizationRepositories() {
        log.info("Synchronizing GitHub repositories for organization: {}", gitHubConfig.getOrganization());
        
        List<Map<String, Object>> mockRepositories = List.of(
                Map.of("id", 101, "name", "tech-knife-backend", "language", "Java", "stars", 42, "visibility", "private"),
                Map.of("id", 102, "name", "tech-knife-frontend", "language", "TypeScript", "stars", 38, "visibility", "private"),
                Map.of("id", 103, "name", "tech-knife-infra", "language", "HCL", "stars", 15, "visibility", "private")
        );

        return Map.of(
                "organization", gitHubConfig.getOrganization() != null ? gitHubConfig.getOrganization() : "tech-knife-org",
                "syncedCount", mockRepositories.size(),
                "repositories", mockRepositories,
                "lastSyncedAt", Instant.now().toString()
        );
    }

    public List<Map<String, Object>> getRecentCommits(String repoName) {
        log.info("Fetching recent commits for repository: {}", repoName);
        return List.of(
                Map.of("sha", "9f8a7b6c5d", "message", "feat: implement Spring Security JWT and RBAC APIs", "author", "Marcus Brody", "date", Instant.now().toString()),
                Map.of("sha", "1a2b3c4d5e", "message", "fix: Mongo DB index optimization", "author", "Clara Oswald", "date", Instant.now().minusSeconds(3600).toString())
        );
    }
}
