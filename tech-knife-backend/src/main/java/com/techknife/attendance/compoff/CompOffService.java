package com.techknife.attendance.compoff;

import com.techknife.attendance.dto.CompOffDTO;
import com.techknife.attendance.entity.CompOffBalance;
import com.techknife.attendance.entity.CompOffGrant;
import com.techknife.attendance.repository.CompOffBalanceRepository;
import com.techknife.attendance.repository.CompOffGrantRepository;
import com.techknife.employee.repository.EmployeeRepository;
import com.techknife.employee.entity.Employee;

import com.techknife.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CompOffService {

    private final CompOffGrantRepository compOffGrantRepository;
    private final CompOffBalanceRepository compOffBalanceRepository;
    private final EmployeeRepository employeeRepository;

    public CompOffDTO generateCompOff(CompOffDTO.Request request) {
        Employee employee = employeeRepository.findByEmployeeId(request.getEmployeeId())
                .orElseThrow(() -> new ResourceNotFoundException("Employee not found with ID: " + request.getEmployeeId()));

        double days = (request.getDaysGranted() != null) ? request.getDaysGranted() : 1.0;
        LocalDate expiryDate = request.getWorkedDate().plusDays(90); // 90 days validity rule

        CompOffGrant grant = CompOffGrant.builder()
                .employeeId(request.getEmployeeId())
                .employeeName(employee.getFirstName() + " " + employee.getLastName())
                .workedDate(request.getWorkedDate())
                .daysGranted(days)
                .reason(request.getReason())
                .status("PENDING")
                .expiryDate(expiryDate)
                .approverId(employee.getManagerId())
                .build();

        CompOffGrant saved = compOffGrantRepository.save(grant);
        return mapToDTO(saved);
    }

    public CompOffDTO approveOrRejectCompOff(String id, String status, String approverId, String comments) {
        CompOffGrant grant = compOffGrantRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Comp-Off grant not found with ID: " + id));

        grant.setStatus(status);
        grant.setApproverId(approverId);
        grant.setApproverComments(comments);

        if ("APPROVED".equalsIgnoreCase(status)) {
            // Update balance
            CompOffBalance balance = compOffBalanceRepository.findByEmployeeId(grant.getEmployeeId())
                    .orElse(CompOffBalance.builder()
                            .employeeId(grant.getEmployeeId())
                            .availableDays(0.0)
                            .usedDays(0.0)
                            .expiredDays(0.0)
                            .build());

            balance.setAvailableDays(balance.getAvailableDays() + grant.getDaysGranted());
            compOffBalanceRepository.save(balance);
        }

        CompOffGrant saved = compOffGrantRepository.save(grant);
        return mapToDTO(saved);
    }

    public CompOffDTO consumeCompOff(String employeeId, Double days, String reason) {
        CompOffBalance balance = compOffBalanceRepository.findByEmployeeId(employeeId)
                .orElseThrow(() -> new ResourceNotFoundException("Comp-off balance not found for employee ID: " + employeeId));

        if (balance.getAvailableDays() < days) {
            throw new IllegalArgumentException("Insufficient Comp-Off balance. Available: " + balance.getAvailableDays());
        }

        balance.setAvailableDays(balance.getAvailableDays() - days);
        balance.setUsedDays(balance.getUsedDays() + days);
        compOffBalanceRepository.save(balance);

        CompOffGrant consumedRecord = CompOffGrant.builder()
                .employeeId(employeeId)
                .daysGranted(-days)
                .reason("Consumed: " + reason)
                .status("CONSUMED")
                .workedDate(LocalDate.now())
                .build();

        CompOffGrant saved = compOffGrantRepository.save(consumedRecord);
        return mapToDTO(saved);
    }

    public CompOffDTO.Balance getBalance(String employeeId) {
        CompOffBalance balance = compOffBalanceRepository.findByEmployeeId(employeeId)
                .orElse(CompOffBalance.builder()
                        .employeeId(employeeId)
                        .availableDays(0.0)
                        .usedDays(0.0)
                        .expiredDays(0.0)
                        .build());

        return CompOffDTO.Balance.builder()
                .employeeId(balance.getEmployeeId())
                .availableDays(balance.getAvailableDays())
                .usedDays(balance.getUsedDays())
                .expiredDays(balance.getExpiredDays())
                .build();
    }

    public List<CompOffDTO> getEmployeeCompOffs(String employeeId) {
        return compOffGrantRepository.findByEmployeeId(employeeId).stream()
                .map(this::mapToDTO).collect(Collectors.toList());
    }

    public List<CompOffDTO> getPendingCompOffs() {
        return compOffGrantRepository.findByStatus("PENDING").stream()
                .map(this::mapToDTO).collect(Collectors.toList());
    }

    private CompOffDTO mapToDTO(CompOffGrant entity) {
        return CompOffDTO.builder()
                .id(entity.getId())
                .employeeId(entity.getEmployeeId())
                .employeeName(entity.getEmployeeName())
                .workedDate(entity.getWorkedDate())
                .daysGranted(entity.getDaysGranted())
                .reason(entity.getReason())
                .status(entity.getStatus())
                .expiryDate(entity.getExpiryDate())
                .approverId(entity.getApproverId())
                .approverComments(entity.getApproverComments())
                .createdAt(entity.getCreatedAt())
                .build();
    }
}
