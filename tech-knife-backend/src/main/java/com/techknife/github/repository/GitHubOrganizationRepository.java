package com.techknife.github.repository;

import com.techknife.github.entity.GitHubOrganization;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface GitHubOrganizationRepository extends MongoRepository<GitHubOrganization, String> {

    Optional<GitHubOrganization> findByOrgName(String orgName);

    List<GitHubOrganization> findByConnectedAccountId(String connectedAccountId);

    boolean existsByOrgName(String orgName);
}
