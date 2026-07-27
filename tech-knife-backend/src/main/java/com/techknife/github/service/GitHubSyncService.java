package com.techknife.github.service;

import com.techknife.github.dto.*;
import com.techknife.github.entity.*;
import com.techknife.github.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class GitHubSyncService {

    private final GitHubRepositoryRepository repositoryRepository;
    private final GitHubBranchRepository branchRepository;
    private final GitHubCommitRepository commitRepository;
    private final GitHubPullRequestRepository pullRequestRepository;
    private final GitHubIssueRepository issueRepository;
    private final GitHubReleaseRepository releaseRepository;
    private final GitHubDeploymentRepository deploymentRepository;
    private final GitHubContributorRepository contributorRepository;
    private final GitHubSyncHistoryRepository syncHistoryRepository;

    public GitHubSyncHistoryDTO triggerManualSync(String repositoryId, String triggeredBy) {
        GitHubRepository repository = repositoryRepository.findById(repositoryId)
                .orElseThrow(() -> new NoSuchElementException("Repository not found: " + repositoryId));

        GitHubSyncHistory syncHistory = GitHubSyncHistory.builder()
                .repositoryId(repositoryId)
                .syncType("MANUAL")
                .status("IN_PROGRESS")
                .startedAt(Instant.now())
                .triggeredBy(triggeredBy != null ? triggeredBy : "SYSTEM")
                .errorLogs(new ArrayList<>())
                .build();

        syncHistory = syncHistoryRepository.save(syncHistory);

        try {
            int itemsSynced = performRepositorySync(repository);
            syncHistory.setStatus("SUCCESS");
            syncHistory.setItemsSynced(itemsSynced);
            syncHistory.setCompletedAt(Instant.now());

            repository.setLastSyncedAt(Instant.now());
            repositoryRepository.save(repository);
        } catch (Exception e) {
            log.error("Sync failed for repository: {}", repositoryId, e);
            syncHistory.setStatus("FAILED");
            syncHistory.getErrorLogs().add("Sync error: " + e.getMessage());
            syncHistory.setCompletedAt(Instant.now());
        }

        GitHubSyncHistory saved = syncHistoryRepository.save(syncHistory);
        return mapToSyncHistoryDTO(saved);
    }

    public GitHubSyncHistoryDTO retryFailedSync(String syncHistoryId, String triggeredBy) {
        GitHubSyncHistory history = syncHistoryRepository.findById(syncHistoryId)
                .orElseThrow(() -> new NoSuchElementException("Sync History not found: " + syncHistoryId));

        return triggerManualSync(history.getRepositoryId(), triggeredBy + " (RETRY)");
    }

    public List<GitHubSyncHistoryDTO> getSyncHistory(String repositoryId) {
        return syncHistoryRepository.findByRepositoryIdOrderByStartedAtDesc(repositoryId).stream()
                .map(this::mapToSyncHistoryDTO)
                .collect(Collectors.toList());
    }

    private int performRepositorySync(GitHubRepository repository) {
        int count = 0;
        String repoId = repository.getId();

        // 1. Sync default branch if none exists
        if (branchRepository.findByRepositoryId(repoId).isEmpty()) {
            GitHubBranch branch = GitHubBranch.builder()
                    .repositoryId(repoId)
                    .name(repository.getDefaultBranch() != null ? repository.getDefaultBranch() : "main")
                    .isDefault(true)
                    .isProtected(true)
                    .creatorUsername(repository.getOwner())
                    .lastCommitSha("a1b2c3d4e5f6")
                    .lastCommitMessage("Initial commit")
                    .lastCommitDate(Instant.now())
                    .build();
            branchRepository.save(branch);
            count++;
        }

        // 2. Sync sample commits if none exists
        if (commitRepository.findByRepositoryIdOrderByCommitTimeDesc(repoId).isEmpty()) {
            GitHubCommit commit = GitHubCommit.builder()
                    .repositoryId(repoId)
                    .commitSha("a1b2c3d4e5f67890123456789012345678901234")
                    .authorName(repository.getOwner())
                    .authorUsername(repository.getOwner())
                    .message("feat: project setup and initial configuration")
                    .branchName(repository.getDefaultBranch())
                    .filesChanged(5)
                    .additions(120)
                    .deletions(0)
                    .commitTime(Instant.now())
                    .htmlUrl(repository.getHtmlUrl() + "/commit/a1b2c3d4e5f6")
                    .build();
            commitRepository.save(commit);
            count++;
        }

        // 3. Update contributor stats
        GitHubContributor contributor = contributorRepository.findByUsername(repository.getOwner())
                .orElseGet(() -> GitHubContributor.builder()
                        .username(repository.getOwner())
                        .developerName(repository.getOwner())
                        .repositories(new ArrayList<>())
                        .build());

        if (!contributor.getRepositories().contains(repoId)) {
            contributor.getRepositories().add(repoId);
        }
        contributor.setTotalCommits(contributor.getTotalCommits() + 1);
        contributor.setContributionScore(contributor.getTotalCommits() * 10.0 + contributor.getTotalPRs() * 25.0);
        contributorRepository.save(contributor);

        return count;
    }

    private GitHubSyncHistoryDTO mapToSyncHistoryDTO(GitHubSyncHistory history) {
        return GitHubSyncHistoryDTO.builder()
                .id(history.getId())
                .repositoryId(history.getRepositoryId())
                .syncType(history.getSyncType())
                .status(history.getStatus())
                .itemsSynced(history.getItemsSynced())
                .errorLogs(history.getErrorLogs())
                .startedAt(history.getStartedAt())
                .completedAt(history.getCompletedAt())
                .triggeredBy(history.getTriggeredBy())
                .build();
    }
}
