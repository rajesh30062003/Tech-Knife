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
import java.time.LocalDate;
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
    private final ProjectActivityService activityService;

    public TaskResponseDTO createTask(TaskRequestDTO request) {
        Project project = projectRepository.findById(request.getProjectId())
                .orElseGet(() -> projectRepository.findByProjectCode(request.getProjectId())
                        .orElseGet(() -> {
                            log.info("==== Auto-creating Project Container for Code: {} ====", request.getProjectId());
                            Project p = Project.builder()
                                    .projectCode(request.getProjectId())
                                    .projectName(request.getProjectId())
                                    .shortName(request.getProjectId())
                                    .build();
                            return projectRepository.save(p);
                        }));

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

        try {
            activityService.logActivity(
                    project.getId(), project.getProjectCode(),
                    "Task Created", "TASK",
                    "Created task '" + saved.getTitle() + "' (" + saved.getTaskNumber() + ").",
                    "Task", null, saved.getTitle()
            );
        } catch (Exception e) {
            log.warn("Could not log task creation activity: {}", e.getMessage());
        }

        return mapToResponseDTO(saved);
    }

    public TaskResponseDTO updateTask(String id, TaskRequestDTO request) {
        Task task = getTaskEntity(id);
        TaskStatus oldStatus = task.getStatus();
        String oldAssignee = task.getAssignedEmployeeName();

        if (request.getTitle() != null && !request.getTitle().isBlank()) {
            task.setTitle(request.getTitle());
        }
        if (request.getDescription() != null) task.setDescription(request.getDescription());
        if (request.getPriority() != null) task.setPriority(request.getPriority());
        if (request.getStatus() != null) {
            task.setStatus(request.getStatus());
            if (request.getStatus() == TaskStatus.DONE || request.getStatus() == TaskStatus.COMPLETED) {
                task.setCompletionPercentage(100.0);
                task.setCompletedDate(LocalDate.now());
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

        if (request.getMilestoneId() != null) task.setMilestoneId(request.getMilestoneId());
        if (request.getParentTaskId() != null) task.setParentTaskId(request.getParentTaskId());
        if (request.getDueDate() != null) task.setDueDate(request.getDueDate());
        if (request.getCompletionPercentage() != null) task.setCompletionPercentage(request.getCompletionPercentage());

        if (request.getLabels() != null && !request.getLabels().isEmpty()) task.setLabels(request.getLabels());
        if (request.getChecklist() != null && !request.getChecklist().isEmpty()) task.setChecklist(request.getChecklist());

        Task updated = taskRepository.save(task);

        try {
            if (request.getStatus() != null && request.getStatus() != oldStatus) {
                String actName = (request.getStatus() == TaskStatus.DONE || request.getStatus() == TaskStatus.COMPLETED) ? "Task Completed" : "Task Status Changed";
                activityService.logActivity(
                        updated.getProjectId(), updated.getProjectId(),
                        actName, "TASK",
                        "Task '" + updated.getTitle() + "' status changed from " + oldStatus + " → " + request.getStatus() + ".",
                        "Task Status", String.valueOf(oldStatus), String.valueOf(request.getStatus())
                );
            } else if (request.getAssignedEmployeeId() != null && !request.getAssignedEmployeeId().equals(oldAssignee)) {
                activityService.logActivity(
                        updated.getProjectId(), updated.getProjectId(),
                        "Task Assigned", "TASK",
                        "Task '" + updated.getTitle() + "' assigned to " + updated.getAssignedEmployeeName() + ".",
                        "Assignee", oldAssignee, updated.getAssignedEmployeeName()
                );
            } else {
                activityService.logActivity(
                        updated.getProjectId(), updated.getProjectId(),
                        "Task Updated", "TASK",
                        "Task '" + updated.getTitle() + "' details updated.",
                        "Task Details", null, updated.getTitle()
                );
            }
        } catch (Exception e) {
            log.warn("Could not log task update activity: {}", e.getMessage());
        }

        return mapToResponseDTO(updated);
    }

    public TaskResponseDTO updateTaskStatus(String id, String statusStr) {
        Task task = getTaskEntity(id);
        TaskStatus newStatus = TaskStatus.fromString(statusStr);
        task.setStatus(newStatus);
        if (newStatus == TaskStatus.DONE || newStatus == TaskStatus.COMPLETED) {
            task.setCompletionPercentage(100.0);
            task.setCompletedDate(LocalDate.now());
            Map<String, String> compInfo = new HashMap<>();
            compInfo.put("name", "System Member");
            compInfo.put("role", "Engineer");
            compInfo.put("avatar", "S");
            compInfo.put("timestamp", LocalDate.now().toString());
            task.setCompletedByInfo(compInfo);
        }
        Task saved = taskRepository.save(task);
        return mapToResponseDTO(saved);
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
        try {
            activityService.logActivity(
                    task.getProjectId(), task.getProjectId(),
                    "Task Deleted", "TASK",
                    "Task '" + task.getTitle() + "' was deleted.",
                    "Task", task.getTitle(), null
            );
        } catch (Exception e) {
            log.warn("Could not log task deletion activity: {}", e.getMessage());
        }
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
        String prefix = "TASK";
        if (project.getShortName() != null && !project.getShortName().isBlank()) {
            prefix = project.getShortName().toUpperCase();
        } else if (project.getProjectCode() != null && !project.getProjectCode().isBlank()) {
            prefix = project.getProjectCode().toUpperCase();
        }

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

    private Object resolveCreatorInfo(String createdBy, Object existingCreatedByInfo) {
        if (existingCreatedByInfo != null) {
            if (existingCreatedByInfo instanceof Map) {
                Map<?, ?> map = (Map<?, ?>) existingCreatedByInfo;
                Object nameObj = map.get("name");
                if (nameObj != null && !isRawObjectId(nameObj.toString())) {
                    return existingCreatedByInfo;
                }
            } else {
                return existingCreatedByInfo;
            }
        }

        String resolvedName = "Former User";
        String resolvedRole = "Engineer";

        if (createdBy != null && !createdBy.isBlank()) {
            if (!isRawObjectId(createdBy)) {
                resolvedName = createdBy;
            }
            var empOpt = employeeRepository.findByEmployeeId(createdBy);
            if (empOpt.isEmpty()) {
                empOpt = employeeRepository.findById(createdBy);
            }
            if (empOpt.isPresent()) {
                var emp = empOpt.get();
                resolvedName = emp.getFirstName() + " " + emp.getLastName();
                if (emp.getEmploymentType() != null) {
                    resolvedRole = emp.getEmploymentType().name();
                }
            }
        }

        String avatar = resolvedName.substring(0, 1).toUpperCase();
        Map<String, String> info = new HashMap<>();
        info.put("name", resolvedName);
        info.put("role", resolvedRole);
        info.put("avatar", avatar);
        info.put("timestamp", LocalDate.now().toString());
        return info;
    }

    private boolean isRawObjectId(String str) {
        if (str == null) return false;
        return str.matches("^[0-9a-fA-F]{24}$");
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

        Object creatorInfo = resolveCreatorInfo(task.getCreatedBy(), task.getCreatedByInfo());
        String displayCreatedBy = task.getCreatedBy();
        if (creatorInfo instanceof Map) {
            Object nameObj = ((Map<?, ?>) creatorInfo).get("name");
            if (nameObj != null) {
                displayCreatedBy = nameObj.toString();
            }
        }

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
                .sprint(task.getSprint())
                .epic(task.getEpic())
                .loggedHours(task.getLoggedHours())
                .dependencies(task.getDependencies() != null ? task.getDependencies() : new ArrayList<>())
                .createdByInfo(creatorInfo)
                .completedByInfo(task.getCompletedByInfo())
                .completedDate(task.getCompletedDate())
                .votesCount(task.getVotesCount())
                .isPinned(task.getIsPinned())
                .isWatching(task.getIsWatching())
                .createdAt(task.getCreatedAt())
                .updatedAt(task.getUpdatedAt())
                .createdBy(displayCreatedBy)
                .updatedBy(task.getUpdatedBy())
                .build();
    }
}
