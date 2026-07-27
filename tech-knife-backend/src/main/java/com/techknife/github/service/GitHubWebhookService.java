package com.techknife.github.service;

import com.techknife.github.dto.GitHubWebhookDTO;
import com.techknife.github.entity.*;
import com.techknife.github.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.time.Instant;
import java.util.HexFormat;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class GitHubWebhookService {

    private final GitHubWebhookRepository webhookRepository;
    private final GitHubRepositoryRepository repositoryRepository;
    private final GitHubCommitRepository commitRepository;
    private final GitHubPullRequestRepository pullRequestRepository;
    private final GitHubIssueRepository issueRepository;
    private final GitHubReleaseRepository releaseRepository;
    private final GitHubDeploymentRepository deploymentRepository;
    private final GitHubBranchRepository branchRepository;

    public GitHubWebhookDTO createWebhook(String repositoryId, List<String> events, String secret) {
        GitHubRepository repository = repositoryRepository.findById(repositoryId)
                .orElseThrow(() -> new NoSuchElementException("Repository not found: " + repositoryId));

        String webhookId = "wh_" + UUID.randomUUID().toString().substring(0, 8);
        String payloadUrl = "/api/v1/github/webhooks/receiver/" + repositoryId;

        GitHubWebhook webhook = GitHubWebhook.builder()
                .repositoryId(repositoryId)
                .webhookId(webhookId)
                .payloadUrl(payloadUrl)
                .secret(secret != null ? secret : UUID.randomUUID().toString())
                .events(events != null ? events : List.of("push", "pull_request", "issues", "release", "deployment", "create", "delete"))
                .active(true)
                .build();

        GitHubWebhook saved = webhookRepository.save(webhook);
        return mapToDTO(saved);
    }

    public List<GitHubWebhookDTO> getWebhooksByRepository(String repositoryId) {
        return webhookRepository.findByRepositoryId(repositoryId).stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    public void deleteWebhook(String id) {
        GitHubWebhook webhook = webhookRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Webhook not found: " + id));
        webhookRepository.delete(webhook);
    }

    public boolean validateSignature(String payload, String signature, String secret) {
        if (signature == null || secret == null || !signature.startsWith("sha256=")) {
            return false;
        }
        try {
            Mac mac = Mac.getInstance("HmacSHA256");
            SecretKeySpec secretKey = new SecretKeySpec(secret.getBytes(StandardCharsets.UTF_8), "HmacSHA256");
            mac.init(secretKey);
            byte[] hash = mac.doFinal(payload.getBytes(StandardCharsets.UTF_8));
            String expectedSignature = "sha256=" + HexFormat.of().formatHex(hash);
            return MessageDigest.isEqual(signature.getBytes(StandardCharsets.UTF_8), expectedSignature.getBytes(StandardCharsets.UTF_8));
        } catch (Exception e) {
            log.error("Signature validation failed", e);
            return false;
        }
    }

    public void processWebhookEvent(String repositoryId, String eventType, String payload) {
        log.info("Processing GitHub webhook event '{}' for repo: {}", eventType, repositoryId);

        List<GitHubWebhook> webhooks = webhookRepository.findByRepositoryId(repositoryId);
        webhooks.forEach(w -> {
            w.setLastTriggeredAt(Instant.now());
            webhookRepository.save(w);
        });

        switch (eventType.toLowerCase()) {
            case "push":
                handlePushEvent(repositoryId, payload);
                break;
            case "pull_request":
                handlePullRequestEvent(repositoryId, payload);
                break;
            case "issues":
            case "issue":
                handleIssueEvent(repositoryId, payload);
                break;
            case "release":
                handleReleaseEvent(repositoryId, payload);
                break;
            case "deployment":
                handleDeploymentEvent(repositoryId, payload);
                break;
            case "create":
                handleBranchCreateEvent(repositoryId, payload);
                break;
            case "delete":
                handleBranchDeleteEvent(repositoryId, payload);
                break;
            default:
                log.info("Unhandled webhook event type: {}", eventType);
        }
    }

    private void handlePushEvent(String repositoryId, String payload) {
        GitHubCommit commit = GitHubCommit.builder()
                .repositoryId(repositoryId)
                .commitSha(UUID.randomUUID().toString().replace("-", ""))
                .message("Webhook push update")
                .commitTime(Instant.now())
                .build();
        commitRepository.save(commit);
    }

    private void handlePullRequestEvent(String repositoryId, String payload) {
        log.info("Handling PR webhook event for repo: {}", repositoryId);
    }

    private void handleIssueEvent(String repositoryId, String payload) {
        log.info("Handling Issue webhook event for repo: {}", repositoryId);
    }

    private void handleReleaseEvent(String repositoryId, String payload) {
        log.info("Handling Release webhook event for repo: {}", repositoryId);
    }

    private void handleDeploymentEvent(String repositoryId, String payload) {
        log.info("Handling Deployment webhook event for repo: {}", repositoryId);
    }

    private void handleBranchCreateEvent(String repositoryId, String payload) {
        log.info("Handling Branch Create webhook event for repo: {}", repositoryId);
    }

    private void handleBranchDeleteEvent(String repositoryId, String payload) {
        log.info("Handling Branch Delete webhook event for repo: {}", repositoryId);
    }

    private GitHubWebhookDTO mapToDTO(GitHubWebhook webhook) {
        return GitHubWebhookDTO.builder()
                .id(webhook.getId())
                .repositoryId(webhook.getRepositoryId())
                .webhookId(webhook.getWebhookId())
                .payloadUrl(webhook.getPayloadUrl())
                .secret(webhook.getSecret() != null ? "******" : null)
                .events(webhook.getEvents())
                .active(webhook.isActive())
                .lastTriggeredAt(webhook.getLastTriggeredAt())
                .createdAt(webhook.getCreatedAt())
                .build();
    }
}
