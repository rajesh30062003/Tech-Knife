package com.techknife.project.service;

import com.techknife.backend.exception.BadRequestException;
import com.techknife.backend.service.SequenceGeneratorService;
import com.techknife.employee.repository.EmployeeRepository;
import com.techknife.project.dto.*;
import com.techknife.project.entity.*;
import com.techknife.project.repository.*;
import com.techknife.security.UserPrincipal;
import com.techknife.storage.FileStorageService;
import com.techknife.storage.FileUploadRequest;
import com.techknife.storage.FileUploadResponse;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
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
    private final ProjectActivityRepository activityRepository;
    private final ProjectActivityService projectActivityService;
    private final MilestoneRepository milestoneRepository;
    private final TaskRepository taskRepository;
    private final EmployeeRepository employeeRepository;
    private final com.techknife.iam.repository.UserRepository iamUserRepository;
    private final FileStorageService fileStorageService;
    private final MongoTemplate mongoTemplate;
    private final SequenceGeneratorService sequenceGeneratorService;

    @PostConstruct
    public void initAndMigrateProjects() {
        try {
            log.info("DATABASE MIGRATION: Checking MongoDB Atlas projects collection for null/missing projectId or legacy status...");
            var collection = mongoTemplate.getCollection("projects");

            // 1. Repair any legacy document where projectId is null, missing, empty, or "null" string
            org.bson.Document queryNullId = new org.bson.Document("$or", List.of(
                    new org.bson.Document("projectId", null),
                    new org.bson.Document("projectId", new org.bson.Document("$exists", false)),
                    new org.bson.Document("projectId", ""),
                    new org.bson.Document("projectId", "null")
            ));

            List<org.bson.Document> nullIdDocs = new ArrayList<>();
            collection.find(queryNullId).into(nullIdDocs);

            if (!nullIdDocs.isEmpty()) {
                log.info("DATABASE MIGRATION: Found {} raw documents with null/missing projectId in MongoDB Atlas. Repairing...", nullIdDocs.size());
                for (org.bson.Document doc : nullIdDocs) {
                    Object idObj = doc.get("_id");
                    if (idObj != null) {
                        String newProjectId = sequenceGeneratorService.generateProjectId();
                        collection.updateOne(
                                new org.bson.Document("_id", idObj),
                                new org.bson.Document("$set", new org.bson.Document("projectId", newProjectId))
                        );
                        log.info("DATABASE MIGRATION: Repaired raw document _id={} with new projectId: {}", idObj, newProjectId);
                    }
                }
            }

            // 2. Repair any legacy document where projectCode is null, missing, or empty
            org.bson.Document queryNullCode = new org.bson.Document("$or", List.of(
                    new org.bson.Document("projectCode", null),
                    new org.bson.Document("projectCode", new org.bson.Document("$exists", false)),
                    new org.bson.Document("projectCode", ""),
                    new org.bson.Document("projectCode", "null")
            ));

            List<org.bson.Document> nullCodeDocs = new ArrayList<>();
            collection.find(queryNullCode).into(nullCodeDocs);

            if (!nullCodeDocs.isEmpty()) {
                log.info("DATABASE MIGRATION: Found {} raw documents with null/missing projectCode in MongoDB Atlas. Repairing...", nullCodeDocs.size());
                int count = 1;
                for (org.bson.Document doc : nullCodeDocs) {
                    Object idObj = doc.get("_id");
                    if (idObj != null) {
                        String newCode = "PRJ-MIG-" + System.currentTimeMillis() + "-" + count++;
                        collection.updateOne(
                                new org.bson.Document("_id", idObj),
                                new org.bson.Document("$set", new org.bson.Document("projectCode", newCode))
                        );
                        log.info("DATABASE MIGRATION: Repaired raw document _id={} with new projectCode: {}", idObj, newCode);
                    }
                }
            }

            // 3. Normalize legacy status strings & sanitize numeric string fields (e.g. "In Progress" -> "IN_PROGRESS", "₹ 25,00,000" -> 2500000.0)
            List<org.bson.Document> allDocs = new ArrayList<>();
            collection.find().into(allDocs);
            for (org.bson.Document doc : allDocs) {
                Object statusObj = doc.get("status");
                if (statusObj instanceof String statusStr) {
                    ProjectStatus mapped = ProjectStatus.fromString(statusStr);
                    if (!mapped.name().equals(statusStr)) {
                        collection.updateOne(
                                new org.bson.Document("_id", doc.get("_id")),
                                new org.bson.Document("$set", new org.bson.Document("status", mapped.name()))
                        );
                        log.info("DATABASE MIGRATION: Normalized status '{}' -> '{}' for project _id={}", statusStr, mapped.name(), doc.get("_id"));
                    }
                }

                org.bson.Document updateNumDoc = new org.bson.Document();
                for (String numField : List.of("budget", "estimatedCost", "estimatedHours", "estimatedDuration", "progressPercentage")) {
                    Object val = doc.get(numField);
                    if (val instanceof String strVal) {
                        try {
                            String cleanStr = strVal.replaceAll("[^0-9.]", "");
                            double parsedVal = cleanStr.isBlank() ? 0.0 : Double.parseDouble(cleanStr);
                            updateNumDoc.append(numField, parsedVal);
                        } catch (Exception parseEx) {
                            updateNumDoc.append(numField, 0.0);
                        }
                    }
                }
                if (!updateNumDoc.isEmpty()) {
                    collection.updateOne(
                            new org.bson.Document("_id", doc.get("_id")),
                            new org.bson.Document("$set", updateNumDoc)
                    );
                    log.info("DATABASE MIGRATION: Cleaned numeric string fields for project _id={}", doc.get("_id"));
                }
            }

            // 4. Sanitize employees collection malformed legacy BSON fields
            com.mongodb.client.MongoCollection<org.bson.Document> empCollection = mongoTemplate.getCollection("employees");
            List<org.bson.Document> allEmps = new ArrayList<>();
            empCollection.find().into(allEmps);
            for (org.bson.Document empDoc : allEmps) {
                org.bson.Document empUpdate = new org.bson.Document();
                org.bson.Document empUnset = new org.bson.Document();

                for (String listField : List.of("skills", "skillDetails", "education", "experience", "documents")) {
                    Object val = empDoc.get(listField);
                    if (val != null && !(val instanceof List)) {
                        empUpdate.append(listField, new ArrayList<>());
                    }
                }
                for (String addrField : List.of("currentAddress", "permanentAddress")) {
                    Object val = empDoc.get(addrField);
                    if (val instanceof String strVal) {
                        if (strVal.isBlank()) {
                            empUnset.append(addrField, "");
                        } else {
                            empUpdate.append(addrField, new org.bson.Document("street", strVal));
                        }
                    }
                }
                for (String dateField : List.of("dob", "joiningDate", "probationEndDate", "confirmationDate")) {
                    Object val = empDoc.get(dateField);
                    if (val instanceof String strVal && strVal.isBlank()) {
                        empUnset.append(dateField, "");
                    }
                }

                org.bson.Document opDoc = new org.bson.Document();
                if (!empUpdate.isEmpty()) {
                    opDoc.append("$set", empUpdate);
                }
                if (!empUnset.isEmpty()) {
                    opDoc.append("$unset", empUnset);
                }
                if (!opDoc.isEmpty()) {
                    empCollection.updateOne(new org.bson.Document("_id", empDoc.get("_id")), opDoc);
                    log.info("DATABASE MIGRATION: Sanitized employee raw document _id={}", empDoc.get("_id"));
                }
            }

            // 5. Automatic Data Repair: Segregate Interns into 'interns' collection and remove from 'employees'
            com.mongodb.client.MongoCollection<org.bson.Document> internsColl = mongoTemplate.getCollection("interns");
            List<org.bson.Document> empsToRemoveFromEmployees = new ArrayList<>();
            for (org.bson.Document empDoc : allEmps) {
                String empId = empDoc.getString("employeeId");
                String empType = empDoc.getString("employmentType");
                boolean isIntern = "INTERN".equalsIgnoreCase(empType) || (empId != null && empId.toUpperCase().startsWith("INT-"));

                if (isIntern) {
                    String internCode = empId != null ? empId : "INT-001";
                    org.bson.Document existingIntern = internsColl.find(new org.bson.Document("internCode", internCode)).first();
                    if (existingIntern == null && empDoc.getString("officialEmail") != null) {
                        existingIntern = internsColl.find(new org.bson.Document("officialEmail", empDoc.getString("officialEmail"))).first();
                    }
                    if (existingIntern == null) {
                        org.bson.Document internDoc = new org.bson.Document(empDoc);
                        internDoc.put("internCode", internCode);
                        internDoc.put("status", "ACTIVE");
                        internDoc.put("certificateGenerated", false);
                        internDoc.put("convertedToEmployee", false);
                        internsColl.insertOne(internDoc);
                        log.info("DATABASE MIGRATION: Moved intern document internCode={} to 'interns' collection", internCode);
                    }
                    empsToRemoveFromEmployees.add(empDoc);
                }
            }
            for (org.bson.Document internToRemove : empsToRemoveFromEmployees) {
                empCollection.deleteOne(new org.bson.Document("_id", internToRemove.get("_id")));
                log.info("DATABASE MIGRATION: Removed intern document _id={} from 'employees' collection for strict data segregation", internToRemove.get("_id"));
            }

            log.info("DATABASE MIGRATION: MongoDB Atlas project & employee collection migration complete.");
        } catch (Exception e) {
            log.error("DATABASE MIGRATION ERROR: Failed to execute raw MongoDB Atlas migration: {}", e.getMessage(), e);
        }
    }

    public ProjectResponseDTO createProject(ProjectRequestDTO request) {
        return createProject(request, "SYSTEM", "ROLE_SYSTEM");
    }

    public ProjectResponseDTO createProject(ProjectRequestDTO request, String currentUser, String currentRole) {
        // Task 6: Validation before saving
        if (request.getProjectName() == null || request.getProjectName().isBlank()) {
            throw new BadRequestException("Project name is required");
        }
        if (request.getProjectCode() == null || request.getProjectCode().isBlank()) {
            throw new BadRequestException("Project code is required");
        }
        if (projectRepository.existsByProjectCode(request.getProjectCode())) {
            throw new BadRequestException("Project code '" + request.getProjectCode() + "' already exists");
        }

        // Task 5: Atomic Enterprise Project ID Generation (TK-PRJ-XXXXXX)
        String generatedProjectId = sequenceGeneratorService.generateProjectId();
        if (generatedProjectId == null || generatedProjectId.isBlank()) {
            log.error("CRITICAL ERROR: Failed to generate atomic projectId");
            throw new BadRequestException("Project ID generation failed");
        }

        String pmName = resolveEmployeeName(request.getProjectManagerId());
        String leadName = resolveEmployeeName(request.getProjectLeadId());

        String dept = (request.getDepartment() != null && !request.getDepartment().isBlank()) ? request.getDepartment() : "Engineering";
        String clientName = (request.getClient() != null && !request.getClient().isBlank()) ? request.getClient() : "Internal";
        ProjectStatus pStatus = request.getStatus() != null ? request.getStatus() : ProjectStatus.PLANNED;

        Project project = Project.builder()
                .projectId(generatedProjectId)
                .projectCode(request.getProjectCode().trim().toUpperCase())
                .projectName(request.getProjectName().trim())
                .shortName(request.getShortName())
                .description(request.getDescription())
                .objectives(request.getObjectives())
                .client(clientName)
                .clientId(request.getClientId())
                .clientOrganization(request.getClientOrganization())
                .department(dept)
                .category(request.getCategory() != null ? request.getCategory() : "Technical")
                .businessUnit(request.getBusinessUnit() != null ? request.getBusinessUnit() : "Enterprise Services")
                .projectType(request.getProjectType() != null ? request.getProjectType() : ProjectType.FIXED_BID)
                .status(pStatus)
                .priority(request.getPriority() != null ? request.getPriority() : ProjectPriority.MEDIUM)
                .startDate(request.getStartDate())
                .endDate(request.getEndDate())
                .targetEndDate(request.getTargetEndDate())
                .estimatedCompletion(request.getEstimatedCompletion())
                .estimatedHours(request.getEstimatedHours() != null ? request.getEstimatedHours() : 0.0)
                .estimatedDuration(request.getEstimatedDuration() != null ? request.getEstimatedDuration() : 0.0)
                .budget(request.getBudget() != null ? request.getBudget() : 0.0)
                .estimatedCost(request.getEstimatedCost() != null ? request.getEstimatedCost() : 0.0)
                .progressPercentage(request.getProgressPercentage() != null ? request.getProgressPercentage() : 0.0)
                .technologyStack(request.getTechnologyStack() != null ? request.getTechnologyStack() : new ArrayList<>())
                .programmingLanguages(request.getProgrammingLanguages() != null ? request.getProgrammingLanguages() : new ArrayList<>())
                .frameworks(request.getFrameworks() != null ? request.getFrameworks() : new ArrayList<>())
                .databaseTech(request.getDatabaseTech())
                .cloudProvider(request.getCloudProvider())
                .repositoryUrl(request.getRepositoryUrl())
                .repositoryType(request.getRepositoryType() != null ? request.getRepositoryType() : "GIT")
                .repositoryVisibility(request.getRepositoryVisibility() != null ? request.getRepositoryVisibility() : "PRIVATE")
                .projectVisibility(request.getProjectVisibility() != null ? request.getProjectVisibility() : "PRIVATE")
                .deploymentType(request.getDeploymentType() != null ? request.getDeploymentType() : "CLOUD")
                .projectManagerId(request.getProjectManagerId())
                .projectManagerName(pmName)
                .projectLeadId(request.getProjectLeadId())
                .projectLeadName(leadName)
                .projectSponsor(request.getProjectSponsor())
                .customerRepresentative(request.getCustomerRepresentative())
                .assignedEmployees(request.getAssignedEmployees() != null ? request.getAssignedEmployees() : new ArrayList<>())
                .assignedInterns(request.getAssignedInterns() != null ? request.getAssignedInterns() : new ArrayList<>())
                .links(request.getLinks() != null ? request.getLinks() : new ProjectLinks())
                .remarks(request.getRemarks())
                .tags(request.getTags() != null ? request.getTags() : new ArrayList<>())
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

        if (request.getProjectLeadId() != null && !request.getProjectLeadId().isBlank()) {
            project.getMembers().add(ProjectMember.builder()
                    .employeeId(request.getProjectLeadId())
                    .employeeName(leadName)
                    .role(ProjectMemberRole.TECH_LEAD)
                    .allocationPercentage(100.0)
                    .joinedDate(LocalDate.now())
                    .build());
        }

        // Task 6: Pre-save validation & logging
        log.info("PRE-SAVE VERIFICATION: Saving project '{}' (Code: {}) with generated projectId: {}", project.getProjectName(), project.getProjectCode(), project.getProjectId());

        if (project.getProjectId() == null || project.getProjectId().isBlank()) {
            log.error("CRITICAL SAFETY BLOCK: Attempted to save project with null projectId!");
            throw new BadRequestException("Project ID generation failed");
        }
        if (project.getProjectName() == null || project.getProjectName().isBlank()) {
            throw new BadRequestException("Project name is required");
        }
        if (project.getStatus() == null) {
            throw new BadRequestException("Project status is required");
        }
        if (project.getDepartment() == null || project.getDepartment().isBlank()) {
            throw new BadRequestException("Department is required");
        }

        Project saved = projectRepository.save(project);
        syncProjectAssignments(saved);

        // Audit Activity
        logActivity(saved.getId(), "CREATE_PROJECT", currentUser, currentRole, "Project", null, saved.getProjectName());
        logStatusChange(saved.getId(), null, saved.getStatus(), "Initial Enterprise Creation", currentUser);

        return mapToResponseDTO(saved);
    }

    public ProjectResponseDTO updateProject(String id, ProjectRequestDTO request) {
        return updateProject(id, request, "SYSTEM", "ROLE_SYSTEM");
    }

    public ProjectResponseDTO requestStatusChange(String projectId, ProjectStatusRequestDTO requestDTO, String currentUser, String currentRole) {
        Project project = getProjectEntity(projectId);

        ProjectPendingStatusRequest pending = ProjectPendingStatusRequest.builder()
                .requestedStatus(requestDTO.getRequestedStatus())
                .reason(requestDTO.getReason())
                .requestedBy(requestDTO.getRequestedBy() != null && !requestDTO.getRequestedBy().isBlank() ? requestDTO.getRequestedBy() : currentUser)
                .requestedByRole(requestDTO.getRequestedByRole() != null && !requestDTO.getRequestedByRole().isBlank() ? requestDTO.getRequestedByRole() : currentRole)
                .requestedAt(LocalDate.now().toString())
                .build();

        org.springframework.data.mongodb.core.query.Criteria criteria;
        if (org.bson.types.ObjectId.isValid(project.getId())) {
            criteria = org.springframework.data.mongodb.core.query.Criteria.where("_id").is(new org.bson.types.ObjectId(project.getId()));
        } else {
            criteria = org.springframework.data.mongodb.core.query.Criteria.where("_id").is(project.getId());
        }

        org.springframework.data.mongodb.core.query.Query query = new org.springframework.data.mongodb.core.query.Query(criteria);
        org.springframework.data.mongodb.core.query.Update update = new org.springframework.data.mongodb.core.query.Update()
                .set("pendingStatusRequest", pending);

        mongoTemplate.updateFirst(query, update, Project.class);
        project.setPendingStatusRequest(pending);

        logActivity(project.getId(), "REQUEST_STATUS_CHANGE", currentUser, currentRole, "PendingStatusRequest",
                String.valueOf(project.getStatus()), requestDTO.getRequestedStatus());

        return mapToResponseDTO(project);
    }

    public ProjectResponseDTO updateProject(String id, ProjectRequestDTO request, String currentUser, String currentRole) {
        Project project = getProjectEntity(id);

        if (request.getProjectCode() != null && !request.getProjectCode().isBlank()) {
            String newCode = request.getProjectCode().trim().toUpperCase();
            if (!newCode.equalsIgnoreCase(project.getProjectCode())
                    && projectRepository.existsByProjectCode(newCode)) {
                throw new BadRequestException("Project code '" + newCode + "' is already taken");
            }
            project.setProjectCode(newCode);
        }

        if (request.getProjectName() != null && !request.getProjectName().isBlank()) {
            project.setProjectName(request.getProjectName().trim());
        }

        if (request.getShortName() != null) project.setShortName(request.getShortName());
        if (request.getDescription() != null) project.setDescription(request.getDescription());
        if (request.getObjectives() != null) project.setObjectives(request.getObjectives());
        if (request.getClient() != null) project.setClient(request.getClient());
        if (request.getClientId() != null) project.setClientId(request.getClientId());
        if (request.getClientOrganization() != null) project.setClientOrganization(request.getClientOrganization());
        if (request.getDepartment() != null) project.setDepartment(request.getDepartment());
        if (request.getCategory() != null) project.setCategory(request.getCategory());
        if (request.getBusinessUnit() != null) project.setBusinessUnit(request.getBusinessUnit());
        if (request.getProjectType() != null) project.setProjectType(request.getProjectType());
        if (request.getStatus() != null) project.setStatus(request.getStatus());
        if (request.getPriority() != null) project.setPriority(request.getPriority());
        if (request.getStartDate() != null) project.setStartDate(request.getStartDate());
        if (request.getEndDate() != null) project.setEndDate(request.getEndDate());
        if (request.getTargetEndDate() != null) project.setTargetEndDate(request.getTargetEndDate());
        if (request.getEstimatedCompletion() != null) project.setEstimatedCompletion(request.getEstimatedCompletion());
        if (request.getEstimatedHours() != null) project.setEstimatedHours(request.getEstimatedHours());
        if (request.getEstimatedDuration() != null) project.setEstimatedDuration(request.getEstimatedDuration());
        if (request.getBudget() != null) project.setBudget(request.getBudget());
        if (request.getEstimatedCost() != null) project.setEstimatedCost(request.getEstimatedCost());
        if (request.getProgressPercentage() != null) project.setProgressPercentage(request.getProgressPercentage());
        if (request.getTechnologyStack() != null) project.setTechnologyStack(request.getTechnologyStack());
        if (request.getProgrammingLanguages() != null) project.setProgrammingLanguages(request.getProgrammingLanguages());
        if (request.getFrameworks() != null) project.setFrameworks(request.getFrameworks());
        if (request.getDatabaseTech() != null) project.setDatabaseTech(request.getDatabaseTech());
        if (request.getCloudProvider() != null) project.setCloudProvider(request.getCloudProvider());
        if (request.getRepositoryUrl() != null) project.setRepositoryUrl(request.getRepositoryUrl());
        if (request.getRepositoryType() != null) project.setRepositoryType(request.getRepositoryType());
        if (request.getRepositoryVisibility() != null) project.setRepositoryVisibility(request.getRepositoryVisibility());
        if (request.getProjectVisibility() != null) project.setProjectVisibility(request.getProjectVisibility());
        if (request.getDeploymentType() != null) project.setDeploymentType(request.getDeploymentType());
        if (request.getProjectSponsor() != null) project.setProjectSponsor(request.getProjectSponsor());
        if (request.getCustomerRepresentative() != null) project.setCustomerRepresentative(request.getCustomerRepresentative());
        if (request.getRemarks() != null) project.setRemarks(request.getRemarks());
        if (request.getTags() != null) project.setTags(request.getTags());
        if (request.getLogoUrl() != null) project.setLogoUrl(request.getLogoUrl());

        if (request.getAssignedEmployees() != null) project.setAssignedEmployees(request.getAssignedEmployees());
        if (request.getAssignedInterns() != null) project.setAssignedInterns(request.getAssignedInterns());

        if (request.getProjectManagerId() != null && !request.getProjectManagerId().equals(project.getProjectManagerId())) {
            String newPmName = resolveEmployeeName(request.getProjectManagerId());
            project.setProjectManagerId(request.getProjectManagerId());
            project.setProjectManagerName(newPmName);
        }

        if (request.getProjectLeadId() != null && !request.getProjectLeadId().equals(project.getProjectLeadId())) {
            String newLeadName = resolveEmployeeName(request.getProjectLeadId());
            project.setProjectLeadId(request.getProjectLeadId());
            project.setProjectLeadName(newLeadName);
        }

        if (request.getLinks() != null) {
            project.setLinks(request.getLinks());
        }

        try {
            project.setPendingStatusRequest(request.getPendingStatusRequest());
            Project updated = projectRepository.save(project);
            syncProjectAssignments(updated);
            logActivity(updated.getId(), "UPDATE_PROJECT", currentUser, currentRole, "Metadata", null, "Updated Metadata");
            return mapToResponseDTO(updated);
        } catch (org.springframework.dao.DuplicateKeyException dke) {
            org.springframework.data.mongodb.core.query.Criteria criteria;
            if (org.bson.types.ObjectId.isValid(project.getId())) {
                criteria = org.springframework.data.mongodb.core.query.Criteria.where("_id").is(new org.bson.types.ObjectId(project.getId()));
            } else {
                criteria = org.springframework.data.mongodb.core.query.Criteria.where("_id").is(project.getId());
            }

            org.springframework.data.mongodb.core.query.Query query = new org.springframework.data.mongodb.core.query.Query(criteria);
            org.springframework.data.mongodb.core.query.Update update = new org.springframework.data.mongodb.core.query.Update();
            if (request.getPendingStatusRequest() == null) {
                update.unset("pendingStatusRequest");
            } else {
                update.set("pendingStatusRequest", request.getPendingStatusRequest());
            }
            mongoTemplate.updateFirst(query, update, Project.class);
            logActivity(project.getId(), "UPDATE_PROJECT", currentUser, currentRole, "Metadata", null, "Updated Metadata");
            return mapToResponseDTO(project);
        }
    }

    public ProjectResponseDTO updateStatus(String projectId, ProjectStatusUpdateDTO updateDTO, String currentUser) {
        return updateStatus(projectId, updateDTO, currentUser, "ROLE_EMPLOYEE");
    }

    public ProjectResponseDTO updateStatus(String projectId, ProjectStatusUpdateDTO updateDTO, String currentUser, String currentRole) {
        Project project = getProjectEntity(projectId);
        ProjectStatus oldStatus = project.getStatus();
        ProjectStatus newStatus = updateDTO.getStatus() != null ? updateDTO.getStatus() : oldStatus;

        if (updateDTO.getProgressPercentage() != null) {
            Double oldProgress = project.getProgressPercentage();
            project.setProgressPercentage(updateDTO.getProgressPercentage());
            logActivity(projectId, "UPDATE_PROGRESS", currentUser, currentRole, "ProgressPercentage", String.valueOf(oldProgress), String.valueOf(updateDTO.getProgressPercentage()));
        }

        if (newStatus != null && oldStatus != newStatus) {
            project.setStatus(newStatus);
            logStatusChange(projectId, oldStatus, newStatus, updateDTO.getReason(), currentUser);
            logActivity(projectId, "UPDATE_STATUS", currentUser, currentRole, "Status", String.valueOf(oldStatus), String.valueOf(newStatus));
        }

        org.springframework.data.mongodb.core.query.Criteria criteria;
        if (org.bson.types.ObjectId.isValid(project.getId())) {
            criteria = org.springframework.data.mongodb.core.query.Criteria.where("_id").is(new org.bson.types.ObjectId(project.getId()));
        } else {
            criteria = org.springframework.data.mongodb.core.query.Criteria.where("_id").is(project.getId());
        }

        org.springframework.data.mongodb.core.query.Query query = new org.springframework.data.mongodb.core.query.Query(criteria);
        org.springframework.data.mongodb.core.query.Update update = new org.springframework.data.mongodb.core.query.Update()
                .set("status", project.getStatus())
                .set("progressPercentage", project.getProgressPercentage())
                .unset("pendingStatusRequest");

        mongoTemplate.updateFirst(query, update, Project.class);
        project.setPendingStatusRequest(null);

        return mapToResponseDTO(project);
    }

    public ProjectResponseDTO assignMembers(String projectId, ProjectAssignDTO dto, String currentUser, String currentRole) {
        Project project = getProjectEntity(projectId);

        if (dto.getProjectManagerId() != null) {
            project.setProjectManagerId(dto.getProjectManagerId());
            project.setProjectManagerName(resolveEmployeeName(dto.getProjectManagerId()));
        }
        if (dto.getProjectLeadId() != null) {
            project.setProjectLeadId(dto.getProjectLeadId());
            project.setProjectLeadName(resolveEmployeeName(dto.getProjectLeadId()));
        }
        if (dto.getAssignedEmployees() != null) {
            project.setAssignedEmployees(dto.getAssignedEmployees());
        }
        if (dto.getAssignedInterns() != null) {
            project.setAssignedInterns(dto.getAssignedInterns());
        }

        Project saved = projectRepository.save(project);
        syncProjectAssignments(saved);
        logActivity(projectId, "ASSIGN_MEMBERS", currentUser, currentRole, "Members", null, "Updated assignments");
        return mapToResponseDTO(saved);
    }

    public void syncProjectAssignments(Project project) {
        if (project == null) return;
        try {
            String pId = project.getProjectId() != null ? project.getProjectId() : project.getId();
            String pName = project.getProjectName() != null ? project.getProjectName() : project.getProjectCode();

            org.springframework.data.mongodb.core.query.Query removeQuery = new org.springframework.data.mongodb.core.query.Query();
            removeQuery.addCriteria(new org.springframework.data.mongodb.core.query.Criteria().orOperator(
                org.springframework.data.mongodb.core.query.Criteria.where("projectId").is(pId),
                org.springframework.data.mongodb.core.query.Criteria.where("projectId").is(project.getId()),
                org.springframework.data.mongodb.core.query.Criteria.where("projectId").is(project.getProjectId())
            ));
            mongoTemplate.remove(removeQuery, com.techknife.project.entity.ProjectAssignment.class);

            List<com.techknife.project.entity.ProjectAssignment> newAssignments = new ArrayList<>();
            Set<String> processedEmpIds = new HashSet<>();

            if (project.getProjectManagerId() != null && !project.getProjectManagerId().isBlank()) {
                processedEmpIds.add(project.getProjectManagerId());
                newAssignments.add(com.techknife.project.entity.ProjectAssignment.builder()
                        .employeeId(project.getProjectManagerId())
                        .projectId(pId)
                        .projectName(pName)
                        .role("PROJECT_MANAGER")
                        .allocationPercentage(100.0)
                        .assignedDate(LocalDate.now())
                        .status("ACTIVE")
                        .build());
            }

            if (project.getProjectLeadId() != null && !project.getProjectLeadId().isBlank() && !processedEmpIds.contains(project.getProjectLeadId())) {
                processedEmpIds.add(project.getProjectLeadId());
                newAssignments.add(com.techknife.project.entity.ProjectAssignment.builder()
                        .employeeId(project.getProjectLeadId())
                        .projectId(pId)
                        .projectName(pName)
                        .role("PROJECT_LEAD")
                        .allocationPercentage(100.0)
                        .assignedDate(LocalDate.now())
                        .status("ACTIVE")
                        .build());
            }

            if (project.getAssignedEmployees() != null) {
                for (String empId : project.getAssignedEmployees()) {
                    if (empId != null && !empId.isBlank() && !processedEmpIds.contains(empId)) {
                        processedEmpIds.add(empId);
                        newAssignments.add(com.techknife.project.entity.ProjectAssignment.builder()
                                .employeeId(empId)
                                .projectId(pId)
                                .projectName(pName)
                                .role("EMPLOYEE")
                                .allocationPercentage(100.0)
                                .assignedDate(LocalDate.now())
                                .status("ACTIVE")
                                .build());
                    }
                }
            }

            if (project.getAssignedInterns() != null) {
                for (String internId : project.getAssignedInterns()) {
                    if (internId != null && !internId.isBlank() && !processedEmpIds.contains(internId)) {
                        processedEmpIds.add(internId);
                        newAssignments.add(com.techknife.project.entity.ProjectAssignment.builder()
                                .employeeId(internId)
                                .projectId(pId)
                                .projectName(pName)
                                .role("INTERN")
                                .allocationPercentage(100.0)
                                .assignedDate(LocalDate.now())
                                .status("ACTIVE")
                                .build());
                    }
                }
            }

            if (!newAssignments.isEmpty()) {
                mongoTemplate.insertAll(newAssignments);
                log.info("PROJECT ASSIGNMENT SYNC: Persisted {} assignment records to MongoDB 'project_assignments' for project '{}' ({})",
                        newAssignments.size(), pName, pId);
            }
        } catch (Exception e) {
            log.error("PROJECT ASSIGNMENT SYNC ERROR: Failed to sync assignments for project: {}", e.getMessage(), e);
        }
    }

    public ProjectResponseDTO updateLinks(String projectId, ProjectLinksUpdateDTO dto, String currentUser, String currentRole) {
        Project project = getProjectEntity(projectId);
        if (dto.getLinks() != null) {
            project.setLinks(dto.getLinks());
        }
        if (dto.getRepositoryVisibility() != null) {
            project.setRepositoryVisibility(dto.getRepositoryVisibility());
        }
        if (dto.getDeploymentType() != null) {
            project.setDeploymentType(dto.getDeploymentType());
        }
        Project saved = projectRepository.save(project);
        logActivity(projectId, "UPDATE_LINKS", currentUser, currentRole, "Links", null, "Updated links & repositories");
        return mapToResponseDTO(saved);
    }

    public ProjectResponseDTO getProjectById(String id) {
        Project project = getProjectEntity(id);
        return mapToResponseDTO(project);
    }

    public ProjectResponseDTO getProjectByCode(String code) {
        Project project = projectRepository.findByProjectCode(code)
                .orElseThrow(() -> new BadRequestException("Project not found with code: " + code));
        return mapToResponseDTO(project);
    }

    private String resolveCanonicalEmployeeId(UserPrincipal principal) {
        if (principal == null) return null;
        String id = principal.getId();
        String email = principal.getEmail();

        if (id != null && (id.toUpperCase().startsWith("EMP-") || id.toUpperCase().startsWith("INT-"))) {
            return id;
        }

        if (employeeRepository != null) {
            if (email != null && !email.isBlank()) {
                Optional<com.techknife.employee.entity.Employee> emp = employeeRepository.findByOfficialEmail(email);
                if (emp.isPresent() && emp.get().getEmployeeId() != null) {
                    return emp.get().getEmployeeId();
                }
                Optional<com.techknife.employee.entity.Employee> empPersonal = employeeRepository.findByPersonalEmail(email);
                if (empPersonal.isPresent() && empPersonal.get().getEmployeeId() != null) {
                    return empPersonal.get().getEmployeeId();
                }
            }

            if (id != null && !id.isBlank()) {
                Optional<com.techknife.employee.entity.Employee> emp = employeeRepository.findByEmployeeId(id);
                if (emp.isPresent() && emp.get().getEmployeeId() != null) {
                    return emp.get().getEmployeeId();
                }
                Optional<com.techknife.employee.entity.Employee> empById = employeeRepository.findById(id);
                if (empById.isPresent() && empById.get().getEmployeeId() != null) {
                    return empById.get().getEmployeeId();
                }
                Optional<com.techknife.employee.entity.Employee> empByOfficial = employeeRepository.findByOfficialEmail(id);
                if (empByOfficial.isPresent() && empByOfficial.get().getEmployeeId() != null) {
                    return empByOfficial.get().getEmployeeId();
                }
                Optional<com.techknife.employee.entity.Employee> empByPersonal = employeeRepository.findByPersonalEmail(id);
                if (empByPersonal.isPresent() && empByPersonal.get().getEmployeeId() != null) {
                    return empByPersonal.get().getEmployeeId();
                }
            }
        }

        if (iamUserRepository != null) {
            String firstName = null;
            String lastName = null;
            String foundUserId = null;

            if (id != null && !id.isBlank()) {
                Optional<com.techknife.iam.entity.User> iamUser = iamUserRepository.findById(id);
                if (iamUser.isPresent()) {
                    foundUserId = iamUser.get().getUserId();
                    firstName = iamUser.get().getFirstName();
                    lastName = iamUser.get().getLastName();
                }
            }
            if (firstName == null && email != null && !email.isBlank()) {
                Optional<com.techknife.iam.entity.User> iamUser = iamUserRepository.findByOfficialEmail(email)
                        .or(() -> iamUserRepository.findByPersonalEmail(email));
                if (iamUser.isPresent()) {
                    foundUserId = iamUser.get().getUserId();
                    firstName = iamUser.get().getFirstName();
                    lastName = iamUser.get().getLastName();
                }
            }

            if (foundUserId != null && (foundUserId.toUpperCase().startsWith("EMP-") || foundUserId.toUpperCase().startsWith("INT-"))) {
                return foundUserId;
            }

            if (employeeRepository != null && firstName != null && lastName != null) {
                final String fName = firstName.trim();
                final String lName = lastName.trim();
                Optional<com.techknife.employee.entity.Employee> empByName = employeeRepository.findAll().stream()
                        .filter(e -> e.getFirstName() != null && e.getFirstName().equalsIgnoreCase(fName)
                                  && e.getLastName() != null && e.getLastName().equalsIgnoreCase(lName))
                        .findFirst();
                if (empByName.isPresent() && empByName.get().getEmployeeId() != null) {
                    return empByName.get().getEmployeeId();
                }

                Optional<com.techknife.iam.entity.User> userByName = iamUserRepository.findAll().stream()
                        .filter(u -> u.getUserId() != null
                                && (u.getUserId().toUpperCase().startsWith("EMP-") || u.getUserId().toUpperCase().startsWith("INT-"))
                                && u.getFirstName() != null && u.getFirstName().equalsIgnoreCase(fName)
                                && u.getLastName() != null && u.getLastName().equalsIgnoreCase(lName))
                        .findFirst();
                if (userByName.isPresent()) {
                    return userByName.get().getUserId();
                }
            }
        }

        return id;
    }

    public List<ProjectResponseDTO> getAllProjects(UserPrincipal principal, ProjectStatus status, String category) {
        List<Project> projects = projectRepository.findAll();

        if (principal != null) {
            List<String> roles = principal.getRoles() != null ? principal.getRoles() : new ArrayList<>();

            boolean isGlobalViewer = roles.stream().anyMatch(r ->
                    r.equalsIgnoreCase("ROLE_SUPER_ADMIN") || r.equalsIgnoreCase("SUPER_ADMIN") ||
                    r.equalsIgnoreCase("ROLE_CEO") || r.equalsIgnoreCase("CEO") ||
                    r.equalsIgnoreCase("ROLE_MD") || r.equalsIgnoreCase("MD") ||
                    r.equalsIgnoreCase("ROLE_CTO") || r.equalsIgnoreCase("CTO") ||
                    r.equalsIgnoreCase("ROLE_GROWTH_HEAD") || r.equalsIgnoreCase("GROWTH_HEAD")
            );

            if (!isGlobalViewer) {
                boolean isCustomer = roles.stream().anyMatch(r -> r.equalsIgnoreCase("ROLE_CUSTOMER") || r.equalsIgnoreCase("CUSTOMER"));

                if (isCustomer) {
                    String userId = principal.getId();
                    String username = principal.getUsername(); // email
                    projects = projects.stream().filter(p -> {
                        if (userId != null && (userId.equalsIgnoreCase(p.getClientId()) || userId.equalsIgnoreCase(p.getClient()))) {
                            return true;
                        }
                        if (username != null && (username.equalsIgnoreCase(p.getClient()) || username.equalsIgnoreCase(p.getCustomerRepresentative()))) {
                            return true;
                        }
                        return false;
                    }).collect(Collectors.toList());
                } else {
                    String canonicalEmpId = resolveCanonicalEmployeeId(principal);
                    String userId = principal.getId();
                    String email = principal.getEmail();

                    projects = projects.stream().filter(p -> {
                        if (canonicalEmpId != null && !canonicalEmpId.isBlank()) {
                            if (canonicalEmpId.equalsIgnoreCase(p.getProjectManagerId()) ||
                                canonicalEmpId.equalsIgnoreCase(p.getProjectLeadId())) {
                                return true;
                            }
                            if (p.getAssignedEmployees() != null && p.getAssignedEmployees().stream().anyMatch(e -> e.equalsIgnoreCase(canonicalEmpId))) {
                                return true;
                            }
                            if (p.getAssignedInterns() != null && p.getAssignedInterns().stream().anyMatch(i -> i.equalsIgnoreCase(canonicalEmpId))) {
                                return true;
                            }
                            if (p.getMembers() != null && p.getMembers().stream().anyMatch(m -> m.getEmployeeId() != null && m.getEmployeeId().equalsIgnoreCase(canonicalEmpId))) {
                                return true;
                            }
                        }

                        if (userId != null && !userId.isBlank()) {
                            if (userId.equalsIgnoreCase(p.getProjectManagerId()) || userId.equalsIgnoreCase(p.getProjectLeadId())) {
                                return true;
                            }
                            if (p.getAssignedEmployees() != null && p.getAssignedEmployees().stream().anyMatch(e -> e.equalsIgnoreCase(userId))) {
                                return true;
                            }
                            if (p.getAssignedInterns() != null && p.getAssignedInterns().stream().anyMatch(i -> i.equalsIgnoreCase(userId))) {
                                return true;
                            }
                            if (p.getMembers() != null && p.getMembers().stream().anyMatch(m -> m.getEmployeeId() != null && m.getEmployeeId().equalsIgnoreCase(userId))) {
                                return true;
                            }
                        }

                        if (email != null && !email.isBlank()) {
                            if (email.equalsIgnoreCase(p.getProjectManagerId()) || email.equalsIgnoreCase(p.getProjectLeadId())) {
                                return true;
                            }
                            if (p.getAssignedEmployees() != null && p.getAssignedEmployees().stream().anyMatch(e -> e.equalsIgnoreCase(email))) {
                                return true;
                            }
                            if (p.getAssignedInterns() != null && p.getAssignedInterns().stream().anyMatch(i -> i.equalsIgnoreCase(email))) {
                                return true;
                            }
                            if (p.getMembers() != null && p.getMembers().stream().anyMatch(m -> m.getEmployeeId() != null && m.getEmployeeId().equalsIgnoreCase(email))) {
                                return true;
                            }
                        }

                        return false;
                    }).collect(Collectors.toList());
                }
            }
        }

        if (status != null) {
            projects = projects.stream().filter(p -> p.getStatus() == status).collect(Collectors.toList());
        }

        if (category != null && !category.isBlank()) {
            projects = projects.stream().filter(p -> category.equalsIgnoreCase(p.getCategory())).collect(Collectors.toList());
        }

        return projects.stream().map(this::mapToResponseDTO).collect(Collectors.toList());
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
        deleteProject(id, "SYSTEM", "ROLE_SYSTEM");
    }

    public void deleteProject(String id, String currentUser, String currentRole) {
        Project project = getProjectEntity(id);
        logActivity(id, "DELETE_PROJECT", currentUser, currentRole, "Project", project.getProjectName(), "Deleted");
        projectRepository.delete(project);
    }

    public ProjectResponseDTO addMember(String projectId, ProjectMemberDTO memberDTO) {
        Project project = getProjectEntity(projectId);

        boolean exists = project.getMembers().stream()
                .anyMatch(m -> m.getEmployeeId().equals(memberDTO.getEmployeeId()));
        if (exists) {
            throw new BadRequestException("Employee is already a member of this project");
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

        FileUploadResponse uploadRes = fileStorageService.uploadFile(uploadReq);

        ProjectDocument doc = ProjectDocument.builder()
                .id(UUID.randomUUID().toString())
                .fileName(file.getOriginalFilename())
                .fileUrl(uploadRes != null ? uploadRes.getSecureUrl() : "")
                .fileType(file.getContentType())
                .fileSize(file.getSize())
                .uploadedBy(uploadedBy)
                .uploadedAt(Instant.now())
                .build();

        project.getDocuments().add(doc);
        Project saved = projectRepository.save(project);
        return mapToResponseDTO(saved);
    }

    public List<ProjectStatusHistory> getStatusHistory(String id) {
        return statusHistoryRepository.findByProjectIdOrderByChangedAtDesc(id);
    }

    public List<ProjectActivity> getActivities(String id) {
        return activityRepository.findByProjectIdOrderByTimestampDesc(id);
    }

    private Project getProjectEntity(String id) {
        if (id == null) throw new BadRequestException("Project id is required");
        Project found = mongoTemplate.findById(id, Project.class);
        if (found != null) return found;

        return projectRepository.findByProjectCode(id)
                .or(() -> projectRepository.findByProjectId(id))
                .orElseThrow(() -> new BadRequestException("Project not found with id or code: " + id));
    }

    private String resolveEmployeeName(String employeeId) {
        if (employeeId == null || employeeId.isBlank() || employeeId.toLowerCase().contains("unassigned")) return "Unassigned";
        try {
            return employeeRepository.findByEmployeeId(employeeId)
                    .map(e -> e.getFirstName() + " " + e.getLastName())
                    .orElseGet(() -> {
                        return employeeRepository.findByOfficialEmail(employeeId)
                                .map(e -> e.getFirstName() + " " + e.getLastName())
                                .orElse("Unassigned");
                    });
        } catch (Exception ex) {
            log.warn("Could not resolve employee name for id '{}': {}", employeeId, ex.getMessage());
            return "Unassigned";
        }
    }

    private void logStatusChange(String projectId, ProjectStatus oldStatus, ProjectStatus newStatus, String reason, String changedBy) {
        ProjectStatusHistory history = ProjectStatusHistory.builder()
                .projectId(projectId)
                .oldStatus(oldStatus)
                .newStatus(newStatus)
                .reason(reason)
                .changedBy(changedBy)
                .changedAt(Instant.now())
                .build();
        statusHistoryRepository.save(history);
    }

    private void logActivity(String projectId, String action, String performedBy, String userRole, String fieldModified, String oldValue, String newValue) {
        Project project = null;
        try {
            project = getProjectEntity(projectId);
        } catch (Exception e) {}
        String code = project != null ? project.getProjectCode() : projectId;
        String pId = project != null ? project.getId() : projectId;

        String type = "PROJECT";
        String upperAction = (action != null ? action : "").toUpperCase();
        if (upperAction.contains("STATUS")) type = "STATUS";
        else if (upperAction.contains("REPO") || upperAction.contains("LINK")) type = "REPOSITORY";
        else if (upperAction.contains("TEAM") || upperAction.contains("MEMBER")) type = "TEAM";
        else if (upperAction.contains("TASK")) type = "TASK";
        else if (upperAction.contains("DOC") || upperAction.contains("FILE")) type = "DOCUMENT";
        else if (upperAction.contains("MEETING") || upperAction.contains("SYNC")) type = "MEETING";
        else if (upperAction.contains("PLAN") || upperAction.contains("MILESTONE")) type = "PLANNING";

        String desc = (action != null ? action.replace("_", " ") : "Action");
        if (fieldModified != null && !fieldModified.isBlank()) {
            desc += " (" + fieldModified + ")";
        }
        if (oldValue != null && newValue != null) {
            desc += ": " + oldValue + " → " + newValue;
        } else if (newValue != null) {
            desc += ": " + newValue;
        }

        projectActivityService.logActivity(pId, code, action, type, desc, fieldModified, oldValue, newValue);
    }

    private ProjectResponseDTO mapToResponseDTO(Project project) {
        List<Task> projectTasks = taskRepository.findByProjectId(project.getId());
        int totalTasks = projectTasks.size();
        int completedTasks = (int) projectTasks.stream().filter(t -> t.getStatus() == TaskStatus.DONE || t.getStatus() == TaskStatus.COMPLETED).count();

        double progress = totalTasks > 0 ? ((double) completedTasks / totalTasks) * 100.0 : (project.getProgressPercentage() != null ? project.getProgressPercentage() : 0.0);

        String pmId = project.getProjectManagerId();
        String pmName = resolveEmployeeName(pmId);
        if ("Unassigned".equals(pmName) && project.getProjectManagerName() != null) {
            String clean = project.getProjectManagerName().replaceAll("\\s*\\([^)]*\\)", "").trim();
            if (!clean.isBlank() && !clean.toLowerCase().contains("unassigned") && !clean.startsWith("EMP-") && !clean.startsWith("EXEC-")) {
                pmName = clean;
            }
        }

        String leadId = project.getProjectLeadId();
        String leadName = resolveEmployeeName(leadId);
        if ("Unassigned".equals(leadName) && project.getProjectLeadName() != null) {
            String clean = project.getProjectLeadName().replaceAll("\\s*\\([^)]*\\)", "").trim();
            if (!clean.isBlank() && !clean.toLowerCase().contains("unassigned") && !clean.startsWith("EMP-") && !clean.startsWith("EXEC-")) {
                leadName = clean;
            } else {
                leadName = "Unassigned";
            }
        }

        String pName = project.getProjectName();
        if (pName == null || pName.isBlank()) {
            pName = project.getProjectCode() != null ? project.getProjectCode() : "Untitled Project";
        }

        return ProjectResponseDTO.builder()
                .id(project.getId())
                .projectId(project.getProjectId())
                .projectCode(project.getProjectCode())
                .projectName(pName)
                .shortName(project.getShortName())
                .description(project.getDescription())
                .objectives(project.getObjectives())
                .client(project.getClient())
                .clientId(project.getClientId())
                .clientOrganization(project.getClientOrganization())
                .department(project.getDepartment())
                .category(project.getCategory())
                .businessUnit(project.getBusinessUnit())
                .projectType(project.getProjectType())
                .status(project.getStatus())
                .priority(project.getPriority())
                .startDate(project.getStartDate())
                .endDate(project.getEndDate())
                .targetEndDate(project.getTargetEndDate())
                .estimatedCompletion(project.getEstimatedCompletion())
                .estimatedHours(project.getEstimatedHours())
                .estimatedDuration(project.getEstimatedDuration())
                .budget(project.getBudget())
                .estimatedCost(project.getEstimatedCost())
                .progressPercentage(progress)
                .technologyStack(project.getTechnologyStack())
                .programmingLanguages(project.getProgrammingLanguages())
                .frameworks(project.getFrameworks())
                .databaseTech(project.getDatabaseTech())
                .cloudProvider(project.getCloudProvider())
                .repositoryUrl(project.getRepositoryUrl())
                .repositoryType(project.getRepositoryType())
                .repositoryVisibility(project.getRepositoryVisibility())
                .projectVisibility(project.getProjectVisibility())
                .deploymentType(project.getDeploymentType())
                .projectManagerId(pmId)
                .projectManagerName(pmName)
                .projectLeadId(leadId)
                .projectLeadName(leadName)
                .projectSponsor(project.getProjectSponsor())
                .customerRepresentative(project.getCustomerRepresentative())
                .assignedEmployees(project.getAssignedEmployees())
                .assignedInterns(project.getAssignedInterns())
                .links(project.getLinks() != null ? project.getLinks() : new ProjectLinks())
                .remarks(project.getRemarks())
                .tags(project.getTags())
                .members(project.getMembers())
                .teams(project.getTeams())
                .documents(project.getDocuments())
                .logoUrl(project.getLogoUrl())
                .pendingStatusRequest(project.getPendingStatusRequest())
                .overallProgressPercentage(progress)
                .totalTasks(totalTasks)
                .completedTasks(completedTasks)
                .createdAt(project.getCreatedAt())
                .updatedAt(project.getUpdatedAt())
                .createdBy(project.getCreatedBy())
                .updatedBy(project.getUpdatedBy())
                .build();
    }
}
