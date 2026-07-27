package com.techknife.project.workflow.service;

import com.techknife.project.entity.Project;
import com.techknife.project.entity.Task;
import com.techknife.project.entity.TaskStatus;
import com.techknife.project.repository.ProjectRepository;
import com.techknife.project.repository.TaskRepository;
import com.techknife.timetracking.entity.Timesheet;
import com.techknife.timetracking.repository.TimesheetRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProjectWorkflowService {

    private final TaskRepository taskRepository;
    private final ProjectRepository projectRepository;
    private final TimesheetRepository timesheetRepository;

    public int autoCloseCompletedTasks(String projectId) {
        List<Task> tasks = taskRepository.findByProjectId(projectId);
        int closedCount = 0;
        for (Task t : tasks) {
            if (t.getTaskStatus() == TaskStatus.IN_REVIEW) {
                t.setTaskStatus(TaskStatus.COMPLETED);
                taskRepository.save(t);
                closedCount++;
            }
        }
        return closedCount;
    }

    public List<Timesheet> checkPendingTimesheetsForReminders() {
        return timesheetRepository.findByStatus("DRAFT");
    }

    public List<Task> checkOverdueTasks(String projectId) {
        List<Task> tasks = taskRepository.findByProjectId(projectId);
        LocalDate today = LocalDate.now();
        return tasks.stream()
                .filter(t -> t.getDueDate() != null && t.getDueDate().isBefore(today) && t.getTaskStatus() != TaskStatus.COMPLETED)
                .toList();
    }

    public boolean checkProjectDeadlineAlert(String projectId) {
        Project project = projectRepository.findById(projectId).orElse(null);
        if (project != null && project.getEndDate() != null) {
            return project.getEndDate().isBefore(LocalDate.now().plusDays(7));
        }
        return false;
    }
}
