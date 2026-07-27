package com.techknife.github.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "github_releases")
public class GitHubRelease {

    @Id
    private String id;

    private String repositoryId;

    private String tagName;

    private String name;

    private String releaseNotes;

    @Builder.Default
    private boolean prerelease = false;

    @Builder.Default
    private boolean draft = false;

    @Builder.Default
    private List<ReleaseAsset> assets = new ArrayList<>();

    private Instant publishedDate;

    private String authorUsername;

    private String htmlUrl;

    @CreatedDate
    private Instant createdAt;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ReleaseAsset {
        private String name;
        private String downloadUrl;
        private long size;
        private int downloadCount;
    }
}
