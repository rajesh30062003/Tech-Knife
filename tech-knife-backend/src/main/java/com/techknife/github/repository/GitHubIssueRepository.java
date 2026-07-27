package com.techknife.github.repository;

import com.techknife.github.entity.GitHubIssue;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface GitHubIssueRepository extends MongoRepository<GitHubIssue, String> {

    List<GitHubIssue> findByRepositoryId(String repositoryId);

    List<GitHubIssue> findByRepositoryIdAndStatus(String repositoryId, String status);

    Optional<GitHubIssue> findByRepositoryIdAndIssueNumber(String repositoryId, int issueNumber);

    List<GitHubIssue> findByAssigneeUsername(String assigneeUsername);

    List<GitHubIssue> findByTitleContainingIgnoreCaseOrBodyContainingIgnoreCase(String title, String body);
}
