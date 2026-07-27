package com.techknife.github.repository;

import com.techknife.github.entity.GitHubContributor;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface GitHubContributorRepository extends MongoRepository<GitHubContributor, String> {

    Optional<GitHubContributor> findByUsername(String username);

    Optional<GitHubContributor> findByLinkedEmployeeId(String employeeId);

    List<GitHubContributor> findByRepositoriesContaining(String repositoryId);
}
