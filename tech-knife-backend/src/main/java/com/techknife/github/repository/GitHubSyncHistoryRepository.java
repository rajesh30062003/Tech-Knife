package com.techknife.github.repository;

import com.techknife.github.entity.GitHubSyncHistory;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface GitHubSyncHistoryRepository extends MongoRepository<GitHubSyncHistory, String> {

    List<GitHubSyncHistory> findByRepositoryIdOrderByStartedAtDesc(String repositoryId);

    List<GitHubSyncHistory> findByStatus(String status);
}
