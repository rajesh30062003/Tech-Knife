package com.techknife.intern.service.impl;

import com.techknife.backend.dto.PagedResponse;
import com.techknife.backend.event.CertificateGeneratedEvent;
import com.techknife.backend.event.InternCreatedEvent;
import com.techknife.backend.event.MentorAssignedEvent;
import com.techknife.backend.exception.BadRequestException;
import com.techknife.backend.exception.ResourceNotFoundException;
import com.techknife.employee.dto.CreateEmployeeRequest;
import com.techknife.employee.dto.EmployeeResponse;
import com.techknife.employee.entity.Employee;
import com.techknife.employee.repository.EmployeeRepository;
import com.techknife.employee.service.EmployeeService;
import com.techknife.intern.dto.AssignMentorRequest;
import com.techknife.intern.dto.CreateInternEvaluationRequest;
import com.techknife.intern.dto.CreateInternRequest;
import com.techknife.intern.dto.CreateInternTaskRequest;
import com.techknife.intern.dto.GenerateCertificateRequest;
import com.techknife.intern.dto.InternAttendanceSummaryResponse;
import com.techknife.intern.dto.InternCertificateResponse;
import com.techknife.intern.dto.InternEvaluationResponse;
import com.techknife.intern.dto.InternMentorResponse;
import com.techknife.intern.dto.InternResponse;
import com.techknife.intern.dto.InternTaskResponse;
import com.techknife.intern.dto.UpdateAttendanceSummaryRequest;
import com.techknife.intern.dto.UpdateInternRequest;
import com.techknife.intern.dto.UpdateInternTaskRequest;
import com.techknife.intern.entity.Intern;
import com.techknife.intern.entity.InternAttendanceSummary;
import com.techknife.intern.entity.InternCertificate;
import com.techknife.intern.entity.InternEvaluation;
import com.techknife.intern.entity.InternMentor;
import com.techknife.intern.entity.InternStatus;
import com.techknife.intern.entity.InternTask;
import com.techknife.intern.entity.TaskStatus;
import com.techknife.intern.repository.InternAttendanceSummaryRepository;
import com.techknife.intern.repository.InternCertificateRepository;
import com.techknife.intern.repository.InternEvaluationRepository;
import com.techknife.intern.repository.InternMentorRepository;
import com.techknife.intern.repository.InternRepository;
import com.techknife.intern.repository.InternTaskRepository;
import com.techknife.intern.service.InternService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Enterprise Service Implementation for Internship Management module.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class InternServiceImpl implements InternService {

    private final InternRepository internRepository;
    private final InternMentorRepository internMentorRepository;
    private final InternTaskRepository internTaskRepository;
    private final InternEvaluationRepository internEvaluationRepository;
    private final InternAttendanceSummaryRepository internAttendanceSummaryRepository;
    private final InternCertificateRepository internCertificateRepository;
    private final EmployeeRepository employeeRepository;
    private final EmployeeService employeeService;
    private final ApplicationEventPublisher eventPublisher;

    @Override
    @Transactional
    public InternResponse createIntern(CreateInternRequest request) {
        log.info("Creating new intern with code: {} and email: {}", request.getInternCode(), request.getOfficialEmail());

        if (internRepository.existsByInternCode(request.getInternCode())) {
            throw new BadRequestException("Intern code '" + request.getInternCode() + "' already exists");
        }

        if (internRepository.existsByOfficialEmail(request.getOfficialEmail())) {
            throw new BadRequestException("Official email '" + request.getOfficialEmail() + "' already exists");
        }

        Intern intern = Intern.builder()
                .internCode(request.getInternCode())
                .officialEmail(request.getOfficialEmail())
                .personalEmail(request.getPersonalEmail())
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .phone(request.getPhone())
                .gender(request.getGender())
                .dob(request.getDob())
                .college(request.getCollege())
                .university(request.getUniversity())
                .course(request.getCourse())
                .semester(request.getSemester())
                .passingYear(request.getPassingYear())
                .resumeUrl(request.getResumeUrl())
                .githubUrl(request.getGithubUrl())
                .linkedInUrl(request.getLinkedInUrl())
                .portfolioUrl(request.getPortfolioUrl())
                .skills(request.getSkills())
                .companyId(request.getCompanyId())
                .branchId(request.getBranchId())
                .departmentId(request.getDepartmentId())
                .mentorId(request.getMentorId())
                .joiningDate(request.getJoiningDate())
                .endDate(request.getEndDate())
                .stipend(request.getStipend())
                .status(request.getStatus() != null ? request.getStatus() : InternStatus.ACTIVE)
                .remarks(request.getRemarks())
                .certificateGenerated(false)
                .convertedToEmployee(false)
                .build();

        Intern saved = internRepository.save(intern);
        log.info("Saved intern document with ID: {}", saved.getId());

        String fullName = (saved.getFirstName() + " " + saved.getLastName()).trim();
        eventPublisher.publishEvent(new InternCreatedEvent(this, saved.getId(), saved.getInternCode(), saved.getOfficialEmail(), fullName));

        // If mentor assigned on creation, create mentor record
        if (request.getMentorId() != null && !request.getMentorId().trim().isEmpty()) {
            AssignMentorRequest mentorReq = AssignMentorRequest.builder()
                    .mentorId(request.getMentorId())
                    .assignedDate(request.getJoiningDate() != null ? request.getJoiningDate() : LocalDate.now())
                    .remarks("Assigned on onboarding")
                    .build();
            assignMentor(saved.getId(), mentorReq);
        }

        return mapToResponse(saved);
    }

    @Override
    @Transactional
    public InternResponse updateIntern(String id, UpdateInternRequest request) {
        log.info("Updating intern ID: {}", id);
        Intern intern = internRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Intern", "id", id));

        if (request.getPersonalEmail() != null) intern.setPersonalEmail(request.getPersonalEmail());
        if (request.getFirstName() != null) intern.setFirstName(request.getFirstName());
        if (request.getLastName() != null) intern.setLastName(request.getLastName());
        if (request.getPhone() != null) intern.setPhone(request.getPhone());
        if (request.getGender() != null) intern.setGender(request.getGender());
        if (request.getDob() != null) intern.setDob(request.getDob());
        if (request.getCollege() != null) intern.setCollege(request.getCollege());
        if (request.getUniversity() != null) intern.setUniversity(request.getUniversity());
        if (request.getCourse() != null) intern.setCourse(request.getCourse());
        if (request.getSemester() != null) intern.setSemester(request.getSemester());
        if (request.getPassingYear() != null) intern.setPassingYear(request.getPassingYear());
        if (request.getResumeUrl() != null) intern.setResumeUrl(request.getResumeUrl());
        if (request.getGithubUrl() != null) intern.setGithubUrl(request.getGithubUrl());
        if (request.getLinkedInUrl() != null) intern.setLinkedInUrl(request.getLinkedInUrl());
        if (request.getPortfolioUrl() != null) intern.setPortfolioUrl(request.getPortfolioUrl());
        if (request.getSkills() != null) intern.setSkills(request.getSkills());
        if (request.getCompanyId() != null) intern.setCompanyId(request.getCompanyId());
        if (request.getBranchId() != null) intern.setBranchId(request.getBranchId());
        if (request.getDepartmentId() != null) intern.setDepartmentId(request.getDepartmentId());
        if (request.getJoiningDate() != null) intern.setJoiningDate(request.getJoiningDate());
        if (request.getEndDate() != null) intern.setEndDate(request.getEndDate());
        if (request.getStipend() != null) intern.setStipend(request.getStipend());
        if (request.getStatus() != null) intern.setStatus(request.getStatus());
        if (request.getRemarks() != null) intern.setRemarks(request.getRemarks());

        if (request.getMentorId() != null && !request.getMentorId().equals(intern.getMentorId())) {
            AssignMentorRequest mentorReq = AssignMentorRequest.builder()
                    .mentorId(request.getMentorId())
                    .assignedDate(LocalDate.now())
                    .remarks("Updated mentor via intern profile update")
                    .build();
            assignMentor(id, mentorReq);
        }

        Intern updated = internRepository.save(intern);
        return mapToResponse(updated);
    }

    @Override
    @Transactional(readOnly = true)
    public InternResponse getInternById(String id) {
        Intern intern = internRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Intern", "id", id));
        return mapToResponse(intern);
    }

    @Override
    @Transactional(readOnly = true)
    public InternResponse getInternByCode(String internCode) {
        Intern intern = internRepository.findByInternCode(internCode)
                .orElseThrow(() -> new ResourceNotFoundException("Intern", "internCode", internCode));
        return mapToResponse(intern);
    }

    @Override
    @Transactional(readOnly = true)
    public InternResponse getInternByOfficialEmail(String officialEmail) {
        Intern intern = internRepository.findByOfficialEmail(officialEmail)
                .orElseThrow(() -> new ResourceNotFoundException("Intern", "officialEmail", officialEmail));
        return mapToResponse(intern);
    }

    @Override
    @Transactional(readOnly = true)
    public PagedResponse<InternResponse> getAllInterns(int page, int size, String search, InternStatus status) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        Page<Intern> internPage;

        if (search != null && !search.trim().isEmpty()) {
            internPage = internRepository.searchByName(search.trim(), pageable);
        } else if (status != null) {
            internPage = internRepository.findByStatus(status, pageable);
        } else {
            internPage = internRepository.findAll(pageable);
        }

        List<InternResponse> content = internPage.getContent().stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());

        return PagedResponse.<InternResponse>builder()
                .content(content)
                .page(internPage.getNumber())
                .size(internPage.getSize())
                .totalElements(internPage.getTotalElements())
                .totalPages(internPage.getTotalPages())
                .last(internPage.isLast())
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public List<InternResponse> getInternsByDepartment(String departmentId) {
        return internRepository.findByDepartmentId(departmentId).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<InternResponse> getInternsByMentor(String mentorId) {
        return internRepository.findByMentorId(mentorId).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void deleteIntern(String id) {
        if (!internRepository.existsById(id)) {
            throw new ResourceNotFoundException("Intern", "id", id);
        }
        internRepository.deleteById(id);
        log.info("Deleted intern document ID: {}", id);
    }

    // --- MENTOR MANAGEMENT ---

    @Override
    @Transactional
    public InternMentorResponse assignMentor(String internId, AssignMentorRequest request) {
        Intern intern = internRepository.findById(internId)
                .orElseThrow(() -> new ResourceNotFoundException("Intern", "id", internId));

        if (!employeeRepository.existsById(request.getMentorId())) {
            throw new ResourceNotFoundException("Mentor Employee", "id", request.getMentorId());
        }

        // Validate Maximum Active Intern Limit
        int maxLimit = request.getMaxInternLimit() != null ? request.getMaxInternLimit() : 5;
        long activeInternCount = internMentorRepository.countByMentorIdAndActiveTrue(request.getMentorId());
        if (activeInternCount >= maxLimit) {
            throw new BadRequestException("Mentor ID " + request.getMentorId() + " has reached the maximum allowed active interns limit (" + maxLimit + ")");
        }

        // Deactivate existing active mentor assignment if any
        internMentorRepository.findByInternIdAndActiveTrue(internId).ifPresent(existing -> {
            existing.setActive(false);
            existing.setEndDate(LocalDate.now());
            internMentorRepository.save(existing);
        });

        InternMentor newAssignment = InternMentor.builder()
                .internId(internId)
                .mentorId(request.getMentorId())
                .assignedDate(request.getAssignedDate() != null ? request.getAssignedDate() : LocalDate.now())
                .active(true)
                .maxInternLimit(maxLimit)
                .remarks(request.getRemarks())
                .build();

        InternMentor saved = internMentorRepository.save(newAssignment);

        intern.setMentorId(request.getMentorId());
        internRepository.save(intern);

        eventPublisher.publishEvent(new MentorAssignedEvent(this, internId, request.getMentorId(), "SYSTEM_HR"));

        return mapToMentorResponse(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public List<InternMentorResponse> getMentorHistory(String internId) {
        return internMentorRepository.findByInternId(internId).stream()
                .map(this::mapToMentorResponse)
                .collect(Collectors.toList());
    }

    // --- TASK MANAGEMENT ---

    @Override
    @Transactional
    public InternTaskResponse assignTask(String internId, CreateInternTaskRequest request) {
        Intern intern = internRepository.findById(internId)
                .orElseThrow(() -> new ResourceNotFoundException("Intern", "id", internId));

        String mentorId = request.getMentorId() != null ? request.getMentorId() : intern.getMentorId();

        InternTask task = InternTask.builder()
                .internId(internId)
                .mentorId(mentorId)
                .title(request.getTitle())
                .description(request.getDescription())
                .priority(request.getPriority())
                .status(request.getStatus() != null ? request.getStatus() : TaskStatus.TODO)
                .progressPercentage(request.getProgressPercentage() != null ? request.getProgressPercentage() : 0)
                .deadline(request.getDeadline())
                .assignedDate(LocalDate.now())
                .build();

        InternTask saved = internTaskRepository.save(task);
        return mapToTaskResponse(saved);
    }

    @Override
    @Transactional
    public InternTaskResponse updateTask(String internId, String taskId, UpdateInternTaskRequest request) {
        InternTask task = internTaskRepository.findById(taskId)
                .orElseThrow(() -> new ResourceNotFoundException("InternTask", "id", taskId));

        if (!task.getInternId().equals(internId)) {
            throw new BadRequestException("Task does not belong to intern ID: " + internId);
        }

        if (request.getTitle() != null) task.setTitle(request.getTitle());
        if (request.getDescription() != null) task.setDescription(request.getDescription());
        if (request.getPriority() != null) task.setPriority(request.getPriority());
        if (request.getStatus() != null) {
            task.setStatus(request.getStatus());
            if (request.getStatus() == TaskStatus.COMPLETED && task.getCompletionDate() == null) {
                task.setCompletionDate(LocalDate.now());
                task.setProgressPercentage(100);
            }
        }
        if (request.getProgressPercentage() != null) task.setProgressPercentage(request.getProgressPercentage());
        if (request.getDeadline() != null) task.setDeadline(request.getDeadline());
        if (request.getCompletionDate() != null) task.setCompletionDate(request.getCompletionDate());
        if (request.getReviewRemarks() != null) task.setReviewRemarks(request.getReviewRemarks());

        InternTask updated = internTaskRepository.save(task);
        return mapToTaskResponse(updated);
    }

    @Override
    @Transactional(readOnly = true)
    public List<InternTaskResponse> getInternTasks(String internId) {
        return internTaskRepository.findByInternId(internId).stream()
                .map(this::mapToTaskResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void deleteTask(String internId, String taskId) {
        InternTask task = internTaskRepository.findById(taskId)
                .orElseThrow(() -> new ResourceNotFoundException("InternTask", "id", taskId));

        if (!task.getInternId().equals(internId)) {
            throw new BadRequestException("Task does not belong to intern ID: " + internId);
        }

        internTaskRepository.deleteById(taskId);
    }

    // --- EVALUATIONS ---

    @Override
    @Transactional
    public InternEvaluationResponse createEvaluation(String internId, CreateInternEvaluationRequest request) {
        Intern intern = internRepository.findById(internId)
                .orElseThrow(() -> new ResourceNotFoundException("Intern", "id", internId));

        double overall = (request.getTechnicalSkills() + request.getCommunication() + request.getProblemSolving()
                          + request.getAttendance() + request.getDiscipline() + request.getLearningAbility()) / 6.0;

        InternEvaluation eval = InternEvaluation.builder()
                .internId(internId)
                .evaluatorId(request.getEvaluatorId() != null ? request.getEvaluatorId() : intern.getMentorId())
                .evaluationDate(request.getEvaluationDate() != null ? request.getEvaluationDate() : LocalDate.now())
                .technicalSkills(request.getTechnicalSkills())
                .communication(request.getCommunication())
                .problemSolving(request.getProblemSolving())
                .attendance(request.getAttendance())
                .discipline(request.getDiscipline())
                .learningAbility(request.getLearningAbility())
                .overallScore(Math.round(overall * 100.0) / 100.0)
                .remarks(request.getRemarks())
                .recommendation(request.getRecommendation())
                .build();

        InternEvaluation saved = internEvaluationRepository.save(eval);
        return mapToEvaluationResponse(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public List<InternEvaluationResponse> getInternEvaluations(String internId) {
        return internEvaluationRepository.findByInternId(internId).stream()
                .map(this::mapToEvaluationResponse)
                .collect(Collectors.toList());
    }

    // --- ATTENDANCE SUMMARY ---

    @Override
    @Transactional
    public InternAttendanceSummaryResponse updateAttendanceSummary(String internId, UpdateAttendanceSummaryRequest request) {
        if (!internRepository.existsById(internId)) {
            throw new ResourceNotFoundException("Intern", "id", internId);
        }

        double percentage = request.getTotalWorkingDays() > 0 ?
                ((double) request.getPresentDays() / request.getTotalWorkingDays()) * 100.0 : 0.0;

        InternAttendanceSummary summary = internAttendanceSummaryRepository.findByInternIdAndMonthYear(internId, request.getMonthYear())
                .orElse(InternAttendanceSummary.builder()
                        .internId(internId)
                        .monthYear(request.getMonthYear())
                        .build());

        summary.setTotalWorkingDays(request.getTotalWorkingDays());
        summary.setPresentDays(request.getPresentDays());
        summary.setAbsentDays(request.getAbsentDays());
        summary.setLeaveDays(request.getLeaveDays());
        summary.setAttendancePercentage(Math.round(percentage * 100.0) / 100.0);

        InternAttendanceSummary saved = internAttendanceSummaryRepository.save(summary);
        return mapToAttendanceResponse(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public List<InternAttendanceSummaryResponse> getAttendanceSummaries(String internId) {
        return internAttendanceSummaryRepository.findByInternId(internId).stream()
                .map(this::mapToAttendanceResponse)
                .collect(Collectors.toList());
    }

    // --- CERTIFICATE GENERATION ---

    @Override
    @Transactional
    public InternCertificateResponse generateCertificate(String internId, GenerateCertificateRequest request) {
        Intern intern = internRepository.findById(internId)
                .orElseThrow(() -> new ResourceNotFoundException("Intern", "id", internId));

        if (Boolean.TRUE.equals(intern.getCertificateGenerated())) {
            return getCertificate(internId);
        }

        String certNo = "CERT-TK-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        String verifyCode = UUID.randomUUID().toString().replace("-", "").substring(0, 12).toUpperCase();
        String simulatedDownloadUrl = "https://res.cloudinary.com/techknife/raw/upload/certificates/" + certNo + ".pdf";

        InternCertificate cert = InternCertificate.builder()
                .internId(internId)
                .certificateNumber(certNo)
                .verificationCode(verifyCode)
                .issueDate(request.getIssueDate() != null ? request.getIssueDate() : LocalDate.now())
                .downloadUrl(simulatedDownloadUrl)
                .publicId("certificates/" + certNo)
                .generatedBy(request.getGeneratedBy() != null ? request.getGeneratedBy() : "SYSTEM_HR")
                .createdAt(Instant.now())
                .build();

        InternCertificate saved = internCertificateRepository.save(cert);

        intern.setCertificateGenerated(true);
        intern.setCertificateId(saved.getId());
        intern.setStatus(InternStatus.COMPLETED);
        internRepository.save(intern);

        eventPublisher.publishEvent(new CertificateGeneratedEvent(this, internId, certNo, verifyCode));

        return mapToCertificateResponse(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public InternCertificateResponse getCertificate(String internId) {
        InternCertificate cert = internCertificateRepository.findByInternId(internId)
                .orElseThrow(() -> new ResourceNotFoundException("Certificate for Intern", "internId", internId));
        return mapToCertificateResponse(cert);
    }

    // --- CONVERT INTERN TO EMPLOYEE ---

    @Override
    @Transactional
    public EmployeeResponse convertToEmployee(String internId) {
        Intern intern = internRepository.findById(internId)
                .orElseThrow(() -> new ResourceNotFoundException("Intern", "id", internId));

        if (Boolean.TRUE.equals(intern.getConvertedToEmployee())) {
            throw new BadRequestException("Intern ID " + internId + " has already been converted to an employee");
        }

        String employeeCode = "EMP-" + intern.getInternCode().replace("INT-", "");
        CreateEmployeeRequest empRequest = CreateEmployeeRequest.builder()
                .employeeId(employeeCode)
                .officialEmail(intern.getOfficialEmail())
                .personalEmail(intern.getPersonalEmail())
                .primaryMobile(intern.getPhone())
                .firstName(intern.getFirstName())
                .lastName(intern.getLastName())
                .gender(intern.getGender())
                .dob(intern.getDob())
                .companyId(intern.getCompanyId())
                .branchId(intern.getBranchId())
                .departmentId(intern.getDepartmentId())
                .managerId(intern.getMentorId())
                .joiningDate(LocalDate.now())
                .skills(intern.getSkills())
                .githubUsername(intern.getGithubUrl())
                .remarks("Converted from Intern code: " + intern.getInternCode())
                .build();

        EmployeeResponse employeeResponse = employeeService.createEmployee(empRequest);

        intern.setConvertedToEmployee(true);
        intern.setConvertedEmployeeId(employeeResponse.getId());
        intern.setStatus(InternStatus.CONVERTED);
        internRepository.save(intern);

        log.info("Successfully converted Intern ID {} to Employee ID {}", internId, employeeResponse.getId());
        return employeeResponse;
    }

    // --- HELPER MAPPERS ---

    private InternResponse mapToResponse(Intern intern) {
        String fullName = ((intern.getFirstName() != null ? intern.getFirstName() : "") + " " +
                           (intern.getLastName() != null ? intern.getLastName() : "")).trim();

        return InternResponse.builder()
                .id(intern.getId())
                .internCode(intern.getInternCode())
                .officialEmail(intern.getOfficialEmail())
                .personalEmail(intern.getPersonalEmail())
                .firstName(intern.getFirstName())
                .lastName(intern.getLastName())
                .fullName(fullName)
                .phone(intern.getPhone())
                .gender(intern.getGender())
                .dob(intern.getDob())
                .college(intern.getCollege())
                .university(intern.getUniversity())
                .course(intern.getCourse())
                .semester(intern.getSemester())
                .passingYear(intern.getPassingYear())
                .resumeUrl(intern.getResumeUrl())
                .resumePublicId(intern.getResumePublicId())
                .githubUrl(intern.getGithubUrl())
                .linkedInUrl(intern.getLinkedInUrl())
                .portfolioUrl(intern.getPortfolioUrl())
                .skills(intern.getSkills())
                .companyId(intern.getCompanyId())
                .branchId(intern.getBranchId())
                .departmentId(intern.getDepartmentId())
                .mentorId(intern.getMentorId())
                .joiningDate(intern.getJoiningDate())
                .endDate(intern.getEndDate())
                .stipend(intern.getStipend())
                .status(intern.getStatus())
                .certificateGenerated(intern.getCertificateGenerated())
                .certificateId(intern.getCertificateId())
                .convertedToEmployee(intern.getConvertedToEmployee())
                .convertedEmployeeId(intern.getConvertedEmployeeId())
                .remarks(intern.getRemarks())
                .createdAt(intern.getCreatedAt())
                .updatedAt(intern.getUpdatedAt())
                .createdBy(intern.getCreatedBy())
                .updatedBy(intern.getUpdatedBy())
                .build();
    }

    private InternMentorResponse mapToMentorResponse(InternMentor entity) {
        String mentorName = "";
        if (entity.getMentorId() != null) {
            mentorName = employeeRepository.findById(entity.getMentorId())
                    .map(e -> (e.getFirstName() + " " + e.getLastName()).trim())
                    .orElse(entity.getMentorId());
        }

        return InternMentorResponse.builder()
                .id(entity.getId())
                .internId(entity.getInternId())
                .mentorId(entity.getMentorId())
                .mentorName(mentorName)
                .assignedDate(entity.getAssignedDate())
                .endDate(entity.getEndDate())
                .active(entity.isActive())
                .maxInternLimit(entity.getMaxInternLimit())
                .assignedBy(entity.getAssignedBy())
                .remarks(entity.getRemarks())
                .createdAt(entity.getCreatedAt())
                .build();
    }

    private InternTaskResponse mapToTaskResponse(InternTask task) {
        return InternTaskResponse.builder()
                .id(task.getId())
                .internId(task.getInternId())
                .mentorId(task.getMentorId())
                .title(task.getTitle())
                .description(task.getDescription())
                .priority(task.getPriority())
                .status(task.getStatus())
                .progressPercentage(task.getProgressPercentage())
                .deadline(task.getDeadline())
                .assignedDate(task.getAssignedDate())
                .completionDate(task.getCompletionDate())
                .reviewRemarks(task.getReviewRemarks())
                .createdAt(task.getCreatedAt())
                .updatedAt(task.getUpdatedAt())
                .build();
    }

    private InternEvaluationResponse mapToEvaluationResponse(InternEvaluation eval) {
        String evaluatorName = "";
        if (eval.getEvaluatorId() != null) {
            evaluatorName = employeeRepository.findById(eval.getEvaluatorId())
                    .map(e -> (e.getFirstName() + " " + e.getLastName()).trim())
                    .orElse(eval.getEvaluatorId());
        }

        return InternEvaluationResponse.builder()
                .id(eval.getId())
                .internId(eval.getInternId())
                .evaluatorId(eval.getEvaluatorId())
                .evaluatorName(evaluatorName)
                .evaluationDate(eval.getEvaluationDate())
                .technicalSkills(eval.getTechnicalSkills())
                .communication(eval.getCommunication())
                .problemSolving(eval.getProblemSolving())
                .attendance(eval.getAttendance())
                .discipline(eval.getDiscipline())
                .learningAbility(eval.getLearningAbility())
                .overallScore(eval.getOverallScore())
                .remarks(eval.getRemarks())
                .recommendation(eval.getRecommendation())
                .createdAt(eval.getCreatedAt())
                .build();
    }

    private InternAttendanceSummaryResponse mapToAttendanceResponse(InternAttendanceSummary summary) {
        return InternAttendanceSummaryResponse.builder()
                .id(summary.getId())
                .internId(summary.getInternId())
                .monthYear(summary.getMonthYear())
                .totalWorkingDays(summary.getTotalWorkingDays())
                .presentDays(summary.getPresentDays())
                .absentDays(summary.getAbsentDays())
                .leaveDays(summary.getLeaveDays())
                .attendancePercentage(summary.getAttendancePercentage())
                .updatedAt(summary.getUpdatedAt())
                .build();
    }

    private InternCertificateResponse mapToCertificateResponse(InternCertificate cert) {
        return InternCertificateResponse.builder()
                .id(cert.getId())
                .internId(cert.getInternId())
                .certificateNumber(cert.getCertificateNumber())
                .verificationCode(cert.getVerificationCode())
                .issueDate(cert.getIssueDate())
                .downloadUrl(cert.getDownloadUrl())
                .publicId(cert.getPublicId())
                .generatedBy(cert.getGeneratedBy())
                .createdAt(cert.getCreatedAt())
                .build();
    }
}
