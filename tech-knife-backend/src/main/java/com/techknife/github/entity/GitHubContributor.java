package com.techknife.github.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "github_contributors")
public class GitHubContributor {

    @Id
    private String id;

    @Indexed(unique = true)
    private String username;

    private String developerName;

    private String linkedEmployeeId;

    private String avatarUrl;

    @Builder.Default
    private List<String> repositories = new ArrayList<>();

    private int totalCommits;

    private int totalPRs;

    private int totalIssues;

    private int totalReviews;

    private double contributionScore;

    @LastModifiedDate
    private Instant updatedAt;
}
