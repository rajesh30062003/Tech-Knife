package com.techknife.github.repository;

import com.techknife.github.entity.GitHubWebhook;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface GitHubWebhookRepository extends MongoRepository<GitHubWebhook, String> {

    List<GitHubWebhook> findByRepositoryId(String repositoryId);

    Optional<GitHubWebhook> findByRepositoryIdAndWebhookId(String repositoryId, String webhookId);
}
