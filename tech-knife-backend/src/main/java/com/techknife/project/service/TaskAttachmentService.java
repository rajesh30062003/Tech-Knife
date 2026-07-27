package com.techknife.project.service;

import com.techknife.employee.repository.EmployeeRepository;
import com.techknife.project.dto.TaskAttachmentDTO;
import com.techknife.project.entity.Task;
import com.techknife.project.entity.TaskAttachment;
import com.techknife.project.repository.TaskAttachmentRepository;
import com.techknife.project.repository.TaskRepository;
import com.techknife.storage.FileStorageService;
import com.techknife.storage.FileUploadResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.time.Instant;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TaskAttachmentService {

    private final TaskAttachmentRepository attachmentRepository;
    private final TaskRepository taskRepository;
    private final EmployeeRepository employeeRepository;
    private final FileStorageService fileStorageService;

    public TaskAttachmentDTO uploadAttachment(String taskId, MultipartFile file, String uploadedBy) {
        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new NoSuchElementException("Task not found with ID: " + taskId));

        String folder = "projects/" + task.getProjectId() + "/tasks/" + task.getTaskNumber();
        FileUploadResponse uploadResp = fileStorageService.uploadDocument(file, folder);

        String uploaderName = resolveEmployeeName(uploadedBy);

        TaskAttachment attachment = TaskAttachment.builder()
                .taskId(task.getId())
                .projectId(task.getProjectId())
                .fileName(file.getOriginalFilename())
                .fileUrl(uploadResp.getSecureUrl())
                .fileType(file.getContentType())
                .fileSize(file.getSize())
                .uploadedBy(uploadedBy)
                .uploadedByName(uploaderName)
                .createdAt(Instant.now())
                .build();

        TaskAttachment saved = attachmentRepository.save(attachment);
        return mapToDTO(saved);
    }

    public List<TaskAttachmentDTO> getAttachmentsByTask(String taskId) {
        List<TaskAttachment> attachments = attachmentRepository.findByTaskId(taskId);
        return attachments.stream().map(this::mapToDTO).collect(Collectors.toList());
    }

    public List<TaskAttachmentDTO> getAttachmentsByProject(String projectId) {
        List<TaskAttachment> attachments = attachmentRepository.findByProjectId(projectId);
        return attachments.stream().map(this::mapToDTO).collect(Collectors.toList());
    }

    public void deleteAttachment(String id) {
        TaskAttachment attachment = attachmentRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Attachment not found with ID: " + id));
        attachmentRepository.delete(attachment);
    }

    private String resolveEmployeeName(String employeeId) {
        if (employeeId == null || employeeId.isBlank()) return null;
        return employeeRepository.findByEmployeeId(employeeId)
                .map(e -> e.getFirstName() + " " + e.getLastName())
                .orElse(employeeId);
    }

    private TaskAttachmentDTO mapToDTO(TaskAttachment attachment) {
        return TaskAttachmentDTO.builder()
                .id(attachment.getId())
                .taskId(attachment.getTaskId())
                .projectId(attachment.getProjectId())
                .fileName(attachment.getFileName())
                .fileUrl(attachment.getFileUrl())
                .fileType(attachment.getFileType())
                .fileSize(attachment.getFileSize())
                .uploadedBy(attachment.getUploadedBy())
                .uploadedByName(attachment.getUploadedByName())
                .createdAt(attachment.getCreatedAt())
                .build();
    }
}
