package com.techknife.github.repository;

import com.techknife.github.entity.GitHubDeployment;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface GitHubDeploymentRepository extends MongoRepository<GitHubDeployment, String> {

    List<GitHubDeployment> findByRepositoryIdOrderByDeploymentTimeDesc(String repositoryId);

    List<GitHubDeployment> findByRepositoryIdAndEnvironmentOrderByDeploymentTimeDesc(String repositoryId, String environment);
}
