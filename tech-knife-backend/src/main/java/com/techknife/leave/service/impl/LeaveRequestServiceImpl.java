package com.techknife.leave.service.impl;

import com.techknife.backend.event.LeaveRequestedEvent;
import com.techknife.backend.event.LeaveStatusChangedEvent;
import com.techknife.backend.exception.BadRequestException;
import com.techknife.backend.exception.ResourceNotFoundException;
import com.techknife.employee.entity.Employee;
import com.techknife.employee.repository.EmployeeRepository;
import com.techknife.leave.dto.LeaveApprovalDTO;
import com.techknife.leave.dto.LeaveRequestCreateDTO;
import com.techknife.leave.dto.LeaveRequestDTO;
import com.techknife.leave.entity.*;
import com.techknife.leave.repository.LeaveBalanceRepository;
import com.techknife.leave.repository.LeaveRequestRepository;
import com.techknife.leave.repository.LeaveTypeRepository;
import com.techknife.leave.service.LeaveBalanceService;
import com.techknife.leave.service.LeaveRequestService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class LeaveRequestServiceImpl implements LeaveRequestService {

    private final LeaveRequestRepository leaveRequestRepository;
    private final LeaveBalanceRepository leaveBalanceRepository;
    private final LeaveTypeRepository leaveTypeRepository;
    private final EmployeeRepository employeeRepository;
    private final LeaveBalanceService leaveBalanceService;
    private final ApplicationEventPublisher eventPublisher;

    @Override
    @Transactional
    public LeaveRequestDTO applyLeave(LeaveRequestCreateDTO dto) {
        if (dto.getEndDate().isBefore(dto.getStartDate())) {
            throw new BadRequestException("End date cannot be before start date");
        }

        Employee employee = employeeRepository.findById(dto.getEmployeeId())
                .or(() -> employeeRepository.findByEmployeeId(dto.getEmployeeId()))
                .orElseThrow(() -> new ResourceNotFoundException("Employee not found with ID: " + dto.getEmployeeId()));

        LeaveType leaveType = leaveTypeRepository.findById(dto.getLeaveTypeId())
                .orElseThrow(() -> new ResourceNotFoundException("LeaveType not found with ID: " + dto.getLeaveTypeId()));

        // Calculate total days
        double totalDays = ChronoUnit.DAYS.between(dto.getStartDate(), dto.getEndDate()) + 1.0;
        if (dto.getHalfDayType() != null && dto.getHalfDayType() != HalfDayType.NONE) {
            totalDays = 0.5;
        }

        // Check overlapping leave requests
        List<LeaveRequest> overlapping = leaveRequestRepository
                .findByEmployeeIdAndStatusInAndStartDateLessThanEqualAndEndDateGreaterThanEqual(
                        employee.getId(),
                        List.of(LeaveStatus.PENDING, LeaveStatus.APPROVED),
                        dto.getEndDate(),
                        dto.getStartDate());

        if (!overlapping.isEmpty()) {
            throw new BadRequestException("An existing leave request overlaps with the selected date range");
        }

        // Check balance
        int currentYear = dto.getStartDate().getYear();
        LeaveBalance balance = leaveBalanceRepository.findByEmployeeIdAndLeaveTypeIdAndYear(employee.getId(), leaveType.getId(), currentYear)
                .orElseGet(() -> {
                    leaveBalanceService.initializeEmployeeBalances(employee.getId(), currentYear);
                    return leaveBalanceRepository.findByEmployeeIdAndLeaveTypeIdAndYear(employee.getId(), leaveType.getId(), currentYear)
                            .orElseThrow(() -> new ResourceNotFoundException("Leave Balance not available"));
                });

        if (balance.getAvailableDays() < totalDays) {
            throw new BadRequestException("Insufficient leave balance. Available: " + balance.getAvailableDays() + ", Requested: " + totalDays);
        }

        // Approver setup
        String initialApproverId = employee.getManagerId() != null ? employee.getManagerId() : "HR_ADMIN";

        LeaveRequest leaveRequest = LeaveRequest.builder()
                .employeeId(employee.getId())
                .employeeName(employee.getFirstName() + " " + employee.getLastName())
                .departmentId(employee.getDepartmentId())
                .leaveTypeId(leaveType.getId())
                .leaveTypeName(leaveType.getName())
                .startDate(dto.getStartDate())
                .endDate(dto.getEndDate())
                .totalDays(totalDays)
                .halfDayType(dto.getHalfDayType() != null ? dto.getHalfDayType() : HalfDayType.NONE)
                .reason(dto.getReason())
                .attachmentUrl(dto.getAttachmentUrl())
                .status(LeaveStatus.PENDING)
                .currentApproverId(initialApproverId)
                .approvals(new ArrayList<>())
                .build();

        LeaveRequest saved = leaveRequestRepository.save(leaveRequest);

        // Update balance pending days
        balance.setPendingDays(balance.getPendingDays() + totalDays);
        balance.setAvailableDays(balance.getAvailableDays() - totalDays);
        leaveBalanceRepository.save(balance);

        // Publish event
        eventPublisher.publishEvent(new LeaveRequestedEvent(this, saved.getId(), employee.getId(), leaveType.getId(), totalDays));

        log.info("Applied Leave Request: ID={}, Employee={}, TotalDays={}", saved.getId(), employee.getEmployeeId(), totalDays);
        return mapToDTO(saved);
    }

    @Override
    @Transactional
    public LeaveRequestDTO approveOrRejectLeave(String requestId, LeaveApprovalDTO approvalDTO, String approverId, String approverName, String approverRole) {
        LeaveRequest leaveRequest = leaveRequestRepository.findById(requestId)
                .orElseThrow(() -> new ResourceNotFoundException("LeaveRequest not found with ID: " + requestId));

        if (leaveRequest.getStatus() != LeaveStatus.PENDING) {
            throw new BadRequestException("Leave request is already in status: " + leaveRequest.getStatus());
        }

        int currentLevel = leaveRequest.getApprovals().size() + 1;

        LeaveApproval approvalStep = LeaveApproval.builder()
                .level(currentLevel)
                .approverId(approverId)
                .approverName(approverName)
                .approverRole(approverRole)
                .status(approvalDTO.getStatus())
                .comments(approvalDTO.getComments())
                .actionedAt(Instant.now())
                .build();

        leaveRequest.getApprovals().add(approvalStep);

        int year = leaveRequest.getStartDate().getYear();
        LeaveBalance balance = leaveBalanceRepository.findByEmployeeIdAndLeaveTypeIdAndYear(leaveRequest.getEmployeeId(), leaveRequest.getLeaveTypeId(), year)
                .orElseThrow(() -> new ResourceNotFoundException("Leave Balance not found"));

        if (approvalDTO.getStatus() == LeaveStatus.APPROVED) {
            leaveRequest.setStatus(LeaveStatus.APPROVED);
            leaveRequest.setCurrentApproverId(null);

            // Update balance: move pending to used
            balance.setPendingDays(Math.max(0.0, balance.getPendingDays() - leaveRequest.getTotalDays()));
            balance.setUsedDays(balance.getUsedDays() + leaveRequest.getTotalDays());
            leaveBalanceRepository.save(balance);

            log.info("Leave Request APPROVED: ID={}, Approver={}", requestId, approverId);
        } else if (approvalDTO.getStatus() == LeaveStatus.REJECTED) {
            leaveRequest.setStatus(LeaveStatus.REJECTED);
            leaveRequest.setCurrentApproverId(null);

            // Restore pending balance back to available
            balance.setPendingDays(Math.max(0.0, balance.getPendingDays() - leaveRequest.getTotalDays()));
            balance.setAvailableDays(balance.getAvailableDays() + leaveRequest.getTotalDays());
            leaveBalanceRepository.save(balance);

            log.info("Leave Request REJECTED: ID={}, Approver={}", requestId, approverId);
        }

        LeaveRequest updated = leaveRequestRepository.save(leaveRequest);

        eventPublisher.publishEvent(new LeaveStatusChangedEvent(this, updated.getId(), updated.getEmployeeId(), updated.getStatus(), approverId));

        return mapToDTO(updated);
    }

    @Override
    @Transactional
    public LeaveRequestDTO cancelLeave(String requestId, String employeeId, String reason) {
        LeaveRequest leaveRequest = leaveRequestRepository.findById(requestId)
                .orElseThrow(() -> new ResourceNotFoundException("LeaveRequest not found with ID: " + requestId));

        if (!leaveRequest.getEmployeeId().equals(employeeId)) {
            throw new BadRequestException("Unauthorized to cancel this leave request");
        }

        if (leaveRequest.getStatus() == LeaveStatus.CANCELLED || leaveRequest.getStatus() == LeaveStatus.REJECTED) {
            throw new BadRequestException("Cannot cancel leave request in status: " + leaveRequest.getStatus());
        }

        int year = leaveRequest.getStartDate().getYear();
        LeaveBalance balance = leaveBalanceRepository.findByEmployeeIdAndLeaveTypeIdAndYear(leaveRequest.getEmployeeId(), leaveRequest.getLeaveTypeId(), year)
                .orElseThrow(() -> new ResourceNotFoundException("Leave Balance not found"));

        if (leaveRequest.getStatus() == LeaveStatus.PENDING) {
            balance.setPendingDays(Math.max(0.0, balance.getPendingDays() - leaveRequest.getTotalDays()));
            balance.setAvailableDays(balance.getAvailableDays() + leaveRequest.getTotalDays());
        } else if (leaveRequest.getStatus() == LeaveStatus.APPROVED) {
            balance.setUsedDays(Math.max(0.0, balance.getUsedDays() - leaveRequest.getTotalDays()));
            balance.setAvailableDays(balance.getAvailableDays() + leaveRequest.getTotalDays());
        }

        balanceRepositorySave(balance);

        leaveRequest.setStatus(LeaveStatus.CANCELLED);
        leaveRequest.setCancellationReason(reason);
        leaveRequest.setCancelledAt(Instant.now());
        leaveRequest.setCurrentApproverId(null);

        LeaveRequest updated = leaveRequestRepository.save(leaveRequest);

        eventPublisher.publishEvent(new LeaveStatusChangedEvent(this, updated.getId(), updated.getEmployeeId(), LeaveStatus.CANCELLED, employeeId));

        log.info("Cancelled Leave Request: ID={}, Reason={}", requestId, reason);
        return mapToDTO(updated);
    }

    private void balanceRepositorySave(LeaveBalance balance) {
        leaveBalanceRepository.save(balance);
    }

    @Override
    public LeaveRequestDTO getLeaveRequestById(String id) {
        return leaveRequestRepository.findById(id)
                .map(this::mapToDTO)
                .orElseThrow(() -> new ResourceNotFoundException("LeaveRequest not found with ID: " + id));
    }

    @Override
    public List<LeaveRequestDTO> getEmployeeLeaveRequests(String employeeId) {
        return leaveRequestRepository.findByEmployeeId(employeeId).stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<LeaveRequestDTO> getPendingApprovalsForUser(String approverId) {
        return leaveRequestRepository.findByCurrentApproverIdAndStatus(approverId, LeaveStatus.PENDING).stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<LeaveRequestDTO> getDepartmentLeaveRequests(String departmentId) {
        return leaveRequestRepository.findByDepartmentIdAndStatus(departmentId, LeaveStatus.APPROVED).stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<LeaveRequestDTO> getAllLeaveRequests() {
        return leaveRequestRepository.findAll().stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    private LeaveRequestDTO mapToDTO(LeaveRequest entity) {
        return LeaveRequestDTO.builder()
                .id(entity.getId())
                .employeeId(entity.getEmployeeId())
                .employeeName(entity.getEmployeeName())
                .departmentId(entity.getDepartmentId())
                .leaveTypeId(entity.getLeaveTypeId())
                .leaveTypeName(entity.getLeaveTypeName())
                .startDate(entity.getStartDate())
                .endDate(entity.getEndDate())
                .totalDays(entity.getTotalDays())
                .halfDayType(entity.getHalfDayType())
                .reason(entity.getReason())
                .attachmentUrl(entity.getAttachmentUrl())
                .status(entity.getStatus())
                .approvals(entity.getApprovals())
                .currentApproverId(entity.getCurrentApproverId())
                .cancellationReason(entity.getCancellationReason())
                .cancelledAt(entity.getCancelledAt())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .build();
    }
}
