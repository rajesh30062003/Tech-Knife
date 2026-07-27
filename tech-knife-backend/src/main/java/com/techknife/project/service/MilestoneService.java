package com.techknife.project.service;

import com.techknife.project.dto.MilestoneDTO;
import com.techknife.project.entity.Milestone;
import com.techknife.project.entity.MilestoneStatus;
import com.techknife.project.entity.Project;
import com.techknife.project.entity.Task;
import com.techknife.project.entity.TaskStatus;
import com.techknife.project.repository.MilestoneRepository;
import com.techknife.project.repository.ProjectRepository;
import com.techknife.project.repository.TaskRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MilestoneService {

    private final MilestoneRepository milestoneRepository;
    private final ProjectRepository projectRepository;
    private final TaskRepository taskRepository;

    public MilestoneDTO createMilestone(MilestoneDTO dto) {
        Project project = projectRepository.findById(dto.getProjectId())
                .orElseThrow(() -> new NoSuchElementException("Project not found with ID: " + dto.getProjectId()));

        Milestone milestone = Milestone.builder()
                .projectId(project.getId())
                .title(dto.getTitle())
                .description(dto.getDescription())
                .dueDate(dto.getDueDate())
                .completionPercentage(0.0)
                .status(dto.getStatus() != null ? dto.getStatus() : MilestoneStatus.PLANNED)
                .build();

        Milestone saved = milestoneRepository.save(milestone);
        return mapToDTO(saved);
    }

    public MilestoneDTO updateMilestone(String id, MilestoneDTO dto) {
        Milestone milestone = milestoneRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Milestone not found with ID: " + id));

        milestone.setTitle(dto.getTitle());
        milestone.setDescription(dto.getDescription());
        milestone.setDueDate(dto.getDueDate());
        if (dto.getStatus() != null) {
            milestone.setStatus(dto.getStatus());
        }

        recalculateMilestoneProgress(milestone);
        Milestone saved = milestoneRepository.save(milestone);
        return mapToDTO(saved);
    }

    public MilestoneDTO getMilestoneById(String id) {
        Milestone milestone = milestoneRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Milestone not found with ID: " + id));

        recalculateMilestoneProgress(milestone);
        return mapToDTO(milestone);
    }

    public List<MilestoneDTO> getMilestonesByProject(String projectId) {
        List<Milestone> milestones = milestoneRepository.findByProjectId(projectId);
        milestones.forEach(this::recalculateMilestoneProgress);
        return milestones.stream().map(this::mapToDTO).collect(Collectors.toList());
    }

    public MilestoneDTO completeMilestone(String id) {
        Milestone milestone = milestoneRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Milestone not found with ID: " + id));

        milestone.setStatus(MilestoneStatus.COMPLETED);
        milestone.setCompletionPercentage(100.0);
        Milestone saved = milestoneRepository.save(milestone);
        return mapToDTO(saved);
    }

    public void deleteMilestone(String id) {
        Milestone milestone = milestoneRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Milestone not found with ID: " + id));
        milestoneRepository.delete(milestone);
    }

    private void recalculateMilestoneProgress(Milestone milestone) {
        List<Task> milestoneTasks = taskRepository.findByMilestoneId(milestone.getId());
        if (!milestoneTasks.isEmpty()) {
            long completed = milestoneTasks.stream().filter(t -> t.getStatus() == TaskStatus.DONE).count();
            double pct = ((double) completed / milestoneTasks.size()) * 100.0;
            milestone.setCompletionPercentage(Math.round(pct * 10.0) / 10.0);
            if (completed == milestoneTasks.size() && milestone.getStatus() != MilestoneStatus.COMPLETED) {
                milestone.setStatus(MilestoneStatus.COMPLETED);
            }
        }
    }

    private MilestoneDTO mapToDTO(Milestone milestone) {
        return MilestoneDTO.builder()
                .id(milestone.getId())
                .projectId(milestone.getProjectId())
                .title(milestone.getTitle())
                .description(milestone.getDescription())
                .dueDate(milestone.getDueDate())
                .completionPercentage(milestone.getCompletionPercentage())
                .status(milestone.getStatus())
                .createdAt(milestone.getCreatedAt())
                .updatedAt(milestone.getUpdatedAt())
                .build();
    }
}
