package com.techknife.github.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
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
@Document(collection = "github_accounts")
public class GitHubAccount {

    @Id
    private String id;

    @Indexed(unique = true)
    private String username;

    private String githubUserId;

    private String name;

    private String email;

    private String avatarUrl;

    private String accessToken; // Encrypted or stored PAT / OAuth token

    private String tokenType; // OAUTH / PAT

    @Builder.Default
    private boolean connected = true;

    private String connectedEmployeeId;

    @Builder.Default
    private List<String> organizations = new ArrayList<>();

    @CreatedDate
    private Instant createdAt;

    @LastModifiedDate
    private Instant updatedAt;

    @CreatedBy
    private String createdBy;
}
