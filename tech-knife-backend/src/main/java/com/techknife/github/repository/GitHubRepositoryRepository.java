package com.techknife.github.repository;

import com.techknife.github.entity.GitHubRepository;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface GitHubRepositoryRepository extends MongoRepository<GitHubRepository, String> {

    Optional<GitHubRepository> findByFullName(String fullName);

    List<GitHubRepository> findByOwner(String owner);

    List<GitHubRepository> findByLinkedProjectId(String projectId);

    boolean existsByFullName(String fullName);

    List<GitHubRepository> findByRepoNameContainingIgnoreCaseOrDescriptionContainingIgnoreCase(String repoName, String description);
}
