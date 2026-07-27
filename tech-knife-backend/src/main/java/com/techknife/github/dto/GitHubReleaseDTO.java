package com.techknife.github.dto;

import com.techknife.github.entity.GitHubRelease;
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
public class GitHubReleaseDTO {
    private String id;
    private String repositoryId;
    private String tagName;
    private String name;
    private String releaseNotes;
    private boolean prerelease;
    private boolean draft;
    private List<GitHubRelease.ReleaseAsset> assets;
    private Instant publishedDate;
    private String authorUsername;
    private String htmlUrl;
    private Instant createdAt;
}
