package com.techknife.github.service;

import com.techknife.github.dto.GitHubAccountDTO;
import com.techknife.github.entity.GitHubAccount;
import com.techknife.github.entity.GitHubOrganization;
import com.techknife.github.repository.GitHubAccountRepository;
import com.techknife.github.repository.GitHubOrganizationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class GitHubAccountService {

    private final GitHubAccountRepository accountRepository;
    private final GitHubOrganizationRepository organizationRepository;

    public GitHubAccountDTO connectAccount(GitHubAccountDTO dto, String connectedEmployeeId) {
        if (accountRepository.existsByUsername(dto.getUsername())) {
            GitHubAccount existing = accountRepository.findByUsername(dto.getUsername()).orElseThrow();
            existing.setConnected(true);
            existing.setAccessToken(dto.getAccessToken());
            existing.setTokenType(dto.getTokenType() != null ? dto.getTokenType() : "PAT");
            existing.setConnectedEmployeeId(connectedEmployeeId);
            GitHubAccount saved = accountRepository.save(existing);
            return mapToDTO(saved);
        }

        GitHubAccount account = GitHubAccount.builder()
                .username(dto.getUsername())
                .githubUserId(dto.getGithubUserId())
                .name(dto.getName())
                .email(dto.getEmail())
                .avatarUrl(dto.getAvatarUrl())
                .accessToken(dto.getAccessToken())
                .tokenType(dto.getTokenType() != null ? dto.getTokenType() : "PAT")
                .connected(true)
                .connectedEmployeeId(connectedEmployeeId)
                .organizations(dto.getOrganizations() != null ? dto.getOrganizations() : List.of())
                .build();

        GitHubAccount saved = accountRepository.save(account);
        return mapToDTO(saved);
    }

    public GitHubAccountDTO disconnectAccount(String username) {
        GitHubAccount account = accountRepository.findByUsername(username)
                .orElseThrow(() -> new NoSuchElementException("GitHub account not found: " + username));

        account.setConnected(false);
        account.setAccessToken(null);
        GitHubAccount saved = accountRepository.save(account);
        return mapToDTO(saved);
    }

    public GitHubAccountDTO getAccountByUsername(String username) {
        GitHubAccount account = accountRepository.findByUsername(username)
                .orElseThrow(() -> new NoSuchElementException("GitHub account not found: " + username));
        return mapToDTO(account);
    }

    public List<GitHubAccountDTO> getAllConnectedAccounts() {
        return accountRepository.findAll().stream()
                .filter(GitHubAccount::isConnected)
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    public boolean validateOAuthOrToken(String token) {
        return token != null && !token.isBlank() && (token.startsWith("ghp_") || token.startsWith("gho_") || token.length() > 10);
    }

    public List<GitHubOrganization> getOrganizationsForAccount(String accountId) {
        return organizationRepository.findByConnectedAccountId(accountId);
    }

    private GitHubAccountDTO mapToDTO(GitHubAccount account) {
        return GitHubAccountDTO.builder()
                .id(account.getId())
                .username(account.getUsername())
                .githubUserId(account.getGithubUserId())
                .name(account.getName())
                .email(account.getEmail())
                .avatarUrl(account.getAvatarUrl())
                .accessToken(account.getAccessToken() != null ? "******" : null)
                .tokenType(account.getTokenType())
                .connected(account.isConnected())
                .connectedEmployeeId(account.getConnectedEmployeeId())
                .organizations(account.getOrganizations())
                .createdAt(account.getCreatedAt())
                .updatedAt(account.getUpdatedAt())
                .build();
    }
}
