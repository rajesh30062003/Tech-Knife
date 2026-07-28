package com.techknife.attendance.workflow;

import com.techknife.attendance.dto.AttendanceRegularizationDTO;
import com.techknife.attendance.entity.*;
import com.techknife.attendance.repository.AttendanceRecordRepository;
import com.techknife.attendance.repository.AttendanceRegularizationRepository;
import com.techknife.employee.entity.Employee;

import com.techknife.employee.repository.EmployeeRepository;
import com.techknife.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AttendanceWorkflowService {

    private final AttendanceRegularizationRepository regularizationRepository;
    private final AttendanceRecordRepository attendanceRecordRepository;
    private final EmployeeRepository employeeRepository;

    public AttendanceRegularizationDTO submitRegularization(AttendanceRegularizationDTO.Request request) {
        Employee employee = employeeRepository.findByEmployeeId(request.getEmployeeId())
                .orElseThrow(() -> new ResourceNotFoundException("Employee not found with ID: " + request.getEmployeeId()));

        Optional<AttendanceRecord> recordOpt = attendanceRecordRepository.findByEmployeeIdAndDate(request.getEmployeeId(), request.getDate());

        AttendanceRegularization reg = AttendanceRegularization.builder()
                .employeeId(request.getEmployeeId())
                .employeeName(employee.getFirstName() + " " + employee.getLastName())
                .departmentId(employee.getDepartmentId())
                .attendanceRecordId(recordOpt.map(AttendanceRecord::getId).orElse(null))
                .date(request.getDate())
                .type(request.getType())
                .requestedCheckIn(request.getRequestedCheckIn())
                .requestedCheckOut(request.getRequestedCheckOut())
                .reason(request.getReason())
                .status(RegularizationStatus.PENDING)
                .approverId(employee.getManagerId())
                .build();

        AttendanceRegularization saved = regularizationRepository.save(reg);
        return mapToDTO(saved);
    }

    public AttendanceRegularizationDTO approveOrReject(String id, RegularizationStatus status, String approverId, String approverName, String comments) {
        AttendanceRegularization reg = regularizationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Regularization request not found with ID: " + id));

        reg.setStatus(status);
        reg.setApproverId(approverId);
        reg.setApproverName(approverName);
        reg.setApproverComments(comments);
        reg.setApprovedOrRejectedAt(Instant.now());

        if (status == RegularizationStatus.APPROVED) {
            // Update or create AttendanceRecord
            AttendanceRecord record = attendanceRecordRepository.findByEmployeeIdAndDate(reg.getEmployeeId(), reg.getDate())
                    .orElse(AttendanceRecord.builder()
                            .employeeId(reg.getEmployeeId())
                            .employeeName(reg.getEmployeeName())
                            .departmentId(reg.getDepartmentId())
                            .date(reg.getDate())
                            .build());

            if (reg.getRequestedCheckIn() != null) {
                record.setCheckIn(reg.getRequestedCheckIn());
            }
            if (reg.getRequestedCheckOut() != null) {
                record.setCheckOut(reg.getRequestedCheckOut());
            }

            if (record.getCheckIn() != null && record.getCheckOut() != null) {
                long minutes = Duration.between(record.getCheckIn(), record.getCheckOut()).toMinutes();
                record.setWorkHours((double) minutes / 60.0);
            }

            record.setStatus(AttendanceStatus.PRESENT);
            record.setIsRegularized(true);
            record.setRemarks("Regularized (" + reg.getType() + "): " + reg.getReason());
            attendanceRecordRepository.save(record);
        }

        AttendanceRegularization saved = regularizationRepository.save(reg);
        return mapToDTO(saved);
    }

    public List<AttendanceRegularizationDTO> getEmployeeHistory(String employeeId) {
        return regularizationRepository.findByEmployeeId(employeeId)
                .stream().map(this::mapToDTO).collect(Collectors.toList());
    }

    public List<AttendanceRegularizationDTO> getPendingForApprover(String approverId) {
        return regularizationRepository.findByApproverIdAndStatus(approverId, RegularizationStatus.PENDING)
                .stream().map(this::mapToDTO).collect(Collectors.toList());
    }

    public List<AttendanceRegularizationDTO> getAllRegularizations() {
        return regularizationRepository.findAll()
                .stream().map(this::mapToDTO).collect(Collectors.toList());
    }

    private AttendanceRegularizationDTO mapToDTO(AttendanceRegularization entity) {
        return AttendanceRegularizationDTO.builder()
                .id(entity.getId())
                .employeeId(entity.getEmployeeId())
                .employeeName(entity.getEmployeeName())
                .departmentId(entity.getDepartmentId())
                .attendanceRecordId(entity.getAttendanceRecordId())
                .date(entity.getDate())
                .type(entity.getType())
                .requestedCheckIn(entity.getRequestedCheckIn())
                .requestedCheckOut(entity.getRequestedCheckOut())
                .reason(entity.getReason())
                .status(entity.getStatus())
                .approverId(entity.getApproverId())
                .approverName(entity.getApproverName())
                .approverComments(entity.getApproverComments())
                .approvedOrRejectedAt(entity.getApprovedOrRejectedAt())
                .createdAt(entity.getCreatedAt())
                .build();
    }
}
