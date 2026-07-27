package com.techknife.github.repository;

import com.techknife.github.entity.GitHubBranch;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface GitHubBranchRepository extends MongoRepository<GitHubBranch, String> {

    List<GitHubBranch> findByRepositoryId(String repositoryId);

    Optional<GitHubBranch> findByRepositoryIdAndName(String repositoryId, String name);

    Optional<GitHubBranch> findByRepositoryIdAndIsDefaultTrue(String repositoryId);
}
