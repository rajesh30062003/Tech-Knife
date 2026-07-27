package com.techknife.project.sprint.service;

import com.techknife.project.entity.Task;
import com.techknife.project.repository.TaskRepository;
import com.techknife.project.sprint.dto.*;
import com.techknife.project.sprint.entity.*;
import com.techknife.project.sprint.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class SprintService {

    private final SprintRepository sprintRepository;
    private final SprintReviewRepository sprintReviewRepository;
    private final SprintRetrospectiveRepository sprintRetrospectiveRepository;
    private final TaskRepository taskRepository;

    public SprintDTO createSprint(SprintDTO dto) {
        Sprint sprint = Sprint.builder()
                .projectId(dto.getProjectId())
                .sprintName(dto.getSprintName())
                .sprintGoal(dto.getSprintGoal())
                .startDate(dto.getStartDate())
                .endDate(dto.getEndDate())
                .status("PLANNED")
                .totalStoryPoints(dto.getTotalStoryPoints() != null ? dto.getTotalStoryPoints() : 0)
                .completedStoryPoints(0)
                .taskIds(dto.getTaskIds() != null ? dto.getTaskIds() : new ArrayList<>())
                .build();

        Sprint saved = sprintRepository.save(sprint);
        return mapToDTO(saved);
    }

    public SprintDTO startSprint(String sprintId) {
        Sprint sprint = sprintRepository.findById(sprintId)
                .orElseThrow(() -> new NoSuchElementException("Sprint not found: " + sprintId));

        sprint.setStatus("ACTIVE");
        Sprint saved = sprintRepository.save(sprint);
        return mapToDTO(saved);
    }

    public SprintDTO closeSprint(String sprintId) {
        Sprint sprint = sprintRepository.findById(sprintId)
                .orElseThrow(() -> new NoSuchElementException("Sprint not found: " + sprintId));

        List<Task> tasks = taskRepository.findAllById(sprint.getTaskIds());
        int completedStoryPoints = tasks.stream()
                .filter(t -> t.getTaskStatus() != null && "COMPLETED".equalsIgnoreCase(t.getTaskStatus().name()))
                .mapToInt(t -> t.getEstimatedHours() != null ? t.getEstimatedHours().intValue() : 1)
                .sum();

        sprint.setStatus("COMPLETED");
        sprint.setCompletedStoryPoints(completedStoryPoints);

        Sprint saved = sprintRepository.save(sprint);
        return mapToDTO(saved);
    }

    public SprintDTO assignTasksToSprint(String sprintId, List<String> taskIds) {
        Sprint sprint = sprintRepository.findById(sprintId)
                .orElseThrow(() -> new NoSuchElementException("Sprint not found: " + sprintId));

        List<String> current = sprint.getTaskIds() != null ? sprint.getTaskIds() : new ArrayList<>();
        for (String id : taskIds) {
            if (!current.contains(id)) {
                current.add(id);
            }
        }
        sprint.setTaskIds(current);

        List<Task> tasks = taskRepository.findAllById(current);
        int totalStoryPoints = tasks.stream()
                .mapToInt(t -> t.getEstimatedHours() != null ? t.getEstimatedHours().intValue() : 1)
                .sum();
        sprint.setTotalStoryPoints(totalStoryPoints);

        Sprint saved = sprintRepository.save(sprint);
        return mapToDTO(saved);
    }

    public SprintBoardDTO getSprintBoard(String sprintId) {
        Sprint sprint = sprintRepository.findById(sprintId)
                .orElseThrow(() -> new NoSuchElementException("Sprint not found: " + sprintId));

        List<Task> tasks = taskRepository.findAllById(sprint.getTaskIds() != null ? sprint.getTaskIds() : List.of());

        Map<String, List<Task>> tasksByStatus = tasks.stream()
                .collect(Collectors.groupingBy(t -> t.getTaskStatus() != null ? t.getTaskStatus().name() : "TODO"));

        int totalTasks = tasks.size();
        int completedTasks = (int) tasks.stream()
                .filter(t -> t.getTaskStatus() != null && "COMPLETED".equalsIgnoreCase(t.getTaskStatus().name()))
                .count();

        double completionPct = totalTasks > 0 ? (completedTasks * 100.0) / totalTasks : 0.0;

        return SprintBoardDTO.builder()
                .sprint(mapToDTO(sprint))
                .tasksByStatus(tasksByStatus)
                .totalTasks(totalTasks)
                .completedTasks(completedTasks)
                .completionPercentage(completionPct)
                .build();
    }

    public List<SprintDTO> getSprintsByProject(String projectId) {
        return sprintRepository.findByProjectId(projectId).stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    public SprintReviewDTO saveSprintReview(SprintReviewDTO dto) {
        SprintReview review = SprintReview.builder()
                .sprintId(dto.getSprintId())
                .demonstratedFeatures(dto.getDemonstratedFeatures())
                .stakeholderFeedback(dto.getStakeholderFeedback())
                .reviewedBy(dto.getReviewedBy())
                .reviewDate(Instant.now())
                .build();

        SprintReview saved = sprintReviewRepository.save(review);
        return mapReviewToDTO(saved);
    }

    public SprintRetrospectiveDTO saveSprintRetrospective(SprintRetrospectiveDTO dto) {
        SprintRetrospective retro = SprintRetrospective.builder()
                .sprintId(dto.getSprintId())
                .whatWentWell(dto.getWhatWentWell() != null ? dto.getWhatWentWell() : List.of())
                .whatCouldBeImproved(dto.getWhatCouldBeImproved() != null ? dto.getWhatCouldBeImproved() : List.of())
                .actionItems(dto.getActionItems() != null ? dto.getActionItems() : List.of())
                .build();

        SprintRetrospective saved = sprintRetrospectiveRepository.save(retro);
        return mapRetroToDTO(saved);
    }

    public SprintDTO mapToDTO(Sprint sprint) {
        return SprintDTO.builder()
                .id(sprint.getId())
                .projectId(sprint.getProjectId())
                .sprintName(sprint.getSprintName())
                .sprintGoal(sprint.getSprintGoal())
                .startDate(sprint.getStartDate())
                .endDate(sprint.getEndDate())
                .status(sprint.getStatus())
                .totalStoryPoints(sprint.getTotalStoryPoints())
                .completedStoryPoints(sprint.getCompletedStoryPoints())
                .taskIds(sprint.getTaskIds())
                .createdAt(sprint.getCreatedAt())
                .updatedAt(sprint.getUpdatedAt())
                .build();
    }

    private SprintReviewDTO mapReviewToDTO(SprintReview review) {
        return SprintReviewDTO.builder()
                .id(review.getId())
                .sprintId(review.getSprintId())
                .demonstratedFeatures(review.getDemonstratedFeatures())
                .stakeholderFeedback(review.getStakeholderFeedback())
                .reviewedBy(review.getReviewedBy())
                .reviewDate(review.getReviewDate())
                .createdAt(review.getCreatedAt())
                .build();
    }

    private SprintRetrospectiveDTO mapRetroToDTO(SprintRetrospective retro) {
        return SprintRetrospectiveDTO.builder()
                .id(retro.getId())
                .sprintId(retro.getSprintId())
                .whatWentWell(retro.getWhatWentWell())
                .whatCouldBeImproved(retro.getWhatCouldBeImproved())
                .actionItems(retro.getActionItems())
                .createdAt(retro.getCreatedAt())
                .build();
    }
}
