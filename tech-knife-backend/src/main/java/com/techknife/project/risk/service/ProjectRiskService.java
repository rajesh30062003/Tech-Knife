package com.techknife.project.risk.service;

import com.techknife.project.risk.dto.ProjectRiskDTO;
import com.techknife.project.risk.entity.ProjectRisk;
import com.techknife.project.risk.repository.ProjectRiskRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProjectRiskService {

    private final ProjectRiskRepository riskRepository;

    public ProjectRiskDTO createRisk(ProjectRiskDTO dto) {
        ProjectRisk risk = ProjectRisk.builder()
                .projectId(dto.getProjectId())
                .title(dto.getTitle())
                .description(dto.getDescription())
                .impact(dto.getImpact() != null ? dto.getImpact() : "MEDIUM")
                .probability(dto.getProbability() != null ? dto.getProbability() : "MEDIUM")
                .ownerId(dto.getOwnerId())
                .mitigationPlan(dto.getMitigationPlan())
                .status("IDENTIFIED")
                .build();

        ProjectRisk saved = riskRepository.save(risk);
        return mapToDTO(saved);
    }

    public ProjectRiskDTO updateRisk(String riskId, ProjectRiskDTO dto) {
        ProjectRisk risk = riskRepository.findById(riskId)
                .orElseThrow(() -> new NoSuchElementException("Risk not found: " + riskId));

        if (dto.getTitle() != null) risk.setTitle(dto.getTitle());
        if (dto.getDescription() != null) risk.setDescription(dto.getDescription());
        if (dto.getImpact() != null) risk.setImpact(dto.getImpact());
        if (dto.getProbability() != null) risk.setProbability(dto.getProbability());
        if (dto.getOwnerId() != null) risk.setOwnerId(dto.getOwnerId());
        if (dto.getMitigationPlan() != null) risk.setMitigationPlan(dto.getMitigationPlan());
        if (dto.getStatus() != null) risk.setStatus(dto.getStatus());

        ProjectRisk saved = riskRepository.save(risk);
        return mapToDTO(saved);
    }

    public List<ProjectRiskDTO> getRisksByProject(String projectId) {
        return riskRepository.findByProjectId(projectId).stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    public void deleteRisk(String riskId) {
        ProjectRisk risk = riskRepository.findById(riskId)
                .orElseThrow(() -> new NoSuchElementException("Risk not found: " + riskId));
        riskRepository.delete(risk);
    }

    public ProjectRiskDTO mapToDTO(ProjectRisk r) {
        return ProjectRiskDTO.builder()
                .id(r.getId())
                .projectId(r.getProjectId())
                .title(r.getTitle())
                .description(r.getDescription())
                .impact(r.getImpact())
                .probability(r.getProbability())
                .ownerId(r.getOwnerId())
                .mitigationPlan(r.getMitigationPlan())
                .status(r.getStatus())
                .createdAt(r.getCreatedAt())
                .updatedAt(r.getUpdatedAt())
                .build();
    }
}
