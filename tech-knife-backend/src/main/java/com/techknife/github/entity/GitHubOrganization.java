package com.techknife.github.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "github_organizations")
public class GitHubOrganization {

    @Id
    private String id;

    @Indexed(unique = true)
    private String orgName;

    private String githubOrgId;

    private String description;

    private String avatarUrl;

    private String htmlUrl;

    private int membersCount;

    private int reposCount;

    private String connectedAccountId;

    @CreatedDate
    private Instant createdAt;

    @LastModifiedDate
    private Instant updatedAt;
}
