package com.techknife.github.repository;

import com.techknife.github.entity.GitHubCommit;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface GitHubCommitRepository extends MongoRepository<GitHubCommit, String> {

    List<GitHubCommit> findByRepositoryIdOrderByCommitTimeDesc(String repositoryId);

    List<GitHubCommit> findByRepositoryIdAndBranchNameOrderByCommitTimeDesc(String repositoryId, String branchName);

    Optional<GitHubCommit> findByRepositoryIdAndCommitSha(String repositoryId, String commitSha);

    List<GitHubCommit> findByAuthorUsername(String authorUsername);

    List<GitHubCommit> findByMessageContainingIgnoreCase(String keyword);
}
