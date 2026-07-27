package com.techknife.github.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GitHubDashboardSummaryDTO {
    private int totalRepositories;
    private int linkedRepositories;
    private int openPullRequests;
    private int pendingReviews;
    private int openIssues;
    private int totalDeployments;
    private List<GitHubRepositoryDTO> repositoryOverview;
    private List<GitHubPullRequestDTO> recentPRs;
    private List<GitHubCommitDTO> recentCommits;
    private List<GitHubIssueDTO> recentIssues;
    private List<GitHubReleaseDTO> latestReleases;
    private List<GitHubDeploymentDTO> recentDeployments;
    private List<GitHubContributorDTO> topContributors;
}
