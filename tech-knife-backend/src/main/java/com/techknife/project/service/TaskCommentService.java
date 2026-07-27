package com.techknife.project.service;

import com.techknife.employee.repository.EmployeeRepository;
import com.techknife.project.dto.TaskCommentDTO;
import com.techknife.project.entity.Task;
import com.techknife.project.entity.TaskComment;
import com.techknife.project.repository.TaskCommentRepository;
import com.techknife.project.repository.TaskRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TaskCommentService {

    private final TaskCommentRepository commentRepository;
    private final TaskRepository taskRepository;
    private final EmployeeRepository employeeRepository;

    public TaskCommentDTO createComment(TaskCommentDTO dto, String authorId) {
        Task task = taskRepository.findById(dto.getTaskId())
                .orElseThrow(() -> new NoSuchElementException("Task not found with ID: " + dto.getTaskId()));

        String authorName = resolveEmployeeName(authorId);

        TaskComment comment = TaskComment.builder()
                .taskId(task.getId())
                .authorId(authorId)
                .authorName(authorName)
                .content(dto.getContent())
                .mentions(dto.getMentions() != null ? dto.getMentions() : new ArrayList<>())
                .parentCommentId(dto.getParentCommentId())
                .createdAt(Instant.now())
                .build();

        TaskComment saved = commentRepository.save(comment);
        return mapToDTO(saved);
    }

    public List<TaskCommentDTO> getCommentsByTask(String taskId) {
        List<TaskComment> comments = commentRepository.findByTaskIdOrderByCreatedAtAsc(taskId);
        return comments.stream().map(this::mapToDTO).collect(Collectors.toList());
    }

    public void deleteComment(String id) {
        TaskComment comment = commentRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Comment not found with ID: " + id));
        commentRepository.delete(comment);
    }

    private String resolveEmployeeName(String employeeId) {
        if (employeeId == null || employeeId.isBlank()) return null;
        return employeeRepository.findByEmployeeId(employeeId)
                .map(e -> e.getFirstName() + " " + e.getLastName())
                .orElse(employeeId);
    }

    private TaskCommentDTO mapToDTO(TaskComment comment) {
        return TaskCommentDTO.builder()
                .id(comment.getId())
                .taskId(comment.getTaskId())
                .authorId(comment.getAuthorId())
                .authorName(comment.getAuthorName())
                .content(comment.getContent())
                .mentions(comment.getMentions())
                .parentCommentId(comment.getParentCommentId())
                .createdAt(comment.getCreatedAt())
                .updatedAt(comment.getUpdatedAt())
                .build();
    }
}
