package com.techknife.github.repository;

import com.techknife.github.entity.GitHubAccount;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface GitHubAccountRepository extends MongoRepository<GitHubAccount, String> {

    Optional<GitHubAccount> findByUsername(String username);

    Optional<GitHubAccount> findByConnectedEmployeeId(String employeeId);

    boolean existsByUsername(String username);
}
