package com.techknife.employee.service.impl;

import com.techknife.backend.dto.PagedResponse;
import com.techknife.backend.event.EmployeeCreatedEvent;
import com.techknife.backend.event.EmployeeStatusChangedEvent;
import com.techknife.backend.exception.BadRequestException;
import com.techknife.backend.exception.ResourceNotFoundException;
import com.techknife.employee.dto.BulkImportError;
import com.techknife.employee.dto.BulkImportResult;
import com.techknife.employee.dto.BulkTransferDepartmentRequest;
import com.techknife.employee.dto.BulkUpdateStatusRequest;
import com.techknife.employee.dto.CreateEmployeeRequest;
import com.techknife.employee.dto.EmployeeDTO;
import com.techknife.employee.dto.EmployeeResponse;
import com.techknife.employee.dto.EmployeeSearchFilter;
import com.techknife.employee.dto.EmployeeSummaryResponse;
import com.techknife.employee.dto.OrgTreeNode;
import com.techknife.employee.dto.ReportingHierarchyResponse;
import com.techknife.employee.dto.UpdateEmployeeRequest;
import com.techknife.employee.dto.UpdateEmployeeStatusRequest;
import com.techknife.employee.entity.DocumentType;
import com.techknife.employee.entity.Education;
import com.techknife.employee.entity.Employee;
import com.techknife.employee.entity.EmployeeDocument;
import com.techknife.employee.entity.EmployeeStatus;
import com.techknife.employee.entity.EmployeeTimeline;
import com.techknife.employee.entity.EmployeeTimelineRecord;
import com.techknife.employee.entity.EmploymentType;
import com.techknife.employee.entity.Experience;
import com.techknife.employee.entity.Skill;
import com.techknife.employee.entity.TimelineEventType;
import com.techknife.employee.repository.EmployeeRepository;

import com.techknife.employee.repository.EmployeeTimelineRepository;
import com.techknife.employee.service.EmployeeService;
import com.techknife.storage.FileStorageService;
import com.techknife.storage.FileUploadResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Production-ready Service Implementation for Employee management module.
 */
@Slf4j
@Service("employeeFeatureServiceImpl")
@RequiredArgsConstructor
public class EmployeeServiceImpl implements EmployeeService {

    private final EmployeeRepository employeeRepository;
    private final EmployeeTimelineRepository timelineRepository;
    private final FileStorageService fileStorageService;
    private final ApplicationEventPublisher eventPublisher;

    @Override
    @Transactional
    public EmployeeResponse createEmployee(CreateEmployeeRequest request) {
        log.info("Creating new employee with ID: {} and email: {}", request.getEmployeeId(), request.getOfficialEmail());

        if (employeeRepository.existsByEmployeeId(request.getEmployeeId())) {
            throw new BadRequestException("Employee ID '" + request.getEmployeeId() + "' already exists");
        }

        if (employeeRepository.existsByOfficialEmail(request.getOfficialEmail())) {
            throw new BadRequestException("Official Email '" + request.getOfficialEmail() + "' already exists");
        }

        Employee employee = mapToEntity(request);
        Employee saved = employeeRepository.save(employee);
        log.info("Successfully created employee document with ID: {}", saved.getId());

        recordTimeline(saved.getId(), "EMPLOYEE_CREATED", "INITIAL", saved.getStatus().name(), "Employee Onboarded", LocalDate.now());
        eventPublisher.publishEvent(new EmployeeCreatedEvent(this, saved));

        return mapToResponse(saved);
    }

    @Override
    @Transactional
    public EmployeeResponse updateEmployee(String id, UpdateEmployeeRequest request) {
        log.info("Updating employee with document ID: {}", id);

        Employee employee = employeeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Employee", "id", id));

        String oldDept = employee.getDepartmentId();
        String oldDesignation = employee.getDesignationId();
        String oldManager = employee.getManagerId();

        updateEntityFromRequest(request, employee);
        Employee updated = employeeRepository.save(employee);

        if (request.getDepartmentId() != null && !request.getDepartmentId().equals(oldDept)) {
            recordTimeline(updated.getId(), "DEPARTMENT_CHANGE", oldDept, updated.getDepartmentId(), "Department Transferred", LocalDate.now());
        }
        if (request.getDesignationId() != null && !request.getDesignationId().equals(oldDesignation)) {
            recordTimeline(updated.getId(), "DESIGNATION_CHANGE", oldDesignation, updated.getDesignationId(), "Designation Updated", LocalDate.now());
        }
        if (request.getManagerId() != null && !request.getManagerId().equals(oldManager)) {
            recordTimeline(updated.getId(), "MANAGER_CHANGE", oldManager, updated.getManagerId(), "Reporting Manager Changed", LocalDate.now());
        }

        log.info("Successfully updated employee document with ID: {}", updated.getId());
        return mapToResponse(updated);
    }

    @Override
    @Transactional(readOnly = true)
    public EmployeeResponse getEmployeeById(String id) {
        Employee employee = employeeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Employee", "id", id));
        return mapToResponse(employee);
    }

