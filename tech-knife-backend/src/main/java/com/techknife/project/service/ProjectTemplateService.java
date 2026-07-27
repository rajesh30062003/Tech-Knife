package com.techknife.project.service;

import com.techknife.project.dto.ProjectRequestDTO;
import com.techknife.project.dto.ProjectResponseDTO;
import com.techknife.project.dto.ProjectTemplateDTO;
import com.techknife.project.entity.*;
import com.techknife.project.repository.MilestoneRepository;
import com.techknife.project.repository.ProjectRepository;
import com.techknife.project.repository.ProjectTemplateRepository;
import com.techknife.project.repository.TaskRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProjectTemplateService {

    private final ProjectTemplateRepository templateRepository;
    private final ProjectService projectService;
    private final MilestoneRepository milestoneRepository;
    private final TaskRepository taskRepository;

    public ProjectTemplateDTO createTemplate(ProjectTemplateDTO dto) {
        if (templateRepository.existsByTemplateCode(dto.getTemplateCode())) {
            throw new IllegalArgumentException("Template code '" + dto.getTemplateCode() + "' already exists");
        }

        ProjectTemplate template = ProjectTemplate.builder()
                .templateCode(dto.getTemplateCode())
                .name(dto.getName())
                .description(dto.getDescription())
                .technologyStack(dto.getTechnologyStack() != null ? dto.getTechnologyStack() : new ArrayList<>())
                .defaultMilestones(dto.getDefaultMilestones() != null ? dto.getDefaultMilestones() : new ArrayList<>())
                .defaultTasks(dto.getDefaultTasks() != null ? dto.getDefaultTasks() : new ArrayList<>())
                .build();

        ProjectTemplate saved = templateRepository.save(template);
        return mapToDTO(saved);
    }

    public ProjectTemplateDTO updateTemplate(String id, ProjectTemplateDTO dto) {
        ProjectTemplate template = templateRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Template not found with ID: " + id));

        template.setName(dto.getName());
        template.setDescription(dto.getDescription());
        if (dto.getTechnologyStack() != null) template.setTechnologyStack(dto.getTechnologyStack());
        if (dto.getDefaultMilestones() != null) template.setDefaultMilestones(dto.getDefaultMilestones());
        if (dto.getDefaultTasks() != null) template.setDefaultTasks(dto.getDefaultTasks());

        ProjectTemplate saved = templateRepository.save(template);
        return mapToDTO(saved);
    }

    public ProjectTemplateDTO getTemplateById(String id) {
        ProjectTemplate template = templateRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Template not found with ID: " + id));
        return mapToDTO(template);
    }

    public List<ProjectTemplateDTO> getAllTemplates() {
        return templateRepository.findAll().stream().map(this::mapToDTO).collect(Collectors.toList());
    }

    public void deleteTemplate(String id) {
        ProjectTemplate template = templateRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Template not found with ID: " + id));
        templateRepository.delete(template);
    }

    public ProjectResponseDTO instantiateProjectFromTemplate(String templateId, ProjectRequestDTO request) {
        ProjectTemplate template = templateRepository.findById(templateId)
                .orElseThrow(() -> new NoSuchElementException("Template not found with ID: " + templateId));

        if (request.getTechnologyStack() == null || request.getTechnologyStack().isEmpty()) {
            request.setTechnologyStack(template.getTechnologyStack());
        }

        // Create the base project
        ProjectResponseDTO newProject = projectService.createProject(request);
        LocalDate start = request.getStartDate() != null ? request.getStartDate() : LocalDate.now();

        Map<String, String> milestoneTitleToIdMap = new HashMap<>();

        // Create default milestones from template
        if (template.getDefaultMilestones() != null) {
            for (TemplateMilestone tm : template.getDefaultMilestones()) {
                LocalDate dueDate = tm.getDayOffset() != null ? start.plusDays(tm.getDayOffset()) : start.plusDays(30);
                Milestone milestone = Milestone.builder()
                        .projectId(newProject.getId())
                        .title(tm.getTitle())
                        .description(tm.getDescription())
                        .dueDate(dueDate)
                        .status(MilestoneStatus.PLANNED)
                        .build();

                Milestone savedM = milestoneRepository.save(milestone);
                milestoneTitleToIdMap.put(tm.getTitle().toLowerCase(), savedM.getId());
            }
        }

        // Create default tasks from template
        if (template.getDefaultTasks() != null) {
            int count = 1;
            for (TemplateTask tt : template.getDefaultTasks()) {
                String milestoneId = tt.getMilestoneTitle() != null
                        ? milestoneTitleToIdMap.get(tt.getMilestoneTitle().toLowerCase())
                        : null;

                String taskNum = newProject.getShortName() != null ? newProject.getShortName().toUpperCase() + "-T" + count
                        : newProject.getProjectCode().toUpperCase() + "-T" + count;
                count++;

                Task task = Task.builder()
                        .taskNumber(taskNum)
                        .projectId(newProject.getId())
                        .title(tt.getTitle())
                        .description(tt.getDescription())
                        .priority(tt.getPriority() != null ? tt.getPriority() : TaskPriority.MEDIUM)
                        .status(TaskStatus.TODO)
                        .estimatedHours(tt.getEstimatedHours() != null ? tt.getEstimatedHours() : 8.0)
                        .milestoneId(milestoneId)
                        .dueDate(start.plusDays(14))
                        .build();

                taskRepository.save(task);
            }
        }

        return projectService.getProjectById(newProject.getId());
    }

    private ProjectTemplateDTO mapToDTO(ProjectTemplate template) {
        return ProjectTemplateDTO.builder()
                .id(template.getId())
                .templateCode(template.getTemplateCode())
                .name(template.getName())
                .description(template.getDescription())
                .technologyStack(template.getTechnologyStack())
                .defaultMilestones(template.getDefaultMilestones())
                .defaultTasks(template.getDefaultTasks())
                .createdAt(template.getCreatedAt())
                .updatedAt(template.getUpdatedAt())
                .build();
    }
}
