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
public class GitHubContributorDTO {
    private String id;
    private String username;
    private String developerName;
    private String linkedEmployeeId;
    private String avatarUrl;
    private List<String> repositories;
    private int totalCommits;
    private int totalPRs;
    private int totalIssues;
    private int totalReviews;
    private double contributionScore;
    private Instant updatedAt;
}
