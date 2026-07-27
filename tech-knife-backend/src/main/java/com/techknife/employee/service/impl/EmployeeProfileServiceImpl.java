package com.techknife.employee.service.impl;

import com.techknife.backend.event.EmployeeStatusChangedEvent;
import com.techknife.backend.exception.ResourceNotFoundException;
import com.techknife.employee.dto.EmployeeResponse;
import com.techknife.employee.entity.Employee;
import com.techknife.employee.entity.EmployeeStatus;
import com.techknife.employee.entity.EmployeeTimeline;
import com.techknife.employee.entity.TimelineEventType;
import com.techknife.employee.repository.EmployeeRepository;
import com.techknife.employee.repository.EmployeeTimelineRepository;
import com.techknife.employee.service.EmployeeProfileService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;

@Slf4j
@Service
@RequiredArgsConstructor
public class EmployeeProfileServiceImpl implements EmployeeProfileService {

    private final EmployeeRepository employeeRepository;
    private final EmployeeTimelineRepository timelineRepository;
    private final ApplicationEventPublisher eventPublisher;

    @Override
    @Transactional
    public EmployeeResponse activateProfile(String employeeId, String remarks, String updatedBy) {
        return updateStatus(employeeId, EmployeeStatus.ACTIVE, remarks, updatedBy);
    }

    @Override
    @Transactional
    public EmployeeResponse deactivateProfile(String employeeId, String remarks, String updatedBy) {
        return updateStatus(employeeId, EmployeeStatus.DEACTIVATED, remarks, updatedBy);
    }

    @Override
    @Transactional
    public EmployeeResponse suspendProfile(String employeeId, String remarks, String updatedBy) {
        return updateStatus(employeeId, EmployeeStatus.SUSPENDED, remarks, updatedBy);
    }

    @Override
    @Transactional
    public EmployeeResponse terminateProfile(String employeeId, String remarks, String updatedBy) {
        return updateStatus(employeeId, EmployeeStatus.TERMINATED, remarks, updatedBy);
    }

    @Override
    @Transactional
    public EmployeeResponse resignProfile(String employeeId, String remarks, String updatedBy) {
        return updateStatus(employeeId, EmployeeStatus.RESIGNED, remarks, updatedBy);
    }

    @Override
    @Transactional
    public EmployeeResponse retireProfile(String employeeId, String remarks, String updatedBy) {
        return updateStatus(employeeId, EmployeeStatus.RETIRED, remarks, updatedBy);
    }

    private EmployeeResponse updateStatus(String id, EmployeeStatus targetStatus, String remarks, String updatedBy) {
        Employee employee = employeeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Employee", "id", id));

        String oldStatus = employee.getStatus() != null ? employee.getStatus().name() : "NONE";
        employee.setStatus(targetStatus);
        if (remarks != null && !remarks.trim().isEmpty()) {
            employee.setRemarks(remarks);
        }

        Employee saved = employeeRepository.save(employee);

        // Record timeline
        EmployeeTimeline timeline = EmployeeTimeline.builder()
                .employeeId(id)
                .eventType(TimelineEventType.STATUS_CHANGE)
                .oldValue(oldStatus)
                .newValue(targetStatus.name())
                .description(remarks != null ? remarks : "Employee status updated to " + targetStatus)
                .changedBy(updatedBy != null ? updatedBy : "HR_ADMIN")
                .timestamp(Instant.now())
                .build();

        timelineRepository.save(timeline);

        // Publish event
        eventPublisher.publishEvent(new EmployeeStatusChangedEvent(this, id, oldStatus, targetStatus.name()));

        log.info("Updated employee ID {} status from {} to {}", id, oldStatus, targetStatus);
        return mapToResponse(saved);
    }

    private EmployeeResponse mapToResponse(Employee employee) {
        String fullName = (employee.getFirstName() + " " + employee.getLastName()).trim();
        return EmployeeResponse.builder()
                .id(employee.getId())
                .employeeId(employee.getEmployeeId())
                .officialEmail(employee.getOfficialEmail())
                .personalEmail(employee.getPersonalEmail())
                .primaryMobile(employee.getPrimaryMobile())
                .firstName(employee.getFirstName())
                .lastName(employee.getLastName())
                .fullName(fullName)
                .gender(employee.getGender())
                .dob(employee.getDob())
                .companyId(employee.getCompanyId())
                .branchId(employee.getBranchId())
                .departmentId(employee.getDepartmentId())
                .designationId(employee.getDesignationId())
                .managerId(employee.getManagerId())
                .joiningDate(employee.getJoiningDate())
                .status(employee.getStatus())
                .employmentType(employee.getEmploymentType())
                .profileImage(employee.getProfileImage())
                .skills(employee.getSkills())
                .githubUsername(employee.getGithubUsername())
                .remarks(employee.getRemarks())
                .createdAt(employee.getCreatedAt())
                .updatedAt(employee.getUpdatedAt())
                .createdBy(employee.getCreatedBy())
                .updatedBy(employee.getUpdatedBy())
                .build();
    }
}
