package com.techknife.project.service;

import com.techknife.employee.entity.Employee;
import com.techknife.employee.repository.EmployeeRepository;
import com.techknife.project.dto.*;
import com.techknife.project.entity.*;
import com.techknife.project.repository.*;
import com.techknife.storage.FileStorageService;
import com.techknife.storage.FileUploadRequest;
import com.techknife.storage.FileUploadResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.time.Instant;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProjectService {

    private final ProjectRepository projectRepository;
    private final ProjectStatusHistoryRepository statusHistoryRepository;
    private final MilestoneRepository milestoneRepository;
    private final TaskRepository taskRepository;
    private final EmployeeRepository employeeRepository;
    private final FileStorageService fileStorageService;

    public ProjectResponseDTO createProject(ProjectRequestDTO request) {
        if (projectRepository.existsByProjectCode(request.getProjectCode())) {
            throw new IllegalArgumentException("Project code '" + request.getProjectCode() + "' already exists");
        }

        String pmName = resolveEmployeeName(request.getProjectManagerId());

        Project project = Project.builder()
                .projectCode(request.getProjectCode())
                .projectName(request.getProjectName())
                .shortName(request.getShortName())
                .description(request.getDescription())
                .client(request.getClient())
                .projectType(request.getProjectType() != null ? request.getProjectType() : ProjectType.FIXED_BID)
                .status(request.getStatus() != null ? request.getStatus() : ProjectStatus.PLANNED)
                .priority(request.getPriority() != null ? request.getPriority() : ProjectPriority.MEDIUM)
                .startDate(request.getStartDate())
                .endDate(request.getEndDate())
                .estimatedHours(request.getEstimatedHours() != null ? request.getEstimatedHours() : 0.0)
                .budget(request.getBudget() != null ? request.getBudget() : 0.0)
                .technologyStack(request.getTechnologyStack() != null ? request.getTechnologyStack() : new ArrayList<>())
                .repositoryUrl(request.getRepositoryUrl())
                .projectManagerId(request.getProjectManagerId())
                .projectManagerName(pmName)
                .logoUrl(request.getLogoUrl())
                .members(new ArrayList<>())
                .teams(new ArrayList<>())
                .documents(new ArrayList<>())
                .build();

        if (request.getProjectManagerId() != null && !request.getProjectManagerId().isBlank()) {
            project.getMembers().add(ProjectMember.builder()
                    .employeeId(request.getProjectManagerId())
                    .employeeName(pmName)
                    .role(ProjectMemberRole.PROJECT_MANAGER)
                    .allocationPercentage(100.0)
                    .joinedDate(LocalDate.now())
                    .build());
        }

        Project saved = projectRepository.save(project);

        // Record initial status
        logStatusChange(saved.getId(), null, saved.getStatus(), "Initial Project Creation", "SYSTEM");

        return mapToResponseDTO(saved);
    }

    public ProjectResponseDTO updateProject(String id, ProjectRequestDTO request) {
        Project project = getProjectEntity(id);

        if (!project.getProjectCode().equalsIgnoreCase(request.getProjectCode())
                && projectRepository.existsByProjectCode(request.getProjectCode())) {
            throw new IllegalArgumentException("Project code '" + request.getProjectCode() + "' is already taken");
        }

        project.setProjectCode(request.getProjectCode());
        project.setProjectName(request.getProjectName());
        project.setShortName(request.getShortName());
        project.setDescription(request.getDescription());
        project.setClient(request.getClient());
        if (request.getProjectType() != null) project.setProjectType(request.getProjectType());
        if (request.getPriority() != null) project.setPriority(request.getPriority());
        project.setStartDate(request.getStartDate());
        project.setEndDate(request.getEndDate());
        if (request.getEstimatedHours() != null) project.setEstimatedHours(request.getEstimatedHours());
        if (request.getBudget() != null) project.setBudget(request.getBudget());
        if (request.getTechnologyStack() != null) project.setTechnologyStack(request.getTechnologyStack());
        project.setRepositoryUrl(request.getRepositoryUrl());
        project.setLogoUrl(request.getLogoUrl());

        if (request.getProjectManagerId() != null && !request.getProjectManagerId().equals(project.getProjectManagerId())) {
            String newPmName = resolveEmployeeName(request.getProjectManagerId());
            project.setProjectManagerId(request.getProjectManagerId());
            project.setProjectManagerName(newPmName);
        }

        Project updated = projectRepository.save(project);
        return mapToResponseDTO(updated);
    }

    public ProjectResponseDTO updateStatus(String projectId, ProjectStatusUpdateDTO updateDTO, String currentUser) {
        Project project = getProjectEntity(projectId);
        ProjectStatus oldStatus = project.getStatus();
        ProjectStatus newStatus = updateDTO.getStatus();

        if (oldStatus != newStatus) {
            project.setStatus(newStatus);
            projectRepository.save(project);
            logStatusChange(projectId, oldStatus, newStatus, updateDTO.getReason(), currentUser);
        }

        return mapToResponseDTO(project);
    }

    public ProjectResponseDTO getProjectById(String id) {
        Project project = getProjectEntity(id);
        return mapToResponseDTO(project);
    }

    public ProjectResponseDTO getProjectByCode(String code) {
        Project project = projectRepository.findByProjectCode(code)
                .orElseThrow(() -> new NoSuchElementException("Project not found with code: " + code));
        return mapToResponseDTO(project);
    }

    public List<ProjectResponseDTO> getAllProjects(ProjectStatus status, String managerId, String employeeId) {
        List<Project> projects;
        if (status != null) {
            projects = projectRepository.findByStatus(status);
        } else if (managerId != null && !managerId.isBlank()) {
            projects = projectRepository.findByProjectManagerId(managerId);
        } else if (employeeId != null && !employeeId.isBlank()) {
            projects = projectRepository.findByMembersEmployeeId(employeeId);
        } else {
            projects = projectRepository.findAll();
        }

        return projects.stream().map(this::mapToResponseDTO).collect(Collectors.toList());
    }

    public void deleteProject(String id) {
        Project project = getProjectEntity(id);
        projectRepository.delete(project);
    }

    public ProjectResponseDTO addMember(String projectId, ProjectMemberDTO memberDTO) {
        Project project = getProjectEntity(projectId);

        boolean exists = project.getMembers().stream()
                .anyMatch(m -> m.getEmployeeId().equals(memberDTO.getEmployeeId()));
        if (exists) {
            throw new IllegalArgumentException("Employee is already a member of this project");
        }

        String name = resolveEmployeeName(memberDTO.getEmployeeId());
        ProjectMember member = ProjectMember.builder()
                .employeeId(memberDTO.getEmployeeId())
                .employeeName(name)
                .role(memberDTO.getRole() != null ? memberDTO.getRole() : ProjectMemberRole.MEMBER)
                .allocationPercentage(memberDTO.getAllocationPercentage() != null ? memberDTO.getAllocationPercentage() : 100.0)
                .joinedDate(memberDTO.getJoinedDate() != null ? memberDTO.getJoinedDate() : LocalDate.now())
                .build();

        project.getMembers().add(member);
        Project saved = projectRepository.save(project);
        return mapToResponseDTO(saved);
    }

    public ProjectResponseDTO removeMember(String projectId, String employeeId) {
        Project project = getProjectEntity(projectId);
        project.getMembers().removeIf(m -> m.getEmployeeId().equals(employeeId));
        Project saved = projectRepository.save(project);
        return mapToResponseDTO(saved);
    }

    public ProjectResponseDTO addTeam(String projectId, ProjectTeamDTO teamDTO) {
        Project project = getProjectEntity(projectId);

        String leadName = resolveEmployeeName(teamDTO.getLeadEmployeeId());
        ProjectTeam team = ProjectTeam.builder()
                .teamId(UUID.randomUUID().toString())
                .teamName(teamDTO.getTeamName())
                .leadEmployeeId(teamDTO.getLeadEmployeeId())
                .leadEmployeeName(leadName)
                .memberIds(teamDTO.getMemberIds() != null ? teamDTO.getMemberIds() : new ArrayList<>())
                .build();

        project.getTeams().add(team);
        Project saved = projectRepository.save(project);
        return mapToResponseDTO(saved);
    }

    public ProjectResponseDTO removeTeam(String projectId, String teamId) {
        Project project = getProjectEntity(projectId);
        project.getTeams().removeIf(t -> t.getTeamId().equals(teamId));
        Project saved = projectRepository.save(project);
        return mapToResponseDTO(saved);
    }

    public ProjectResponseDTO uploadDocument(String projectId, MultipartFile file, String uploadedBy) {
        Project project = getProjectEntity(projectId);

        FileUploadRequest uploadReq = FileUploadRequest.builder()
                .file(file)
                .folder("projects/" + project.getProjectCode() + "/documents")
                .build();

        FileUploadResponse uploadResp = fileStorageService.uploadDocument(file, "projects/" + project.getProjectCode() + "/documents");

        ProjectDocument doc = ProjectDocument.builder()
                .id(UUID.randomUUID().toString())
                .fileName(file.getOriginalFilename())
                .fileUrl(uploadResp.getSecureUrl())
                .fileType(file.getContentType())
                .fileSize(file.getSize())
                .uploadedBy(uploadedBy)
                .uploadedAt(Instant.now())
                .build();

        project.getDocuments().add(doc);
        Project saved = projectRepository.save(project);
        return mapToResponseDTO(saved);
    }

    public List<ProjectStatusHistory> getStatusHistory(String projectId) {
        return statusHistoryRepository.findByProjectIdOrderByChangedAtDesc(projectId);
    }

    public Project getProjectEntity(String id) {
        return projectRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Project not found with ID: " + id));
    }

    private String resolveEmployeeName(String employeeId) {
        if (employeeId == null || employeeId.isBlank()) return null;
        return employeeRepository.findByEmployeeId(employeeId)
                .map(e -> e.getFirstName() + " " + e.getLastName())
                .orElse(employeeId);
    }

    private void logStatusChange(String projectId, ProjectStatus oldStatus, ProjectStatus newStatus, String reason, String currentUser) {
        String name = resolveEmployeeName(currentUser);
        ProjectStatusHistory history = ProjectStatusHistory.builder()
                .projectId(projectId)
                .oldStatus(oldStatus)
                .newStatus(newStatus)
                .changedBy(currentUser)
                .changedByName(name != null ? name : currentUser)
                .reason(reason)
                .changedAt(Instant.now())
                .build();
        statusHistoryRepository.save(history);
    }

    private ProjectResponseDTO mapToResponseDTO(Project project) {
        List<Task> tasks = taskRepository.findByProjectId(project.getId());
        int totalTasks = tasks.size();
        int completedTasks = (int) tasks.stream().filter(t -> t.getStatus() == TaskStatus.DONE).count();
        double progress = totalTasks > 0 ? ((double) completedTasks / totalTasks) * 100.0 : 0.0;

        return ProjectResponseDTO.builder()
                .id(project.getId())
                .projectCode(project.getProjectCode())
                .projectName(project.getProjectName())
                .shortName(project.getShortName())
                .description(project.getDescription())
                .client(project.getClient())
                .projectType(project.getProjectType())
                .status(project.getStatus())
                .priority(project.getPriority())
                .startDate(project.getStartDate())
                .endDate(project.getEndDate())
                .estimatedHours(project.getEstimatedHours())
                .budget(project.getBudget())
                .technologyStack(project.getTechnologyStack())
                .repositoryUrl(project.getRepositoryUrl())
                .projectManagerId(project.getProjectManagerId())
                .projectManagerName(project.getProjectManagerName())
                .members(project.getMembers())
                .teams(project.getTeams())
                .documents(project.getDocuments())
                .logoUrl(project.getLogoUrl())
                .overallProgressPercentage(Math.round(progress * 10.0) / 10.0)
                .totalTasks(totalTasks)
                .completedTasks(completedTasks)
                .createdAt(project.getCreatedAt())
                .updatedAt(project.getUpdatedAt())
                .createdBy(project.getCreatedBy())
                .updatedBy(project.getUpdatedBy())
                .build();
    }
}
