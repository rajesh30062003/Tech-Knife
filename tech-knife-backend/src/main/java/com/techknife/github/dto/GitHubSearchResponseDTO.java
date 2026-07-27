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
public class GitHubSearchResponseDTO {
    private String query;
    private List<GitHubRepositoryDTO> repositories;
    private List<GitHubBranchDTO> branches;
    private List<GitHubCommitDTO> commits;
    private List<GitHubPullRequestDTO> pullRequests;
    private List<GitHubIssueDTO> issues;
    private List<GitHubReleaseDTO> releases;
    private List<GitHubContributorDTO> contributors;
}
