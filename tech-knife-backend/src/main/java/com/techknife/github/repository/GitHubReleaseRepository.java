package com.techknife.github.repository;

import com.techknife.github.entity.GitHubRelease;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface GitHubReleaseRepository extends MongoRepository<GitHubRelease, String> {

    List<GitHubRelease> findByRepositoryIdOrderByPublishedDateDesc(String repositoryId);

    Optional<GitHubRelease> findByRepositoryIdAndTagName(String repositoryId, String tagName);
}
