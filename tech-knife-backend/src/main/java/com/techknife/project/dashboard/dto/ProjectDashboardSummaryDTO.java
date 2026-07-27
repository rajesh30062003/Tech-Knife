package com.techknife.project.dashboard.dto;

import com.techknife.github.dto.GitHubRepositoryDTO;
import com.techknife.project.entity.Milestone;
import com.techknife.project.entity.Task;
import com.techknife.project.resource.dto.ResourceAllocationDTO;
import com.techknife.project.sprint.dto.SprintDTO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProjectDashboardSummaryDTO {

    private String projectId;
    private String projectName;
    private String projectHealth; // EXCELLENT, GOOD, AT_RISK, CRITICAL
    private Double budgetTotal;
    private Double budgetUsed;
    private Double budgetUsagePercentage;
    private Double progressPercentage;
    private Integer totalMilestones;
    private Integer completedMilestones;
    private List<Milestone> upcomingDeadlines;
    private List<Task> pendingTasks;
    private List<Task> blockedTasks;
    private SprintDTO activeSprint;
    private List<GitHubRepositoryDTO> githubActivity;
    private List<ResourceAllocationDTO> resourceUtilization;
}
