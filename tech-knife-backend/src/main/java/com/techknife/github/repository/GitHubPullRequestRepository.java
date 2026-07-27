package com.techknife.github.repository;

import com.techknife.github.entity.GitHubPullRequest;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface GitHubPullRequestRepository extends MongoRepository<GitHubPullRequest, String> {

    List<GitHubPullRequest> findByRepositoryId(String repositoryId);

    List<GitHubPullRequest> findByRepositoryIdAndStatus(String repositoryId, String status);

    Optional<GitHubPullRequest> findByRepositoryIdAndPrNumber(String repositoryId, int prNumber);

    List<GitHubPullRequest> findByStatus(String status);

    List<GitHubPullRequest> findByReviewerUsernamesContainingAndReviewStatus(String reviewerUsername, String reviewStatus);

    List<GitHubPullRequest> findByTitleContainingIgnoreCaseOrDescriptionContainingIgnoreCase(String title, String description);
}