    @Override
    @Transactional(readOnly = true)
    public EmployeeResponse getEmployeeByEmployeeId(String employeeId) {
        Employee employee = employeeRepository.findByEmployeeId(employeeId)
                .orElseThrow(() -> new ResourceNotFoundException("Employee", "employeeId", employeeId));
        return mapToResponse(employee);
    }

    @Override
    @Transactional(readOnly = true)
    public EmployeeResponse getEmployeeByOfficialEmail(String officialEmail) {
        Employee employee = employeeRepository.findByOfficialEmail(officialEmail)
                .orElseThrow(() -> new ResourceNotFoundException("Employee", "officialEmail", officialEmail));
        return mapToResponse(employee);
    }

    @Override
    @Transactional(readOnly = true)
    public PagedResponse<EmployeeResponse> getAllEmployees(int page, int size, String search, String departmentId, String managerId, String status) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        Page<Employee> employeePage;

        if (search != null && !search.trim().isEmpty()) {
            employeePage = employeeRepository.searchByName(search.trim(), pageable);
        } else if (departmentId != null && !departmentId.trim().isEmpty()) {
            employeePage = employeeRepository.findByDepartmentId(departmentId.trim(), pageable);
        } else if (managerId != null && !managerId.trim().isEmpty()) {
            employeePage = employeeRepository.findByManagerId(managerId.trim(), pageable);
        } else if (status != null && !status.trim().isEmpty()) {
            try {
                EmployeeStatus employeeStatus = EmployeeStatus.valueOf(status.trim().toUpperCase());
                employeePage = employeeRepository.findByStatus(employeeStatus, pageable);
            } catch (IllegalArgumentException e) {
                throw new BadRequestException("Invalid employee status: " + status);
            }
        } else {
            employeePage = employeeRepository.findAll(pageable);
        }

        List<EmployeeResponse> content = employeePage.getContent().stream()
                .filter(emp -> emp != null && (emp.getEmploymentType() == null || !emp.getEmploymentType().name().equalsIgnoreCase("INTERN")) && (emp.getEmployeeId() == null || !emp.getEmployeeId().toUpperCase().startsWith("INT-")))
                .map(this::mapToResponse)
                .collect(Collectors.toList());

