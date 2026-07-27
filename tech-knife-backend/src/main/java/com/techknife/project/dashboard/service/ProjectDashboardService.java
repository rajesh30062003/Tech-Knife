package com.techknife.project.dashboard.service;

import com.techknife.github.repository.GitHubRepositoryRepository;
import com.techknife.github.service.GitHubRepositoryService;
import com.techknife.project.dashboard.dto.ProjectDashboardSummaryDTO;
import com.techknife.project.entity.*;
import com.techknife.project.repository.*;
import com.techknife.project.resource.service.ResourceAllocationService;
import com.techknife.project.sprint.entity.Sprint;
import com.techknife.project.sprint.repository.SprintRepository;
import com.techknife.project.sprint.service.SprintService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProjectDashboardService {

    private final ProjectRepository projectRepository;
    private final MilestoneRepository milestoneRepository;
    private final TaskRepository taskRepository;
    private final SprintRepository sprintRepository;
    private final SprintService sprintService;
    private final ResourceAllocationService resourceAllocationService;
    private final GitHubRepositoryService gitHubRepositoryService;

    public ProjectDashboardSummaryDTO getProjectDashboardSummary(String projectId) {
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new NoSuchElementException("Project not found: " + projectId));

        List<Milestone> milestones = milestoneRepository.findByProjectId(projectId);
        List<Task> tasks = taskRepository.findByProjectId(projectId);

        int totalMilestones = milestones.size();
        int completedMilestones = (int) milestones.stream()
                .filter(m -> m.getMilestoneStatus() != null && "COMPLETED".equalsIgnoreCase(m.getMilestoneStatus().name()))
                .count();

        double progressPct = tasks.isEmpty() ? 0.0 :
                (tasks.stream().filter(t -> t.getTaskStatus() != null && "COMPLETED".equalsIgnoreCase(t.getTaskStatus().name())).count() * 100.0) / tasks.size();

        List<Task> pendingTasks = tasks.stream()
                .filter(t -> t.getTaskStatus() != null && !"COMPLETED".equalsIgnoreCase(t.getTaskStatus().name()))
                .limit(10)
                .collect(Collectors.toList());

        List<Task> blockedTasks = tasks.stream()
                .filter(t -> t.getTaskStatus() != null && "BLOCKED".equalsIgnoreCase(t.getTaskStatus().name()))
                .collect(Collectors.toList());

        Optional<Sprint> activeSprint = sprintRepository.findByProjectIdAndStatus(projectId, "ACTIVE");

        String health = "GOOD";
        if (!blockedTasks.isEmpty()) {
            health = "AT_RISK";
        }

        return ProjectDashboardSummaryDTO.builder()
                .projectId(projectId)
                .projectName(project.getProjectName())
                .projectHealth(health)
                .budgetTotal(project.getBudget() != null ? project.getBudget() : 0.0)
                .budgetUsed(project.getSpentAmount() != null ? project.getSpentAmount() : 0.0)
                .budgetUsagePercentage(project.getBudget() != null && project.getBudget() > 0 ? (project.getSpentAmount() * 100.0) / project.getBudget() : 0.0)
                .progressPercentage(progressPct)
                .totalMilestones(totalMilestones)
                .completedMilestones(completedMilestones)
                .upcomingDeadlines(milestones.stream().filter(m -> m.getMilestoneStatus() != null && !"COMPLETED".equalsIgnoreCase(m.getMilestoneStatus().name())).limit(5).collect(Collectors.toList()))
                .pendingTasks(pendingTasks)
                .blockedTasks(blockedTasks)
                .activeSprint(activeSprint.map(sprintService::mapToDTO).orElse(null))
                .githubActivity(gitHubRepositoryService.getRepositoriesByProject(projectId))
                .resourceUtilization(resourceAllocationService.getAllocationsByProject(projectId))
                .build();
    }
}
