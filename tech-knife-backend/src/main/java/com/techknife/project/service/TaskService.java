package com.techknife.project.service;

import com.techknife.employee.repository.EmployeeRepository;
import com.techknife.project.dto.SubTaskDTO;
import com.techknife.project.dto.TaskRequestDTO;
import com.techknife.project.dto.TaskResponseDTO;
import com.techknife.project.entity.*;
import com.techknife.project.repository.MilestoneRepository;
import com.techknife.project.repository.ProjectRepository;
import com.techknife.project.repository.TaskRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class TaskService {

    private final TaskRepository taskRepository;
    private final ProjectRepository projectRepository;
    private final MilestoneRepository milestoneRepository;
    private final EmployeeRepository employeeRepository;

    public TaskResponseDTO createTask(TaskRequestDTO request) {
        Project project = projectRepository.findById(request.getProjectId())
                .orElseThrow(() -> new NoSuchElementException("Project not found with ID: " + request.getProjectId()));

        String taskNum = generateTaskNumber(project);

        String assigneeName = resolveEmployeeName(request.getAssignedEmployeeId());
        String reviewerName = resolveEmployeeName(request.getReviewerId());

        Task task = Task.builder()
                .taskNumber(taskNum)
                .projectId(project.getId())
                .title(request.getTitle())
                .description(request.getDescription())
                .priority(request.getPriority() != null ? request.getPriority() : TaskPriority.MEDIUM)
                .status(request.getStatus() != null ? request.getStatus() : TaskStatus.TODO)
                .storyPoints(request.getStoryPoints() != null ? request.getStoryPoints() : 1)
                .estimatedHours(request.getEstimatedHours() != null ? request.getEstimatedHours() : 0.0)
                .assignedEmployeeId(request.getAssignedEmployeeId())
                .assignedEmployeeName(assigneeName)
                .reviewerId(request.getReviewerId())
                .reviewerName(reviewerName)
                .milestoneId(request.getMilestoneId())
                .parentTaskId(request.getParentTaskId())
                .dueDate(request.getDueDate())
                .completionPercentage(request.getCompletionPercentage() != null ? request.getCompletionPercentage() : 0.0)
                .labels(request.getLabels() != null ? request.getLabels() : new ArrayList<>())
                .checklist(request.getChecklist() != null ? request.getChecklist() : new ArrayList<>())
                .subtasks(request.getSubtasks() != null ? request.getSubtasks() : new ArrayList<>())
                .build();

        Task saved = taskRepository.save(task);
        return mapToResponseDTO(saved);
    }

    public TaskResponseDTO updateTask(String id, TaskRequestDTO request) {
        Task task = getTaskEntity(id);

        task.setTitle(request.getTitle());
        task.setDescription(request.getDescription());
        if (request.getPriority() != null) task.setPriority(request.getPriority());
        if (request.getStatus() != null) {
            task.setStatus(request.getStatus());
            if (request.getStatus() == TaskStatus.DONE) {
                task.setCompletionPercentage(100.0);
            }
        }
        if (request.getStoryPoints() != null) task.setStoryPoints(request.getStoryPoints());
        if (request.getEstimatedHours() != null) task.setEstimatedHours(request.getEstimatedHours());

        if (request.getAssignedEmployeeId() != null && !request.getAssignedEmployeeId().equals(task.getAssignedEmployeeId())) {
            task.setAssignedEmployeeId(request.getAssignedEmployeeId());
            task.setAssignedEmployeeName(resolveEmployeeName(request.getAssignedEmployeeId()));
        }

        if (request.getReviewerId() != null && !request.getReviewerId().equals(task.getReviewerId())) {
            task.setReviewerId(request.getReviewerId());
            task.setReviewerName(resolveEmployeeName(request.getReviewerId()));
        }

        task.setMilestoneId(request.getMilestoneId());
        task.setParentTaskId(request.getParentTaskId());
        task.setDueDate(request.getDueDate());
        if (request.getCompletionPercentage() != null) task.setCompletionPercentage(request.getCompletionPercentage());

        if (request.getLabels() != null) task.setLabels(request.getLabels());
        if (request.getChecklist() != null) task.setChecklist(request.getChecklist());

        Task updated = taskRepository.save(task);
        return mapToResponseDTO(updated);
    }

    public TaskResponseDTO assignTask(String id, String employeeId, String reviewerId) {
        Task task = getTaskEntity(id);
        if (employeeId != null) {
            task.setAssignedEmployeeId(employeeId);
            task.setAssignedEmployeeName(resolveEmployeeName(employeeId));
        }
        if (reviewerId != null) {
            task.setReviewerId(reviewerId);
            task.setReviewerName(resolveEmployeeName(reviewerId));
        }
        Task saved = taskRepository.save(task);
        return mapToResponseDTO(saved);
    }

    public TaskResponseDTO getTaskById(String id) {
        Task task = getTaskEntity(id);
        return mapToResponseDTO(task);
    }

    public List<TaskResponseDTO> getTasks(String projectId, TaskStatus status, TaskPriority priority, String assignedId, String milestoneId) {
        List<Task> tasks;
        if (projectId != null && !projectId.isBlank()) {
            if (status != null) {
                tasks = taskRepository.findByProjectIdAndStatus(projectId, status);
            } else {
                tasks = taskRepository.findByProjectId(projectId);
            }
        } else if (assignedId != null && !assignedId.isBlank()) {
            tasks = taskRepository.findByAssignedEmployeeId(assignedId);
        } else if (milestoneId != null && !milestoneId.isBlank()) {
            tasks = taskRepository.findByMilestoneId(milestoneId);
        } else {
            tasks = taskRepository.findAll();
        }

        if (priority != null) {
            tasks = tasks.stream().filter(t -> t.getPriority() == priority).collect(Collectors.toList());
        }

        return tasks.stream().map(this::mapToResponseDTO).collect(Collectors.toList());
    }

    public void deleteTask(String id) {
        Task task = getTaskEntity(id);
        taskRepository.delete(task);
    }

    // SubTask Operations
    public TaskResponseDTO addSubTask(String taskId, SubTaskDTO subTaskDTO) {
        Task task = getTaskEntity(taskId);

        String assigneeName = resolveEmployeeName(subTaskDTO.getAssignedEmployeeId());
        SubTask subTask = SubTask.builder()
                .id(UUID.randomUUID().toString())
                .title(subTaskDTO.getTitle())
                .status(subTaskDTO.getStatus() != null ? subTaskDTO.getStatus() : TaskStatus.TODO)
                .assignedEmployeeId(subTaskDTO.getAssignedEmployeeId())
                .assignedEmployeeName(assigneeName)
                .completed(subTaskDTO.isCompleted())
                .createdAt(Instant.now())
                .updatedAt(Instant.now())
                .build();

        task.getSubtasks().add(subTask);
        recalculateSubtaskProgress(task);
        Task saved = taskRepository.save(task);
        return mapToResponseDTO(saved);
    }

    public TaskResponseDTO updateSubTask(String taskId, String subTaskId, SubTaskDTO subTaskDTO) {
        Task task = getTaskEntity(taskId);

        SubTask subTask = task.getSubtasks().stream()
                .filter(s -> s.getId().equals(subTaskId))
                .findFirst()
                .orElseThrow(() -> new NoSuchElementException("SubTask not found with ID: " + subTaskId));

        subTask.setTitle(subTaskDTO.getTitle());
        if (subTaskDTO.getStatus() != null) subTask.setStatus(subTaskDTO.getStatus());
        subTask.setCompleted(subTaskDTO.isCompleted() || subTask.getStatus() == TaskStatus.DONE);
        if (subTaskDTO.getAssignedEmployeeId() != null) {
            subTask.setAssignedEmployeeId(subTaskDTO.getAssignedEmployeeId());
            subTask.setAssignedEmployeeName(resolveEmployeeName(subTaskDTO.getAssignedEmployeeId()));
        }
        subTask.setUpdatedAt(Instant.now());

        recalculateSubtaskProgress(task);
        Task saved = taskRepository.save(task);
        return mapToResponseDTO(saved);
    }

    public TaskResponseDTO deleteSubTask(String taskId, String subTaskId) {
        Task task = getTaskEntity(taskId);
        task.getSubtasks().removeIf(s -> s.getId().equals(subTaskId));
        recalculateSubtaskProgress(task);
        Task saved = taskRepository.save(task);
        return mapToResponseDTO(saved);
    }

    public Task getTaskEntity(String id) {
        return taskRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Task not found with ID: " + id));
    }

    private void recalculateSubtaskProgress(Task task) {
        if (task.getSubtasks() != null && !task.getSubtasks().isEmpty()) {
            long completed = task.getSubtasks().stream().filter(SubTask::isCompleted).count();
            double pct = ((double) completed / task.getSubtasks().size()) * 100.0;
            task.setCompletionPercentage(Math.round(pct * 10.0) / 10.0);
            if (completed == task.getSubtasks().size()) {
                task.setStatus(TaskStatus.DONE);
            }
        }
    }

    private String generateTaskNumber(Project project) {
        long count = taskRepository.countByProjectId(project.getId()) + 1;
        String prefix = project.getShortName() != null && !project.getShortName().isBlank()
                ? project.getShortName().toUpperCase()
                : project.getProjectCode().toUpperCase();

        String code = prefix + "-T" + count;
        while (taskRepository.existsByTaskNumber(code)) {
            count++;
            code = prefix + "-T" + count;
        }
        return code;
    }

    private String resolveEmployeeName(String employeeId) {
        if (employeeId == null || employeeId.isBlank()) return null;
        return employeeRepository.findByEmployeeId(employeeId)
                .map(e -> e.getFirstName() + " " + e.getLastName())
                .orElse(employeeId);
    }

    private TaskResponseDTO mapToResponseDTO(Task task) {
        String projectName = projectRepository.findById(task.getProjectId())
                .map(Project::getProjectName).orElse(null);

        String milestoneTitle = null;
        if (task.getMilestoneId() != null) {
            milestoneTitle = milestoneRepository.findById(task.getMilestoneId())
                    .map(Milestone::getTitle).orElse(null);
        }

        int totalSub = task.getSubtasks() != null ? task.getSubtasks().size() : 0;
        int completedSub = task.getSubtasks() != null
                ? (int) task.getSubtasks().stream().filter(SubTask::isCompleted).count()
                : 0;

        return TaskResponseDTO.builder()
                .id(task.getId())
                .taskNumber(task.getTaskNumber())
                .projectId(task.getProjectId())
                .projectName(projectName)
                .title(task.getTitle())
                .description(task.getDescription())
                .priority(task.getPriority())
                .status(task.getStatus())
                .storyPoints(task.getStoryPoints())
                .estimatedHours(task.getEstimatedHours())
                .assignedEmployeeId(task.getAssignedEmployeeId())
                .assignedEmployeeName(task.getAssignedEmployeeName())
                .reviewerId(task.getReviewerId())
                .reviewerName(task.getReviewerName())
                .milestoneId(task.getMilestoneId())
                .milestoneTitle(milestoneTitle)
                .parentTaskId(task.getParentTaskId())
                .dueDate(task.getDueDate())
                .completionPercentage(task.getCompletionPercentage())
                .labels(task.getLabels())
                .checklist(task.getChecklist())
                .subtasks(task.getSubtasks())
                .totalSubtasks(totalSub)
                .completedSubtasks(completedSub)
                .createdAt(task.getCreatedAt())
                .updatedAt(task.getUpdatedAt())
                .createdBy(task.getCreatedBy())
                .updatedBy(task.getUpdatedBy())
                .build();
    }
}