        return PagedResponse.<EmployeeResponse>builder()
                .content(content)
                .page(employeePage.getNumber())
                .size(employeePage.getSize())
                .totalElements(employeePage.getTotalElements())
                .totalPages(employeePage.getTotalPages())
                .last(employeePage.isLast())
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public PagedResponse<EmployeeSummaryResponse> searchEmployees(EmployeeSearchFilter filter) {
        Sort.Direction direction = "ASC".equalsIgnoreCase(filter.getSortDirection()) ? Sort.Direction.ASC : Sort.Direction.DESC;
        String sortBy = filter.getSortBy() != null ? filter.getSortBy() : "createdAt";
        Pageable pageable = PageRequest.of(filter.getPage(), filter.getSize(), Sort.by(direction, sortBy));

        Page<Employee> employeePage = employeeRepository.searchEmployees(
                filter.getSearchTerm(),
                filter.getDepartmentId(),
                filter.getDesignationId(),
                filter.getManagerId(),
                filter.getStatus(),
                filter.getEmploymentType(),
                filter.getBloodGroup(),
                filter.getSkills(),
                pageable
        );

        List<EmployeeSummaryResponse> content = employeePage.getContent().stream()
                .map(this::mapToSummaryResponse)
                .collect(Collectors.toList());

        return PagedResponse.<EmployeeSummaryResponse>builder()
                .content(content)
                .page(employeePage.getNumber())
                .size(employeePage.getSize())
                .totalElements(employeePage.getTotalElements())
                .totalPages(employeePage.getTotalPages())
                .last(employeePage.isLast())
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public List<EmployeeResponse> getEmployeesByDepartment(String departmentId) {
        return employeeRepository.findByDepartmentId(departmentId).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<EmployeeResponse> getDirectReports(String managerId) {
        return employeeRepository.findByManagerId(managerId).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void deleteEmployee(String id) {
        log.info("Deleting employee with document ID: {}", id);
        if (!employeeRepository.existsById(id)) {
            throw new ResourceNotFoundException("Employee", "id", id);
        }
        employeeRepository.deleteById(id);
        log.info("Successfully deleted employee with ID: {}", id);
    }

    @Override
    @Transactional
    public EmployeeResponse updateEmployeeStatus(String id, UpdateEmployeeStatusRequest request) {
        return changeEmployeeStatus(id, request.getStatus(), request.getRemarks());
    }

    @Override
    @Transactional
    public EmployeeResponse updateEmployeeStatus(String id, EmployeeStatus status) {
        return changeEmployeeStatus(id, status, "Status Updated");
    }

    @Override
    @Transactional
    public EmployeeResponse changeEmployeeStatus(String id, EmployeeStatus newStatus, String reason) {
        Employee employee = employeeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Employee", "id", id));

        EmployeeStatus oldStatus = employee.getStatus();
        if (oldStatus == newStatus) {
            return mapToResponse(employee);
        }

        employee.setStatus(newStatus);
        Employee saved = employeeRepository.save(employee);

        recordTimeline(saved.getId(), "STATUS_CHANGE", oldStatus != null ? oldStatus.name() : "NONE", newStatus.name(), reason, LocalDate.now());
        eventPublisher.publishEvent(new EmployeeStatusChangedEvent(this, saved, oldStatus, newStatus));

        return mapToResponse(saved);
    }

    @Override
    @Transactional
    public EmployeeResponse activateEmployee(String id, String reason) {
        return changeEmployeeStatus(id, EmployeeStatus.ACTIVE, reason != null ? reason : "Employee Activated");
    }

    @Override
    @Transactional
    public EmployeeResponse deactivateEmployee(String id, String reason) {
        return changeEmployeeStatus(id, EmployeeStatus.INACTIVE, reason != null ? reason : "Employee Deactivated");
    }

    @Override
    @Transactional
    public EmployeeResponse suspendEmployee(String id, String reason) {
        return changeEmployeeStatus(id, EmployeeStatus.SUSPENDED, reason != null ? reason : "Employee Suspended");
    }

    @Override
    @Transactional
    public EmployeeResponse terminateEmployee(String id, String reason) {
        return changeEmployeeStatus(id, EmployeeStatus.TERMINATED, reason != null ? reason : "Employee Terminated");
    }

    @Override
    @Transactional
    public EmployeeResponse resignEmployee(String id, String reason) {
        return changeEmployeeStatus(id, EmployeeStatus.RESIGNED, reason != null ? reason : "Employee Resigned");
    }

    @Override
    @Transactional
    public EmployeeResponse retireEmployee(String id, String reason) {
        return changeEmployeeStatus(id, EmployeeStatus.RETIRED, reason != null ? reason : "Employee Retired");
    }

    @Override
    @Transactional(readOnly = true)
    public List<EmployeeTimelineRecord> getEmployeeTimeline(String employeeId) {
        return timelineRepository.findByEmployeeIdOrderByCreatedAtDesc(employeeId).stream()
                .map(t -> EmployeeTimelineRecord.builder()
                        .id(t.getId())
                        .employeeId(t.getEmployeeId())
                        .changeType(t.getEventType() != null ? t.getEventType().name() : "EVENT")
                        .oldValue(t.getOldValue())
                        .newValue(t.getNewValue())
                        .description(t.getDescription())
                        .changedBy(t.getChangedBy())
                        .createdAt(t.getTimestamp())
                        .build())
                .collect(java.util.stream.Collectors.toList());
    }


    @Override
    @Transactional(readOnly = true)
    public ReportingHierarchyResponse getReportingHierarchy(String employeeId) {
        Employee employee = employeeRepository.findById(employeeId)
                .orElseThrow(() -> new ResourceNotFoundException("Employee", "id", employeeId));

        EmployeeSummaryResponse directManager = null;
        EmployeeSummaryResponse skipLevelManager = null;

        if (employee.getManagerId() != null && !employee.getManagerId().trim().isEmpty()) {
            Employee mgr = employeeRepository.findById(employee.getManagerId()).orElse(null);
            if (mgr != null) {
                directManager = mapToSummaryResponse(mgr);
                if (mgr.getManagerId() != null && !mgr.getManagerId().trim().isEmpty()) {
                    Employee skipMgr = employeeRepository.findById(mgr.getManagerId()).orElse(null);
                    if (skipMgr != null) {
                        skipLevelManager = mapToSummaryResponse(skipMgr);
                    }
                }
            }
        }

        List<EmployeeSummaryResponse> directReports = employeeRepository.findByManagerId(employee.getId()).stream()
                .map(this::mapToSummaryResponse)
                .collect(Collectors.toList());

        String fullName = (employee.getFirstName() + " " + employee.getLastName()).trim();

        return ReportingHierarchyResponse.builder()
                .id(employee.getId())
                .employeeId(employee.getEmployeeId())
                .fullName(fullName)
                .designationId(employee.getDesignationId())
                .departmentId(employee.getDepartmentId())
                .directManager(directManager)
                .skipLevelManager(skipLevelManager)
                .directReports(directReports)
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public OrgTreeNode getOrgTree(String rootEmployeeId) {
        Employee root = employeeRepository.findById(rootEmployeeId)
                .orElseThrow(() -> new ResourceNotFoundException("Employee", "id", rootEmployeeId));
        return buildOrgTreeNode(root);
    }

    private OrgTreeNode buildOrgTreeNode(Employee employee) {
        String fullName = (employee.getFirstName() + " " + employee.getLastName()).trim();
        OrgTreeNode node = OrgTreeNode.builder()
                .id(employee.getId())
                .employeeId(employee.getEmployeeId())
                .fullName(fullName)
                .officialEmail(employee.getOfficialEmail())
                .designationId(employee.getDesignationId())
                .departmentId(employee.getDepartmentId())
                .profileImage(employee.getProfileImage())
                .children(new ArrayList<>())
                .build();

        List<Employee> subordinates = employeeRepository.findByManagerId(employee.getId());
        for (Employee sub : subordinates) {
            node.getChildren().add(buildOrgTreeNode(sub));
        }

        return node;
    }

    @Override
    @Transactional
    public List<EmployeeResponse> bulkUpdateStatus(BulkUpdateStatusRequest request) {
        List<EmployeeResponse> updatedList = new ArrayList<>();
        for (String empId : request.getEmployeeIds()) {
            try {
                EmployeeResponse res = changeEmployeeStatus(empId, request.getStatus(), request.getRemarks());
                updatedList.add(res);
            } catch (Exception e) {
                log.warn("Failed bulk status update for employee {}: {}", empId, e.getMessage());
            }
        }
        return updatedList;
    }

    @Override
    @Transactional
    public List<EmployeeResponse> bulkTransferDepartment(BulkTransferDepartmentRequest request) {
        List<EmployeeResponse> updatedList = new ArrayList<>();
        for (String empId : request.getEmployeeIds()) {
            try {
                Employee employee = employeeRepository.findById(empId).orElse(null);
                if (employee != null) {
                    String oldDept = employee.getDepartmentId();
                    employee.setDepartmentId(request.getTargetDepartmentId());
                    Employee saved = employeeRepository.save(employee);
                    recordTimeline(saved.getId(), "DEPARTMENT_CHANGE", oldDept, request.getTargetDepartmentId(),
                            request.getRemarks() != null ? request.getRemarks() : "Bulk Department Transfer", LocalDate.now());
                    updatedList.add(mapToResponse(saved));
                }
            } catch (Exception e) {
                log.warn("Failed bulk department transfer for employee {}: {}", empId, e.getMessage());
            }
        }
        return updatedList;
    }

    @Override
    @Transactional
    public BulkImportResult bulkImportEmployees(MultipartFile file) {
        BulkImportResult result = BulkImportResult.builder().build();
        List<BulkImportError> errors = new ArrayList<>();

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(file.getInputStream(), StandardCharsets.UTF_8))) {
            String headerLine = reader.readLine();
            if (headerLine == null) {
                throw new BadRequestException("Import file is empty");
            }

            String line;
            int rowNumber = 1;
            int successCount = 0;

            while ((line = reader.readLine()) != null) {
                rowNumber++;
                if (line.trim().isEmpty()) continue;

                String[] fields = line.split(",");
                if (fields.length < 5) {
                    errors.add(BulkImportError.builder()
                            .rowNumber(rowNumber)
                            .errorMessage("Invalid number of columns. Required: employeeId, officialEmail, firstName, lastName, primaryMobile")
                            .build());
                    continue;
                }

                String empId = fields[0].trim();
                String email = fields[1].trim();
                String firstName = fields[2].trim();
                String lastName = fields[3].trim();
                String mobile = fields[4].trim();

                if (employeeRepository.existsByEmployeeId(empId)) {
                    errors.add(BulkImportError.builder()
                            .rowNumber(rowNumber)
                            .employeeId(empId)
                            .fieldName("employeeId")
                            .errorMessage("Employee ID already exists")
                            .build());
                    continue;
                }

                if (employeeRepository.existsByOfficialEmail(email)) {
                    errors.add(BulkImportError.builder()
                            .rowNumber(rowNumber)
                            .employeeId(empId)
                            .fieldName("officialEmail")
                            .errorMessage("Official email already exists")
                            .build());
                    continue;
                }

                Employee employee = Employee.builder()
                        .employeeId(empId)
                        .officialEmail(email)
                        .firstName(firstName)
                        .lastName(lastName)
                        .primaryMobile(mobile)
                        .joiningDate(LocalDate.now())
                        .employmentType(EmploymentType.FULL_TIME)
                        .status(EmployeeStatus.ACTIVE)
                        .build();

                Employee saved = employeeRepository.save(employee);
                recordTimeline(saved.getId(), "EMPLOYEE_CREATED", "NONE", "ACTIVE", "Bulk Imported", LocalDate.now());
                successCount++;
            }

            result.setTotalProcessed(rowNumber - 1);
            result.setSuccessCount(successCount);
            result.setFailureCount(errors.size());
            result.setErrors(errors);

        } catch (Exception e) {
            log.error("Bulk import failed: {}", e.getMessage(), e);
            throw new BadRequestException("Failed to process bulk import file: " + e.getMessage());
        }

        return result;
    }

    @Override
    @Transactional(readOnly = true)
    public byte[] exportEmployees(String format) {
        List<Employee> employees = employeeRepository.findAll();
        StringBuilder sb = new StringBuilder();
        sb.append("Employee ID,Official Email,First Name,Last Name,Mobile,Department,Employment Type,Status,Joining Date\n");

        for (Employee emp : employees) {
            sb.append(escapeCsv(emp.getEmployeeId())).append(",")
              .append(escapeCsv(emp.getOfficialEmail())).append(",")
              .append(escapeCsv(emp.getFirstName())).append(",")
              .append(escapeCsv(emp.getLastName())).append(",")
              .append(escapeCsv(emp.getPrimaryMobile())).append(",")
              .append(escapeCsv(emp.getDepartmentId())).append(",")
              .append(emp.getEmploymentType() != null ? emp.getEmploymentType().name() : "").append(",")
              .append(emp.getStatus() != null ? emp.getStatus().name() : "").append(",")
              .append(emp.getJoiningDate() != null ? emp.getJoiningDate().toString() : "").append("\n");
        }

        return sb.toString().getBytes(StandardCharsets.UTF_8);
    }

    private String escapeCsv(String input) {
        if (input == null) return "";
        if (input.contains(",") || input.contains("\"") || input.contains("\n")) {
            return "\"" + input.replace("\"", "\"\"") + "\"";
        }
        return input;
    }

    private void recordTimeline(String employeeId, String changeType, String oldValue, String newValue, String description, LocalDate effectiveDate) {
        TimelineEventType type;
        try {
            type = TimelineEventType.valueOf(changeType);
        } catch (Exception e) {
            type = TimelineEventType.STATUS_CHANGE;
        }
        EmployeeTimeline record = EmployeeTimeline.builder()
                .employeeId(employeeId)
                .eventType(type)
                .oldValue(oldValue)
                .newValue(newValue)
                .description(description)
                .timestamp(Instant.now())
                .build();
        timelineRepository.save(record);
    }


    @Override
    @Transactional(readOnly = true)
    public EmployeeDTO getEmployeeDtoById(String id) {
        Employee employee = employeeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Employee", "id", id));
        return mapToDto(employee);
    }

    // Sub-resource implementations
    @Override
    @Transactional
    public EmployeeDocument uploadEmployeeDocument(String employeeId, MultipartFile file, DocumentType documentType) {
        Employee employee = employeeRepository.findById(employeeId)
                .orElseThrow(() -> new ResourceNotFoundException("Employee", "id", employeeId));

        FileUploadResponse uploadResponse = fileStorageService.uploadDocument(file, "employees/" + employeeId + "/documents");

        EmployeeDocument doc = EmployeeDocument.builder()
                .id(UUID.randomUUID().toString())
                .documentType(documentType != null ? documentType : DocumentType.OTHER_DOCUMENTS)
                .documentName(file.getOriginalFilename())
                .documentUrl(uploadResponse.getSecureUrl())
                .publicId(uploadResponse.getPublicId())
                .fileSize(file.getSize())
                .uploadedAt(Instant.now())
                .build();

        if (employee.getDocuments() == null) {
            employee.setDocuments(new ArrayList<>());
        }
        employee.getDocuments().add(doc);
        employeeRepository.save(employee);
        log.info("Uploaded document {} for employee {}", doc.getId(), employeeId);
        return doc;
    }

    @Override
    @Transactional(readOnly = true)
    public List<EmployeeDocument> getEmployeeDocuments(String employeeId) {
        Employee employee = employeeRepository.findById(employeeId)
                .orElseThrow(() -> new ResourceNotFoundException("Employee", "id", employeeId));
        return employee.getDocuments() != null ? employee.getDocuments() : new ArrayList<>();
    }

    @Override
    @Transactional
    public void deleteEmployeeDocument(String employeeId, String documentId) {
        Employee employee = employeeRepository.findById(employeeId)
                .orElseThrow(() -> new ResourceNotFoundException("Employee", "id", employeeId));

        if (employee.getDocuments() != null) {
            EmployeeDocument targetDoc = employee.getDocuments().stream()
                    .filter(d -> d.getId().equals(documentId))
                    .findFirst()
                    .orElseThrow(() -> new ResourceNotFoundException("EmployeeDocument", "id", documentId));

            if (targetDoc.getPublicId() != null) {
                try {
                    fileStorageService.deleteFile(targetDoc.getPublicId());
                } catch (Exception e) {
                    log.warn("Failed to delete file from cloud storage: {}", e.getMessage());
                }
            }

            employee.getDocuments().removeIf(d -> d.getId().equals(documentId));
            employeeRepository.save(employee);
            log.info("Deleted document {} for employee {}", documentId, employeeId);
        }
    }

    @Override
    @Transactional
    public EmployeeResponse addSkill(String employeeId, Skill skill) {
        Employee employee = employeeRepository.findById(employeeId)
                .orElseThrow(() -> new ResourceNotFoundException("Employee", "id", employeeId));

        if (skill.getId() == null) {
            skill.setId(UUID.randomUUID().toString());
        }
        if (employee.getSkillDetails() == null) {
            employee.setSkillDetails(new ArrayList<>());
        }
        employee.getSkillDetails().add(skill);

        if (skill.getSkillName() != null && !employee.getSkills().contains(skill.getSkillName())) {
            employee.getSkills().add(skill.getSkillName());
        }

        Employee updated = employeeRepository.save(employee);
        return mapToResponse(updated);
    }

    @Override
    @Transactional
    public EmployeeResponse updateSkill(String employeeId, String skillId, Skill skill) {
        Employee employee = employeeRepository.findById(employeeId)
                .orElseThrow(() -> new ResourceNotFoundException("Employee", "id", employeeId));

        if (employee.getSkillDetails() != null) {
            for (int i = 0; i < employee.getSkillDetails().size(); i++) {
                if (employee.getSkillDetails().get(i).getId().equals(skillId)) {
                    skill.setId(skillId);
                    employee.getSkillDetails().set(i, skill);
                    break;
                }
            }
        }

        Employee updated = employeeRepository.save(employee);
        return mapToResponse(updated);
    }

    @Override
    @Transactional
    public EmployeeResponse deleteSkill(String employeeId, String skillId) {
        Employee employee = employeeRepository.findById(employeeId)
                .orElseThrow(() -> new ResourceNotFoundException("Employee", "id", employeeId));

        if (employee.getSkillDetails() != null) {
            employee.getSkillDetails().removeIf(s -> s.getId().equals(skillId));
        }

        Employee updated = employeeRepository.save(employee);
        return mapToResponse(updated);
    }

    @Override
    @Transactional
    public EmployeeResponse addEducation(String employeeId, Education education) {
        Employee employee = employeeRepository.findById(employeeId)
                .orElseThrow(() -> new ResourceNotFoundException("Employee", "id", employeeId));

        if (education.getId() == null) {
            education.setId(UUID.randomUUID().toString());
        }
        if (employee.getEducation() == null) {
            employee.setEducation(new ArrayList<>());
        }
        employee.getEducation().add(education);

        Employee updated = employeeRepository.save(employee);
        return mapToResponse(updated);
    }

    @Override
    @Transactional
    public EmployeeResponse updateEducation(String employeeId, String educationId, Education education) {
        Employee employee = employeeRepository.findById(employeeId)
                .orElseThrow(() -> new ResourceNotFoundException("Employee", "id", employeeId));

        if (employee.getEducation() != null) {
            for (int i = 0; i < employee.getEducation().size(); i++) {
                if (employee.getEducation().get(i).getId().equals(educationId)) {
                    education.setId(educationId);
                    employee.getEducation().set(i, education);
                    break;
                }
            }
        }

        Employee updated = employeeRepository.save(employee);
        return mapToResponse(updated);
    }

    @Override
    @Transactional
    public EmployeeResponse deleteEducation(String employeeId, String educationId) {
        Employee employee = employeeRepository.findById(employeeId)
                .orElseThrow(() -> new ResourceNotFoundException("Employee", "id", employeeId));

        if (employee.getEducation() != null) {
            employee.getEducation().removeIf(e -> e.getId().equals(educationId));
        }

        Employee updated = employeeRepository.save(employee);
        return mapToResponse(updated);
    }

    @Override
    @Transactional
    public EmployeeResponse addExperience(String employeeId, Experience experience) {
        Employee employee = employeeRepository.findById(employeeId)
                .orElseThrow(() -> new ResourceNotFoundException("Employee", "id", employeeId));

        if (experience.getId() == null) {
            experience.setId(UUID.randomUUID().toString());
        }
        if (employee.getExperience() == null) {
            employee.setExperience(new ArrayList<>());
        }
        employee.getExperience().add(experience);

        Employee updated = employeeRepository.save(employee);
        return mapToResponse(updated);
    }

    @Override
    @Transactional
    public EmployeeResponse updateExperience(String employeeId, String experienceId, Experience experience) {
        Employee employee = employeeRepository.findById(employeeId)
                .orElseThrow(() -> new ResourceNotFoundException("Employee", "id", employeeId));

        if (employee.getExperience() != null) {
            for (int i = 0; i < employee.getExperience().size(); i++) {
                if (employee.getExperience().get(i).getId().equals(experienceId)) {
                    experience.setId(experienceId);
                    employee.getExperience().set(i, experience);
                    break;
                }
            }
        }

        Employee updated = employeeRepository.save(employee);
        return mapToResponse(updated);
    }

    @Override
    @Transactional
    public EmployeeResponse deleteExperience(String employeeId, String experienceId) {
        Employee employee = employeeRepository.findById(employeeId)
                .orElseThrow(() -> new ResourceNotFoundException("Employee", "id", employeeId));

        if (employee.getExperience() != null) {
            employee.getExperience().removeIf(ex -> ex.getId().equals(experienceId));
        }

        Employee updated = employeeRepository.save(employee);
        return mapToResponse(updated);
    }

    private Employee mapToEntity(CreateEmployeeRequest request) {
        if (request == null) return null;

        return Employee.builder()
                .employeeId(request.getEmployeeId())
                .officialEmail(request.getOfficialEmail())
                .personalEmail(request.getPersonalEmail())
                .primaryMobile(request.getPrimaryMobile())
                .alternateMobile(request.getAlternateMobile())
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .gender(request.getGender())
                .dob(request.getDob())
                .bloodGroup(request.getBloodGroup())
                .nationality(request.getNationality())
                .maritalStatus(request.getMaritalStatus())
                .emergencyContact(request.getEmergencyContact())
                .currentAddress(request.getCurrentAddress())
                .permanentAddress(request.getPermanentAddress())
                .companyId(request.getCompanyId())
                .branchId(request.getBranchId())
                .departmentId(request.getDepartmentId())
                .designationId(request.getDesignationId())
                .managerId(request.getManagerId())
                .teamId(request.getTeamId())
                .joiningDate(request.getJoiningDate())
                .probationEndDate(request.getProbationEndDate())
                .confirmationDate(request.getConfirmationDate())
                .employmentType(request.getEmploymentType())
                .salary(request.getSalary())
                .salaryGrade(request.getSalaryGrade())
                .workLocation(request.getWorkLocation())
                .shift(request.getShift())
                .remarks(request.getRemarks())
                .skills(request.getSkills() != null ? new ArrayList<>(request.getSkills()) : new ArrayList<>())
                .skillDetails(request.getSkillDetails() != null ? new ArrayList<>(request.getSkillDetails()) : new ArrayList<>())
                .education(request.getEducation() != null ? new ArrayList<>(request.getEducation()) : new ArrayList<>())
                .experience(request.getExperience() != null ? new ArrayList<>(request.getExperience()) : new ArrayList<>())
                .bankDetails(request.getBankDetails())
                .pan(request.getPan())
                .aadhaar(request.getAadhaar())
                .passport(request.getPassport())
                .drivingLicense(request.getDrivingLicense())
                .githubUsername(request.getGithubUsername())
                .profileImage(request.getProfileImage())
                .status(request.getStatus() != null ? request.getStatus() : EmployeeStatus.ACTIVE)
                .build();
    }

    private EmployeeResponse mapToResponse(Employee entity) {
        if (entity == null) return null;

        String fullName = ((entity.getFirstName() != null ? entity.getFirstName() : "") + " " +
                           (entity.getLastName() != null ? entity.getLastName() : "")).trim();

        return EmployeeResponse.builder()
                .id(entity.getId())
                .employeeId(entity.getEmployeeId())
                .officialEmail(entity.getOfficialEmail())
                .personalEmail(entity.getPersonalEmail())
                .primaryMobile(entity.getPrimaryMobile())
                .alternateMobile(entity.getAlternateMobile())
                .firstName(entity.getFirstName())
                .lastName(entity.getLastName())
                .fullName(fullName)
                .gender(entity.getGender())
                .dob(entity.getDob())
                .bloodGroup(entity.getBloodGroup())
                .nationality(entity.getNationality())
                .maritalStatus(entity.getMaritalStatus())
                .emergencyContact(entity.getEmergencyContact())
                .currentAddress(entity.getCurrentAddress())
                .permanentAddress(entity.getPermanentAddress())
                .companyId(entity.getCompanyId())
                .branchId(entity.getBranchId())
                .departmentId(entity.getDepartmentId())
                .designationId(entity.getDesignationId())
                .managerId(entity.getManagerId())
                .teamId(entity.getTeamId())
                .joiningDate(entity.getJoiningDate())
                .probationEndDate(entity.getProbationEndDate())
                .confirmationDate(entity.getConfirmationDate())
                .employmentType(entity.getEmploymentType())
                .salary(entity.getSalary())
                .salaryGrade(entity.getSalaryGrade())
                .workLocation(entity.getWorkLocation())
                .shift(entity.getShift())
                .remarks(entity.getRemarks())
                .skills(entity.getSkills() != null ? new ArrayList<>(entity.getSkills()) : new ArrayList<>())
                .skillDetails(entity.getSkillDetails() != null ? new ArrayList<>(entity.getSkillDetails()) : new ArrayList<>())
                .education(entity.getEducation() != null ? new ArrayList<>(entity.getEducation()) : new ArrayList<>())
                .experience(entity.getExperience() != null ? new ArrayList<>(entity.getExperience()) : new ArrayList<>())
                .documents(entity.getDocuments() != null ? new ArrayList<>(entity.getDocuments()) : new ArrayList<>())
                .bankDetails(entity.getBankDetails())
                .pan(entity.getPan())
                .aadhaar(entity.getAadhaar())
                .passport(entity.getPassport())
                .drivingLicense(entity.getDrivingLicense())
                .githubUsername(entity.getGithubUsername())
                .profileImage(entity.getProfileImage())
                .status(entity.getStatus())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .createdBy(entity.getCreatedBy())
                .updatedBy(entity.getUpdatedBy())
                .build();
    }

    private EmployeeSummaryResponse mapToSummaryResponse(Employee entity) {
        if (entity == null) return null;

        String fullName = ((entity.getFirstName() != null ? entity.getFirstName() : "") + " " +
                           (entity.getLastName() != null ? entity.getLastName() : "")).trim();

        return EmployeeSummaryResponse.builder()
                .id(entity.getId())
                .employeeId(entity.getEmployeeId())
                .fullName(fullName)
                .officialEmail(entity.getOfficialEmail())
                .primaryMobile(entity.getPrimaryMobile())
                .departmentId(entity.getDepartmentId())
                .designationId(entity.getDesignationId())
                .employmentType(entity.getEmploymentType())
                .status(entity.getStatus())
                .profileImage(entity.getProfileImage())
                .joiningDate(entity.getJoiningDate())
                .build();
    }

    private EmployeeDTO mapToDto(Employee entity) {
        if (entity == null) return null;

        return EmployeeDTO.builder()
                .id(entity.getId())
                .employeeId(entity.getEmployeeId())
                .officialEmail(entity.getOfficialEmail())
                .personalEmail(entity.getPersonalEmail())
                .primaryMobile(entity.getPrimaryMobile())
                .alternateMobile(entity.getAlternateMobile())
                .firstName(entity.getFirstName())
                .lastName(entity.getLastName())
                .gender(entity.getGender())
                .dob(entity.getDob())
                .bloodGroup(entity.getBloodGroup())
                .departmentId(entity.getDepartmentId())
                .designationId(entity.getDesignationId())
                .managerId(entity.getManagerId())
                .joiningDate(entity.getJoiningDate())
                .employmentType(entity.getEmploymentType())
                .salary(entity.getSalary())
                .skills(entity.getSkills() != null ? new ArrayList<>(entity.getSkills()) : new ArrayList<>())
                .githubUsername(entity.getGithubUsername())
                .profileImage(entity.getProfileImage())
                .status(entity.getStatus())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .createdBy(entity.getCreatedBy())
                .updatedBy(entity.getUpdatedBy())
                .build();
    }

    private void updateEntityFromRequest(UpdateEmployeeRequest request, Employee entity) {
        if (request == null || entity == null) return;

        if (request.getPersonalEmail() != null) entity.setPersonalEmail(request.getPersonalEmail());
        if (request.getPrimaryMobile() != null) entity.setPrimaryMobile(request.getPrimaryMobile());
        if (request.getAlternateMobile() != null) entity.setAlternateMobile(request.getAlternateMobile());
        if (request.getFirstName() != null) entity.setFirstName(request.getFirstName());
        if (request.getLastName() != null) entity.setLastName(request.getLastName());
        if (request.getGender() != null) entity.setGender(request.getGender());
        if (request.getDob() != null) entity.setDob(request.getDob());
        if (request.getBloodGroup() != null) entity.setBloodGroup(request.getBloodGroup());
        if (request.getNationality() != null) entity.setNationality(request.getNationality());
        if (request.getMaritalStatus() != null) entity.setMaritalStatus(request.getMaritalStatus());
        if (request.getEmergencyContact() != null) entity.setEmergencyContact(request.getEmergencyContact());
        if (request.getCurrentAddress() != null) entity.setCurrentAddress(request.getCurrentAddress());
        if (request.getPermanentAddress() != null) entity.setPermanentAddress(request.getPermanentAddress());
        if (request.getCompanyId() != null) entity.setCompanyId(request.getCompanyId());
        if (request.getBranchId() != null) entity.setBranchId(request.getBranchId());
        if (request.getDepartmentId() != null) entity.setDepartmentId(request.getDepartmentId());
        if (request.getDesignationId() != null) entity.setDesignationId(request.getDesignationId());
        if (request.getManagerId() != null) entity.setManagerId(request.getManagerId());
        if (request.getTeamId() != null) entity.setTeamId(request.getTeamId());
        if (request.getJoiningDate() != null) entity.setJoiningDate(request.getJoiningDate());
        if (request.getProbationEndDate() != null) entity.setProbationEndDate(request.getProbationEndDate());
        if (request.getConfirmationDate() != null) entity.setConfirmationDate(request.getConfirmationDate());
        if (request.getEmploymentType() != null) entity.setEmploymentType(request.getEmploymentType());
        if (request.getSalary() != null) entity.setSalary(request.getSalary());
        if (request.getSalaryGrade() != null) entity.setSalaryGrade(request.getSalaryGrade());
        if (request.getWorkLocation() != null) entity.setWorkLocation(request.getWorkLocation());
        if (request.getShift() != null) entity.setShift(request.getShift());
        if (request.getRemarks() != null) entity.setRemarks(request.getRemarks());
        if (request.getSkills() != null) entity.setSkills(new ArrayList<>(request.getSkills()));
        if (request.getSkillDetails() != null) entity.setSkillDetails(new ArrayList<>(request.getSkillDetails()));
        if (request.getEducation() != null) entity.setEducation(new ArrayList<>(request.getEducation()));
        if (request.getExperience() != null) entity.setExperience(new ArrayList<>(request.getExperience()));
        if (request.getBankDetails() != null) entity.setBankDetails(request.getBankDetails());
        if (request.getPan() != null) entity.setPan(request.getPan());
        if (request.getAadhaar() != null) entity.setAadhaar(request.getAadhaar());
        if (request.getPassport() != null) entity.setPassport(request.getPassport());
        if (request.getDrivingLicense() != null) entity.setDrivingLicense(request.getDrivingLicense());
        if (request.getGithubUsername() != null) entity.setGithubUsername(request.getGithubUsername());
        if (request.getProfileImage() != null) entity.setProfileImage(request.getProfileImage());
        if (request.getStatus() != null) entity.setStatus(request.getStatus());
    }
}
