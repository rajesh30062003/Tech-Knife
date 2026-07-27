package com.techknife.holiday.service.impl;

import com.techknife.backend.exception.BadRequestException;
import com.techknife.backend.exception.ResourceNotFoundException;
import com.techknife.employee.entity.Employee;
import com.techknife.employee.repository.EmployeeRepository;
import com.techknife.holiday.dto.RestrictedHolidayRequestDTO;
import com.techknife.holiday.entity.Holiday;
import com.techknife.holiday.entity.HolidayCalendar;
import com.techknife.holiday.entity.RestrictedHolidayRequest;
import com.techknife.holiday.repository.HolidayCalendarRepository;
import com.techknife.holiday.repository.HolidayRepository;
import com.techknife.holiday.repository.RestrictedHolidayRequestRepository;
import com.techknife.holiday.service.RestrictedHolidayService;
import com.techknife.leave.entity.LeaveStatus;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class RestrictedHolidayServiceImpl implements RestrictedHolidayService {

    private final RestrictedHolidayRequestRepository requestRepository;
    private final HolidayRepository holidayRepository;
    private final HolidayCalendarRepository calendarRepository;
    private final EmployeeRepository employeeRepository;

    @Override
    @Transactional
    public RestrictedHolidayRequestDTO applyRestrictedHoliday(RestrictedHolidayRequestDTO dto) {
        Employee employee = employeeRepository.findById(dto.getEmployeeId())
                .or(() -> employeeRepository.findByEmployeeId(dto.getEmployeeId()))
                .orElseThrow(() -> new ResourceNotFoundException("Employee not found with ID: " + dto.getEmployeeId()));

        Holiday holiday = holidayRepository.findById(dto.getHolidayId())
                .orElseThrow(() -> new ResourceNotFoundException("Holiday not found with ID: " + dto.getHolidayId()));

        if (!Boolean.TRUE.equals(holiday.getRestricted())) {
            throw new BadRequestException("Selected holiday is not a restricted holiday: " + holiday.getName());
        }

        int year = holiday.getYear() != null ? holiday.getYear() : holiday.getDate().getYear();

        // Check max restricted holidays allowed
        int maxAllowed = 2;
        if (employee.getBranchId() != null) {
            Optional<HolidayCalendar> cal = calendarRepository.findByYearAndBranchId(year, employee.getBranchId());
            if (cal.isPresent() && cal.get().getMaxRestrictedHolidaysAllowed() != null) {
                maxAllowed = cal.get().getMaxRestrictedHolidaysAllowed();
            }
        }

        List<RestrictedHolidayRequest> existing = requestRepository.findByEmployeeIdAndYear(employee.getId(), year);
        long approvedOrPending = existing.stream()
                .filter(r -> r.getStatus() == LeaveStatus.APPROVED || r.getStatus() == LeaveStatus.PENDING)
                .count();

        if (approvedOrPending >= maxAllowed) {
            throw new BadRequestException("Exceeded maximum allowed restricted holidays for year " + year + " (Limit: " + maxAllowed + ")");
        }

        String approverId = employee.getManagerId() != null ? employee.getManagerId() : "HR_ADMIN";

        RestrictedHolidayRequest request = RestrictedHolidayRequest.builder()
                .employeeId(employee.getId())
                .employeeName(employee.getFirstName() + " " + employee.getLastName())
                .holidayId(holiday.getId())
                .holidayName(holiday.getName())
                .holidayDate(holiday.getDate().toString())
                .year(year)
                .reason(dto.getReason())
                .status(LeaveStatus.PENDING)
                .approverId(approverId)
                .build();

        RestrictedHolidayRequest saved = requestRepository.save(request);
        log.info("Applied Restricted Holiday Request ID={}, Employee={}, Holiday={}", saved.getId(), employee.getEmployeeId(), holiday.getName());
        return mapToDTO(saved);
    }

    @Override
    @Transactional
    public RestrictedHolidayRequestDTO approveOrRejectRestrictedHoliday(String id, LeaveStatus status, String approverId, String approverName, String comments) {
        RestrictedHolidayRequest request = requestRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Restricted Holiday Request not found with ID: " + id));

        if (request.getStatus() != LeaveStatus.PENDING) {
            throw new BadRequestException("Request is already in status: " + request.getStatus());
        }

        request.setStatus(status);
        request.setApproverId(approverId);
        request.setApproverName(approverName);
        request.setComments(comments);
        request.setActionedAt(Instant.now());

        RestrictedHolidayRequest updated = requestRepository.save(request);
        log.info("Restricted Holiday Request {}: ID={}, Approver={}", status, id, approverId);
        return mapToDTO(updated);
    }

    @Override
    public RestrictedHolidayRequestDTO getRequestById(String id) {
        return requestRepository.findById(id)
                .map(this::mapToDTO)
                .orElseThrow(() -> new ResourceNotFoundException("Restricted Holiday Request not found with ID: " + id));
    }

    @Override
    public List<RestrictedHolidayRequestDTO> getEmployeeRequests(String employeeId, Integer year) {
        if (year != null) {
            return requestRepository.findByEmployeeIdAndYear(employeeId, year).stream()
                    .map(this::mapToDTO)
                    .collect(Collectors.toList());
        }
        return requestRepository.findByEmployeeId(employeeId).stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<RestrictedHolidayRequestDTO> getPendingRequestsForApprover(String approverId) {
        return requestRepository.findByApproverIdAndStatus(approverId, LeaveStatus.PENDING).stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<RestrictedHolidayRequestDTO> getAllRequests() {
        return requestRepository.findAll().stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    private RestrictedHolidayRequestDTO mapToDTO(RestrictedHolidayRequest entity) {
        return RestrictedHolidayRequestDTO.builder()
                .id(entity.getId())
                .employeeId(entity.getEmployeeId())
                .employeeName(entity.getEmployeeName())
                .holidayId(entity.getHolidayId())
                .holidayName(entity.getHolidayName())
                .holidayDate(entity.getHolidayDate())
                .year(entity.getYear())
                .reason(entity.getReason())
                .status(entity.getStatus())
                .approverId(entity.getApproverId())
                .approverName(entity.getApproverName())
                .comments(entity.getComments())
                .actionedAt(entity.getActionedAt())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .build();
    }
}
