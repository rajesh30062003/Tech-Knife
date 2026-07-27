package com.techknife.github.service;

import com.techknife.github.dto.GitHubRepositoryDTO;
import com.techknife.github.entity.GitHubRepository;
import com.techknife.github.repository.GitHubRepositoryRepository;
import com.techknife.project.repository.ProjectRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class GitHubRepositoryService {

    private final GitHubRepositoryRepository repositoryRepository;
    private final ProjectRepository projectRepository;

    public GitHubRepositoryDTO linkRepository(GitHubRepositoryDTO dto, String projectId) {
        if (projectId != null && !projectId.isBlank() && !projectRepository.existsById(projectId)) {
            throw new NoSuchElementException("Project not found with ID: " + projectId);
        }

        if (repositoryRepository.existsByFullName(dto.getFullName())) {
            GitHubRepository existing = repositoryRepository.findByFullName(dto.getFullName()).orElseThrow();
            existing.setLinked(true);
            existing.setLinkedProjectId(projectId);
            if (dto.getDescription() != null) existing.setDescription(dto.getDescription());
            if (dto.getDefaultBranch() != null) existing.setDefaultBranch(dto.getDefaultBranch());
            GitHubRepository saved = repositoryRepository.save(existing);
            return mapToDTO(saved);
        }

        GitHubRepository repository = GitHubRepository.builder()
                .githubRepoId(dto.getGithubRepoId())
                .repoName(dto.getRepoName())
                .fullName(dto.getFullName())
                .owner(dto.getOwner())
                .description(dto.getDescription())
                .defaultBranch(dto.getDefaultBranch() != null ? dto.getDefaultBranch() : "main")
                .visibility(dto.getVisibility() != null ? dto.getVisibility() : "PUBLIC")
                .language(dto.getLanguage())
                .license(dto.getLicense())
                .topics(dto.getTopics() != null ? dto.getTopics() : List.of())
                .archived(dto.isArchived())
                .linked(true)
                .linkedProjectId(projectId)
                .cloneUrl(dto.getCloneUrl())
                .htmlUrl(dto.getHtmlUrl())
                .starsCount(dto.getStarsCount())
                .forksCount(dto.getForksCount())
                .openIssuesCount(dto.getOpenIssuesCount())
                .lastSyncedAt(Instant.now())
                .build();

        GitHubRepository saved = repositoryRepository.save(repository);
        return mapToDTO(saved);
    }

    public GitHubRepositoryDTO unlinkRepository(String repositoryId) {
        GitHubRepository repository = repositoryRepository.findById(repositoryId)
                .orElseThrow(() -> new NoSuchElementException("GitHub Repository not found: " + repositoryId));

        repository.setLinked(false);
        repository.setLinkedProjectId(null);
        GitHubRepository saved = repositoryRepository.save(repository);
        return mapToDTO(saved);
    }

    public GitHubRepositoryDTO getRepositoryById(String repositoryId) {
        GitHubRepository repository = repositoryRepository.findById(repositoryId)
                .orElseThrow(() -> new NoSuchElementException("GitHub Repository not found: " + repositoryId));
        return mapToDTO(repository);
    }

    public GitHubRepositoryDTO getRepositoryByFullName(String fullName) {
        GitHubRepository repository = repositoryRepository.findByFullName(fullName)
                .orElseThrow(() -> new NoSuchElementException("GitHub Repository not found for full name: " + fullName));
        return mapToDTO(repository);
    }

    public List<GitHubRepositoryDTO> getRepositoriesByProject(String projectId) {
        return repositoryRepository.findByLinkedProjectId(projectId).stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    public List<GitHubRepositoryDTO> getAllRepositories() {
        return repositoryRepository.findAll().stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    public void deleteRepository(String repositoryId) {
        GitHubRepository repository = repositoryRepository.findById(repositoryId)
                .orElseThrow(() -> new NoSuchElementException("GitHub Repository not found: " + repositoryId));
        repositoryRepository.delete(repository);
    }

    public boolean existsByFullName(String fullName) {
        return repositoryRepository.existsByFullName(fullName);
    }

    public GitHubRepositoryDTO mapToDTO(GitHubRepository repository) {
        return GitHubRepositoryDTO.builder()
                .id(repository.getId())
                .githubRepoId(repository.getGithubRepoId())
                .repoName(repository.getRepoName())
                .fullName(repository.getFullName())
                .owner(repository.getOwner())
                .description(repository.getDescription())
                .defaultBranch(repository.getDefaultBranch())
                .visibility(repository.getVisibility())
                .language(repository.getLanguage())
                .license(repository.getLicense())
                .topics(repository.getTopics())
                .archived(repository.isArchived())
                .linked(repository.isLinked())
                .linkedProjectId(repository.getLinkedProjectId())
                .cloneUrl(repository.getCloneUrl())
                .htmlUrl(repository.getHtmlUrl())
                .starsCount(repository.getStarsCount())
                .forksCount(repository.getForksCount())
                .openIssuesCount(repository.getOpenIssuesCount())
                .lastSyncedAt(repository.getLastSyncedAt())
                .createdAt(repository.getCreatedAt())
                .updatedAt(repository.getUpdatedAt())
                .build();
    }
}
