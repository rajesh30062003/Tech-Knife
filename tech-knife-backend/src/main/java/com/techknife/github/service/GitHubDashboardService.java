package com.techknife.github.service;

import com.techknife.github.dto.*;
import com.techknife.github.entity.*;
import com.techknife.github.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class GitHubDashboardService {

    private final GitHubRepositoryRepository repositoryRepository;
    private final GitHubBranchRepository branchRepository;
    private final GitHubCommitRepository commitRepository;
    private final GitHubPullRequestRepository pullRequestRepository;
    private final GitHubIssueRepository issueRepository;
    private final GitHubReleaseRepository releaseRepository;
    private final GitHubDeploymentRepository deploymentRepository;
    private final GitHubContributorRepository contributorRepository;
    private final GitHubRepositoryService repositoryService;

    public GitHubDashboardSummaryDTO getDashboardSummary(String repositoryId) {
        List<GitHubRepository> repos = repositoryId != null && !repositoryId.isBlank()
                ? repositoryRepository.findById(repositoryId).stream().toList()
                : repositoryRepository.findAll();

        int totalRepos = (int) repositoryRepository.count();
        int linkedRepos = (int) repositoryRepository.findAll().stream().filter(GitHubRepository::isLinked).count();

        List<GitHubPullRequest> openPRs = pullRequestRepository.findByStatus("OPEN");
        List<GitHubPullRequest> pendingReviews = pullRequestRepository.findByReviewerUsernamesContainingAndReviewStatus("", "PENDING");
        List<GitHubIssue> openIssues = issueRepository.findByTitleContainingIgnoreCaseOrBodyContainingIgnoreCase("", "");

        List<GitHubRepositoryDTO> repoOverview = repos.stream().map(repositoryService::mapToDTO).collect(Collectors.toList());

        List<GitHubPullRequestDTO> recentPRs = openPRs.stream().map(this::mapPRToDTO).limit(10).collect(Collectors.toList());
        List<GitHubCommitDTO> recentCommits = commitRepository.findAll().stream()
                .map(this::mapCommitToDTO).limit(10).collect(Collectors.toList());
        List<GitHubIssueDTO> recentIssues = openIssues.stream().filter(i -> "OPEN".equals(i.getStatus()))
                .map(this::mapIssueToDTO).limit(10).collect(Collectors.toList());
        List<GitHubReleaseDTO> latestReleases = releaseRepository.findAll().stream()
                .map(this::mapReleaseToDTO).limit(5).collect(Collectors.toList());
        List<GitHubDeploymentDTO> recentDeployments = deploymentRepository.findAll().stream()
                .map(this::mapDeploymentToDTO).limit(5).collect(Collectors.toList());
        List<GitHubContributorDTO> topContributors = contributorRepository.findAll().stream()
                .map(this::mapContributorToDTO).limit(10).collect(Collectors.toList());

        return GitHubDashboardSummaryDTO.builder()
                .totalRepositories(totalRepos)
                .linkedRepositories(linkedRepos)
                .openPullRequests(openPRs.size())
                .pendingReviews(pendingReviews.size())
                .openIssues((int) issueRepository.findAll().stream().filter(i -> "OPEN".equals(i.getStatus())).count())
                .totalDeployments((int) deploymentRepository.count())
                .repositoryOverview(repoOverview)
                .recentPRs(recentPRs)
                .recentCommits(recentCommits)
                .recentIssues(recentIssues)
                .latestReleases(latestReleases)
                .recentDeployments(recentDeployments)
                .topContributors(topContributors)
                .build();
    }

    public GitHubSearchResponseDTO search(String query) {
        String q = query != null ? query : "";

        List<GitHubRepositoryDTO> repos = repositoryRepository.findByRepoNameContainingIgnoreCaseOrDescriptionContainingIgnoreCase(q, q)
                .stream().map(repositoryService::mapToDTO).collect(Collectors.toList());

        List<GitHubCommitDTO> commits = commitRepository.findByMessageContainingIgnoreCase(q)
                .stream().map(this::mapCommitToDTO).collect(Collectors.toList());

        List<GitHubPullRequestDTO> prs = pullRequestRepository.findByTitleContainingIgnoreCaseOrDescriptionContainingIgnoreCase(q, q)
                .stream().map(this::mapPRToDTO).collect(Collectors.toList());

        List<GitHubIssueDTO> issues = issueRepository.findByTitleContainingIgnoreCaseOrBodyContainingIgnoreCase(q, q)
                .stream().map(this::mapIssueToDTO).collect(Collectors.toList());

        List<GitHubContributorDTO> contributors = contributorRepository.findAll().stream()
                .filter(c -> c.getUsername().toLowerCase().contains(q.toLowerCase()) || (c.getDeveloperName() != null && c.getDeveloperName().toLowerCase().contains(q.toLowerCase())))
                .map(this::mapContributorToDTO).collect(Collectors.toList());

        return GitHubSearchResponseDTO.builder()
                .query(q)
                .repositories(repos)
                .branches(List.of())
                .commits(commits)
                .pullRequests(prs)
                .issues(issues)
                .releases(List.of())
                .contributors(contributors)
                .build();
    }

    private GitHubPullRequestDTO mapPRToDTO(GitHubPullRequest pr) {
        return GitHubPullRequestDTO.builder()
                .id(pr.getId())
                .repositoryId(pr.getRepositoryId())
                .prNumber(pr.getPrNumber())
                .title(pr.getTitle())
                .description(pr.getDescription())
                .status(pr.getStatus())
                .headBranch(pr.getHeadBranch())
                .baseBranch(pr.getBaseBranch())
                .authorUsername(pr.getAuthorUsername())
                .reviewerUsernames(pr.getReviewerUsernames())
                .reviewStatus(pr.getReviewStatus())
                .mergedBy(pr.getMergedBy())
                .mergeDate(pr.getMergeDate())
                .closedAt(pr.getClosedAt())
                .htmlUrl(pr.getHtmlUrl())
                .linkedTaskId(pr.getLinkedTaskId())
                .createdAt(pr.getCreatedAt())
                .updatedAt(pr.getUpdatedAt())
                .build();
    }

    private GitHubCommitDTO mapCommitToDTO(GitHubCommit commit) {
        return GitHubCommitDTO.builder()
                .id(commit.getId())
                .repositoryId(commit.getRepositoryId())
                .commitSha(commit.getCommitSha())
                .authorName(commit.getAuthorName())
                .authorEmail(commit.getAuthorEmail())
                .authorUsername(commit.getAuthorUsername())
                .committerName(commit.getCommitterName())
                .committerEmail(commit.getCommitterEmail())
                .message(commit.getMessage())
                .branchName(commit.getBranchName())
                .filesChanged(commit.getFilesChanged())
                .additions(commit.getAdditions())
                .deletions(commit.getDeletions())
                .commitTime(commit.getCommitTime())
                .linkedEmployeeId(commit.getLinkedEmployeeId())
                .htmlUrl(commit.getHtmlUrl())
                .build();
    }

    private GitHubIssueDTO mapIssueToDTO(GitHubIssue issue) {
        return GitHubIssueDTO.builder()
                .id(issue.getId())
                .repositoryId(issue.getRepositoryId())
                .issueNumber(issue.getIssueNumber())
                .title(issue.getTitle())
                .body(issue.getBody())
                .assigneeUsername(issue.getAssigneeUsername())
                .labels(issue.getLabels())
                .milestoneTitle(issue.getMilestoneTitle())
                .priority(issue.getPriority())
                .status(issue.getStatus())
                .createdBy(issue.getCreatedBy())
                .htmlUrl(issue.getHtmlUrl())
                .linkedTaskId(issue.getLinkedTaskId())
                .closedAt(issue.getClosedAt())
                .createdAt(issue.getCreatedAt())
                .updatedAt(issue.getUpdatedAt())
                .build();
    }

    private GitHubReleaseDTO mapReleaseToDTO(GitHubRelease release) {
        return GitHubReleaseDTO.builder()
                .id(release.getId())
                .repositoryId(release.getRepositoryId())
                .tagName(release.getTagName())
                .name(release.getName())
                .releaseNotes(release.getReleaseNotes())
                .prerelease(release.isPrerelease())
                .draft(release.isDraft())
                .assets(release.getAssets())
                .publishedDate(release.getPublishedDate())
                .authorUsername(release.getAuthorUsername())
                .htmlUrl(release.getHtmlUrl())
                .createdAt(release.getCreatedAt())
                .build();
    }

    private GitHubDeploymentDTO mapDeploymentToDTO(GitHubDeployment deployment) {
        return GitHubDeploymentDTO.builder()
                .id(deployment.getId())
                .repositoryId(deployment.getRepositoryId())
                .environment(deployment.getEnvironment())
                .buildNumber(deployment.getBuildNumber())
                .commitSha(deployment.getCommitSha())
                .status(deployment.getStatus())
                .deploymentTime(deployment.getDeploymentTime())
                .deployedBy(deployment.getDeployedBy())
                .rollbackFromDeploymentId(deployment.getRollbackFromDeploymentId())
                .createdAt(deployment.getCreatedAt())
                .updatedAt(deployment.getUpdatedAt())
                .build();
    }

    private GitHubContributorDTO mapContributorToDTO(GitHubContributor c) {
        return GitHubContributorDTO.builder()
                .id(c.getId())
                .username(c.getUsername())
                .developerName(c.getDeveloperName())
                .linkedEmployeeId(c.getLinkedEmployeeId())
                .avatarUrl(c.getAvatarUrl())
                .repositories(c.getRepositories())
                .totalCommits(c.getTotalCommits())
                .totalPRs(c.getTotalPRs())
                .totalIssues(c.getTotalIssues())
                .totalReviews(c.getTotalReviews())
                .contributionScore(c.getContributionScore())
                .updatedAt(c.getUpdatedAt())
                .build();
    }
}
