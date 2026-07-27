package com.techknife.leave.service.impl;

import com.techknife.backend.exception.BadRequestException;
import com.techknife.backend.exception.ResourceNotFoundException;
import com.techknife.employee.entity.Employee;
import com.techknife.employee.repository.EmployeeRepository;
import com.techknife.leave.dto.WFHRequestCreateDTO;
import com.techknife.leave.dto.WFHRequestDTO;
import com.techknife.leave.entity.WFHStatus;
import com.techknife.leave.entity.WorkFromHomeRequest;
import com.techknife.leave.repository.WorkFromHomeRequestRepository;
import com.techknife.leave.service.WorkFromHomeService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class WorkFromHomeServiceImpl implements WorkFromHomeService {

    private final WorkFromHomeRequestRepository wfhRequestRepository;
    private final EmployeeRepository employeeRepository;

    @Override
    @Transactional
    public WFHRequestDTO applyWFH(WFHRequestCreateDTO dto) {
        if (dto.getEndDate().isBefore(dto.getStartDate())) {
            throw new BadRequestException("End date cannot be before start date");
        }

        Employee employee = employeeRepository.findById(dto.getEmployeeId())
                .or(() -> employeeRepository.findByEmployeeId(dto.getEmployeeId()))
                .orElseThrow(() -> new ResourceNotFoundException("Employee not found with ID: " + dto.getEmployeeId()));

        double days = ChronoUnit.DAYS.between(dto.getStartDate(), dto.getEndDate()) + 1.0;
        String approverId = employee.getManagerId() != null ? employee.getManagerId() : "HR_ADMIN";

        WorkFromHomeRequest request = WorkFromHomeRequest.builder()
                .employeeId(employee.getId())
                .employeeName(employee.getFirstName() + " " + employee.getLastName())
                .departmentId(employee.getDepartmentId())
                .startDate(dto.getStartDate())
                .endDate(dto.getEndDate())
                .totalDays(days)
                .reason(dto.getReason())
                .workPlan(dto.getWorkPlan())
                .status(WFHStatus.PENDING)
                .approverId(approverId)
                .build();

        WorkFromHomeRequest saved = wfhRequestRepository.save(request);
        log.info("Applied WFH Request ID={}, Employee ID={}", saved.getId(), employee.getEmployeeId());
        return mapToDTO(saved);
    }

    @Override
    @Transactional
    public WFHRequestDTO approveOrRejectWFH(String id, WFHStatus status, String approverId, String approverName, String comments) {
        WorkFromHomeRequest request = wfhRequestRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("WFH Request not found with ID: " + id));

        if (request.getStatus() != WFHStatus.PENDING) {
            throw new BadRequestException("WFH request is already in status: " + request.getStatus());
        }

        request.setStatus(status);
        request.setApproverId(approverId);
        request.setApproverName(approverName);
        request.setApproverComments(comments);
        request.setActionedAt(Instant.now());

        WorkFromHomeRequest updated = wfhRequestRepository.save(request);
        log.info("WFH Request {}: ID={}, Approver={}", status, id, approverId);
        return mapToDTO(updated);
    }

    @Override
    @Transactional
    public WFHRequestDTO cancelWFH(String id, String employeeId) {
        WorkFromHomeRequest request = wfhRequestRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("WFH Request not found with ID: " + id));

        if (!request.getEmployeeId().equals(employeeId)) {
            throw new BadRequestException("Unauthorized to cancel this WFH request");
        }

        request.setStatus(WFHStatus.CANCELLED);
        WorkFromHomeRequest updated = wfhRequestRepository.save(request);
        log.info("Cancelled WFH Request ID={}", id);
        return mapToDTO(updated);
    }

    @Override
    public WFHRequestDTO getWFHById(String id) {
        return wfhRequestRepository.findById(id)
                .map(this::mapToDTO)
                .orElseThrow(() -> new ResourceNotFoundException("WFH Request not found with ID: " + id));
    }

    @Override
    public List<WFHRequestDTO> getEmployeeWFHRequests(String employeeId) {
        return wfhRequestRepository.findByEmployeeId(employeeId).stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<WFHRequestDTO> getPendingWFHForApprover(String approverId) {
        return wfhRequestRepository.findByApproverIdAndStatus(approverId, WFHStatus.PENDING).stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<WFHRequestDTO> getAllWFHRequests() {
        return wfhRequestRepository.findAll().stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    private WFHRequestDTO mapToDTO(WorkFromHomeRequest entity) {
        return WFHRequestDTO.builder()
                .id(entity.getId())
                .employeeId(entity.getEmployeeId())
                .employeeName(entity.getEmployeeName())
                .departmentId(entity.getDepartmentId())
                .startDate(entity.getStartDate())
                .endDate(entity.getEndDate())
                .totalDays(entity.getTotalDays())
                .reason(entity.getReason())
                .workPlan(entity.getWorkPlan())
                .status(entity.getStatus())
                .approverId(entity.getApproverId())
                .approverName(entity.getApproverName())
                .approverComments(entity.getApproverComments())
                .actionedAt(entity.getActionedAt())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .build();
    }
}
