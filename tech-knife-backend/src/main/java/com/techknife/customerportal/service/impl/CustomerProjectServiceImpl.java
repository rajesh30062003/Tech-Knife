package com.techknife.customerportal.service.impl;

import com.techknife.customerportal.dto.CustomerMilestoneDTO;
import com.techknife.customerportal.dto.CustomerProjectDTO;
import com.techknife.customerportal.dto.CustomerTaskDTO;
import com.techknife.customerportal.dto.SharedDocumentDTO;
import com.techknife.customerportal.entity.*;
import com.techknife.customerportal.repository.*;
import com.techknife.customerportal.service.CustomerProjectService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CustomerProjectServiceImpl implements CustomerProjectService {

    private final CustomerProjectRepository customerProjectRepository;
    private final CustomerMilestoneRepository customerMilestoneRepository;
    private final CustomerTaskViewRepository customerTaskViewRepository;
    private final SharedDocumentRepository sharedDocumentRepository;
    private final SupportTicketRepository supportTicketRepository;

    @Override
    public List<CustomerProjectDTO> getProjects(String customerAccountId, String status) {
        List<CustomerProject> projects;
        if (status != null && !status.isBlank()) {
            projects = customerProjectRepository.findByCustomerAccountIdAndStatus(customerAccountId, status.toUpperCase());
        } else {
            projects = customerProjectRepository.findByCustomerAccountId(customerAccountId);
        }

        return projects.stream()
                .map(p -> mapToDTO(p, false))
                .collect(Collectors.toList());
    }

    @Override
    public CustomerProjectDTO getProjectById(String projectId, String customerAccountId) {
        CustomerProject project = customerProjectRepository.findByIdAndCustomerAccountId(projectId, customerAccountId)
                .orElseThrow(() -> new IllegalArgumentException("Project not found or unauthorized access"));

        return mapToDTO(project, true);
    }

    @Override
    public List<CustomerMilestoneDTO> getProjectMilestones(String projectId, String customerAccountId) {
        customerProjectRepository.findByIdAndCustomerAccountId(projectId, customerAccountId)
                .orElseThrow(() -> new IllegalArgumentException("Project not found or unauthorized access"));

        return customerMilestoneRepository.findByProjectId(projectId).stream()
                .map(this::mapMilestoneToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<CustomerTaskDTO> getProjectTasks(String projectId, String customerAccountId) {
        customerProjectRepository.findByIdAndCustomerAccountId(projectId, customerAccountId)
                .orElseThrow(() -> new IllegalArgumentException("Project not found or unauthorized access"));

        return customerTaskViewRepository.findByProjectId(projectId).stream()
                .map(this::mapTaskToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public CustomerProjectDTO createProject(CustomerProjectDTO dto) {
        CustomerProject project = CustomerProject.builder()
                .customerAccountId(dto.getCustomerAccountId())
                .projectCode(dto.getProjectCode() != null ? dto.getProjectCode() : "PRJ-" + System.currentTimeMillis() % 100000)
                .projectName(dto.getProjectName())
                .description(dto.getDescription())
                .status(dto.getStatus() != null ? dto.getStatus() : "IN_PROGRESS")
                .priority(dto.getPriority() != null ? dto.getPriority() : "MEDIUM")
                .startDate(dto.getStartDate())
                .endDate(dto.getEndDate())
                .progressPercentage(dto.getProgressPercentage() != null ? dto.getProgressPercentage() : 0.0)
                .projectManagerName(dto.getProjectManagerName())
                .projectManagerEmail(dto.getProjectManagerEmail())
                .assignedTeam(dto.getAssignedTeam())
                .techStack(dto.getTechStack())
                .repositoryUrl(dto.getRepositoryUrl())
                .build();

        CustomerProject saved = customerProjectRepository.save(project);
        return mapToDTO(saved, false);
    }

    private CustomerProjectDTO mapToDTO(CustomerProject project, boolean includeDetails) {
        long sharedDocsCount = sharedDocumentRepository.findByProjectId(project.getId()).size();
        long openTicketsCount = supportTicketRepository.findByCustomerAccountIdAndStatus(project.getCustomerAccountId(), "OPEN").size();

        CustomerProjectDTO dto = CustomerProjectDTO.builder()
                .id(project.getId())
                .customerAccountId(project.getCustomerAccountId())
                .projectCode(project.getProjectCode())
                .projectName(project.getProjectName())
                .description(project.getDescription())
                .status(project.getStatus())
                .priority(project.getPriority())
                .startDate(project.getStartDate())
                .endDate(project.getEndDate())
                .progressPercentage(project.getProgressPercentage())
                .projectManagerName(project.getProjectManagerName())
                .projectManagerEmail(project.getProjectManagerEmail())
                .assignedTeam(project.getAssignedTeam())
                .techStack(project.getTechStack())
                .repositoryUrl(project.getRepositoryUrl())
                .sharedDocumentsCount(sharedDocsCount)
                .openTicketsCount(openTicketsCount)
                .createdAt(project.getCreatedAt())
                .updatedAt(project.getUpdatedAt())
                .build();

        if (includeDetails) {
            List<CustomerMilestoneDTO> milestones = customerMilestoneRepository.findByProjectId(project.getId()).stream()
                    .map(this::mapMilestoneToDTO)
                    .collect(Collectors.toList());

            List<SharedDocumentDTO> docs = sharedDocumentRepository.findByProjectId(project.getId()).stream()
                    .map(this::mapDocToDTO)
                    .collect(Collectors.toList());

            dto.setMilestones(milestones);
            dto.setSharedDocuments(docs);
        }

        return dto;
    }

    private CustomerMilestoneDTO mapMilestoneToDTO(CustomerMilestone m) {
        return CustomerMilestoneDTO.builder()
                .id(m.getId())
                .projectId(m.getProjectId())
                .customerAccountId(m.getCustomerAccountId())
                .milestoneName(m.getMilestoneName())
                .description(m.getDescription())
                .status(m.getStatus())
                .dueDate(m.getDueDate())
                .completedDate(m.getCompletedDate())
                .completionPercentage(m.getCompletionPercentage())
                .deliverables(m.getDeliverables())
                .createdAt(m.getCreatedAt())
                .updatedAt(m.getUpdatedAt())
                .build();
    }

    private CustomerTaskDTO mapTaskToDTO(CustomerTaskView t) {
        return CustomerTaskDTO.builder()
                .id(t.getId())
                .projectId(t.getProjectId())
                .milestoneId(t.getMilestoneId())
                .customerAccountId(t.getCustomerAccountId())
                .taskName(t.getTaskName())
                .description(t.getDescription())
                .status(t.getStatus())
                .priority(t.getPriority())
                .dueDate(t.getDueDate())
                .assigneeName(t.getAssigneeName())
                .completedAt(t.getCompletedAt())
                .createdAt(t.getCreatedAt())
                .updatedAt(t.getUpdatedAt())
                .build();
    }

    private SharedDocumentDTO mapDocToDTO(SharedDocument doc) {
        return SharedDocumentDTO.builder()
                .id(doc.getId())
                .customerAccountId(doc.getCustomerAccountId())
                .projectId(doc.getProjectId())
                .documentName(doc.getDocumentName())
                .description(doc.getDescription())
                .fileUrl(doc.getFileUrl())
                .cloudinaryPublicId(doc.getCloudinaryPublicId())
                .fileType(doc.getFileType())
                .fileSize(doc.getFileSize())
                .category(doc.getCategory())
                .uploadedBy(doc.getUploadedBy())
                .uploadedAt(doc.getUploadedAt())
                .build();
    }
}
