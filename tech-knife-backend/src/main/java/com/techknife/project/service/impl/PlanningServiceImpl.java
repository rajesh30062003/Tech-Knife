package com.techknife.project.service.impl;

import com.techknife.project.entity.PlanningDocument;
import com.techknife.project.entity.PlanningVersion;
import com.techknife.project.repository.PlanningDocumentRepository;
import com.techknife.project.repository.PlanningVersionRepository;
import com.techknife.project.service.PlanningService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class PlanningServiceImpl implements PlanningService {

    private final PlanningDocumentRepository planningDocumentRepository;
    private final PlanningVersionRepository planningVersionRepository;
    private final SimpMessagingTemplate messagingTemplate;

    @Override
    public PlanningDocument getOrCreatePlanningDocument(String projectId) {
        return planningDocumentRepository.findByProjectId(projectId)
                .orElseGet(() -> {
                    log.info("==== Creating Initial Planning Document for Project: {} ====", projectId);
                    PlanningDocument initialDoc = PlanningDocument.builder()
                            .projectId(projectId)
                            .title("Master Execution & Architecture Plan")
                            .category("Project Plan")
                            .content("# 1. Executive Summary & Goals\nMaster technical architecture and business execution plan.\n\n## Stack:\n- React 18, Spring Boot 3.5, MongoDB Atlas, WebSocket SockJS")
                            .editorType("RICH_TEXT")
                            .diagramJson("[]")
                            .version(1)
                            .isLocked(false)
                            .createdAt(Instant.now())
                            .updatedAt(Instant.now())
                            .build();
                    return planningDocumentRepository.save(initialDoc);
                });
    }

    @Override
    public PlanningDocument saveOrAutoSavePlanningDocument(String projectId, PlanningDocument request) {
        PlanningDocument doc = getOrCreatePlanningDocument(projectId);

        if (Boolean.TRUE.equals(doc.getIsLocked()) && request.getUpdatedBy() != null && !request.getUpdatedBy().equals(doc.getLockedBy())) {
            throw new IllegalStateException("Planning document is currently locked by " + doc.getLockedBy());
        }

        if (request.getTitle() != null && !request.getTitle().isBlank()) doc.setTitle(request.getTitle());
        if (request.getCategory() != null && !request.getCategory().isBlank()) doc.setCategory(request.getCategory());
        if (request.getContent() != null) doc.setContent(request.getContent());
        if (request.getDiagramJson() != null) doc.setDiagramJson(request.getDiagramJson());
        if (request.getUpdatedBy() != null) doc.setUpdatedBy(request.getUpdatedBy());
        if (request.getUpdatedByRole() != null) doc.setUpdatedByRole(request.getUpdatedByRole());

        doc.setVersion(doc.getVersion() + 1);
        doc.setUpdatedAt(Instant.now());

        PlanningDocument savedDoc = planningDocumentRepository.save(doc);

        // Save Version Snapshot in MongoDB Atlas
        PlanningVersion snapshot = PlanningVersion.builder()
                .projectId(projectId)
                .documentId(savedDoc.getId())
                .versionNumber(savedDoc.getVersion())
                .savedBy(request.getUpdatedBy() != null ? request.getUpdatedBy() : "System Auto-Save")
                .savedByRole(request.getUpdatedByRole() != null ? request.getUpdatedByRole() : "Contributor")
                .savedAt(Instant.now())
                .docTitle(savedDoc.getTitle())
                .category(savedDoc.getCategory())
                .content(savedDoc.getContent())
                .diagramJson(savedDoc.getDiagramJson())
                .build();
        planningVersionRepository.save(snapshot);

        // STOMP WebSocket Real-time Broadcast
        try {
            Map<String, Object> wsPayload = new HashMap<>();
            wsPayload.put("event", "PlanningSaved");
            wsPayload.put("projectId", projectId);
            wsPayload.put("version", savedDoc.getVersion());
            wsPayload.put("updatedBy", savedDoc.getUpdatedBy());
            wsPayload.put("updatedAt", savedDoc.getUpdatedAt().toString());
            messagingTemplate.convertAndSend("/topic/project." + projectId, wsPayload);
        } catch (Exception e) {
            log.warn("WebSocket broadcast warning for PlanningSaved: {}", e.getMessage());
        }

        return savedDoc;
    }

    @Override
    public List<PlanningVersion> getPlanningVersions(String projectId) {
        return planningVersionRepository.findByProjectIdOrderByVersionNumberDesc(projectId);
    }

    @Override
    public PlanningDocument restorePlanningVersion(String projectId, String versionId) {
        PlanningVersion version = planningVersionRepository.findById(versionId)
                .orElseThrow(() -> new IllegalArgumentException("Planning Version Snapshot not found: " + versionId));

        PlanningDocument doc = getOrCreatePlanningDocument(projectId);
        doc.setTitle(version.getDocTitle());
        doc.setCategory(version.getCategory());
        doc.setContent(version.getContent());
        if (version.getDiagramJson() != null) doc.setDiagramJson(version.getDiagramJson());
        doc.setVersion(doc.getVersion() + 1);
        doc.setUpdatedAt(Instant.now());

        return planningDocumentRepository.save(doc);
    }

    @Override
    public PlanningDocument lockPlanningDocument(String projectId, String userName) {
        PlanningDocument doc = getOrCreatePlanningDocument(projectId);
        doc.setIsLocked(true);
        doc.setLockedBy(userName);
        doc.setLockedAt(Instant.now());
        return planningDocumentRepository.save(doc);
    }

    @Override
    public PlanningDocument unlockPlanningDocument(String projectId) {
        PlanningDocument doc = getOrCreatePlanningDocument(projectId);
        doc.setIsLocked(false);
        doc.setLockedBy(null);
        doc.setLockedAt(null);
        return planningDocumentRepository.save(doc);
    }
}
