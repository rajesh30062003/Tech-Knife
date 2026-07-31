package com.techknife.project.service;

import com.techknife.backend.service.SequenceGeneratorService;
import com.techknife.employee.repository.EmployeeRepository;
import com.techknife.project.dto.ProjectRequestDTO;
import com.techknife.project.dto.ProjectResponseDTO;
import com.techknife.project.entity.Project;
import com.techknife.project.entity.ProjectStatus;
import com.techknife.project.repository.*;
import com.techknife.storage.FileStorageService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.mongodb.core.MongoTemplate;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

class ProjectServiceTest {

    @Mock
    private ProjectRepository projectRepository;
    @Mock
    private ProjectStatusHistoryRepository statusHistoryRepository;
    @Mock
    private ProjectActivityRepository activityRepository;
    @Mock
    private MilestoneRepository milestoneRepository;
    @Mock
    private TaskRepository taskRepository;
    @Mock
    private EmployeeRepository employeeRepository;
    @Mock
    private FileStorageService fileStorageService;
    @Mock
    private MongoTemplate mongoTemplate;
    @Mock
    private SequenceGeneratorService sequenceGeneratorService;

    @InjectMocks
    private ProjectService projectService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    @DisplayName("Create 100 Projects - Verify unique, non-null projectId generation")
    void testCreate100Projects_UniqueProjectId() {
        Set<String> generatedIds = new HashSet<>();

        when(projectRepository.existsByProjectCode(anyString())).thenReturn(false);

        for (int i = 1; i <= 100; i++) {
            final int index = i;
            String mockProjectId = String.format("TK-PRJ-%06d", index);
            when(sequenceGeneratorService.generateProjectId()).thenReturn(mockProjectId);

            when(projectRepository.save(any(Project.class))).thenAnswer(invocation -> {
                Project p = invocation.getArgument(0);
                assertNotNull(p.getProjectId(), "projectId must NEVER be null before save");
                assertFalse(p.getProjectId().isBlank(), "projectId must NEVER be blank before save");
                p.setId("MONGO-ID-" + index);
                return p;
            });

            ProjectRequestDTO request = ProjectRequestDTO.builder()
                    .projectCode("PRJ-CODE-" + i)
                    .projectName("Automated Test Project " + i)
                    .department("Engineering")
                    .client("Tech Knife Enterprise")
                    .status(ProjectStatus.PLANNED)
                    .build();

            ProjectResponseDTO response = projectService.createProject(request, "TEST_USER", "ROLE_CEO");

            assertNotNull(response);
            assertNotNull(response.getProjectId());
            assertEquals(mockProjectId, response.getProjectId());
            assertTrue(generatedIds.add(response.getProjectId()), "Duplicate projectId generated: " + response.getProjectId());
        }

        assertEquals(100, generatedIds.size(), "Expected 100 unique project IDs");
    }
}
