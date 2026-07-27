package com.techknife.leave.dashboard;

import com.techknife.leave.dto.LeaveDashboardDTO;
import com.techknife.leave.entity.LeaveBalance;
import com.techknife.leave.entity.LeaveRequest;
import com.techknife.leave.entity.LeaveStatus;
import com.techknife.leave.repository.LeaveBalanceRepository;
import com.techknife.leave.repository.LeaveRequestRepository;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class LeaveDashboardService {

    private final LeaveRequestRepository leaveRequestRepository;
    private final LeaveBalanceRepository leaveBalanceRepository;

    public LeaveDashboardDTO getLeaveDashboard() {
        LocalDate today = LocalDate.now();

        List<LeaveRequest> pendingRequests = leaveRequestRepository.findByStatus(LeaveStatus.PENDING);
        List<LeaveRequest> approvedList = leaveRequestRepository.findByStatus(LeaveStatus.APPROVED);
        List<LeaveRequest> rejectedList = leaveRequestRepository.findByStatus(LeaveStatus.REJECTED);

        List<LeaveRequest> approvedToday = approvedList.stream()
                .filter(r -> r.getUpdatedAt() != null && r.getUpdatedAt().toString().startsWith(today.toString()))
                .collect(Collectors.toList());

        List<LeaveRequest> rejectedToday = rejectedList.stream()
                .filter(r -> r.getUpdatedAt() != null && r.getUpdatedAt().toString().startsWith(today.toString()))
                .collect(Collectors.toList());

        List<LeaveRequest> upcomingLeaves = leaveRequestRepository.findByStartDateGreaterThanEqualAndStatus(today, LeaveStatus.APPROVED);

        List<LeaveDashboardDTO.LeaveSummaryItem> pendingItems = pendingRequests.stream()
                .map(this::mapToSummaryItem)
                .collect(Collectors.toList());

        List<LeaveDashboardDTO.LeaveSummaryItem> approvedTodayItems = approvedToday.stream()
                .map(this::mapToSummaryItem)
                .collect(Collectors.toList());

        List<LeaveDashboardDTO.LeaveSummaryItem> upcomingItems = upcomingLeaves.stream()
                .map(this::mapToSummaryItem)
                .collect(Collectors.toList());

        // Balance summary
        List<LeaveBalance> balances = leaveBalanceRepository.findAll();
        List<LeaveDashboardDTO.LeaveBalanceItem> balanceItems = balances.stream()
                .map(b -> LeaveDashboardDTO.LeaveBalanceItem.builder()
                        .leaveTypeName("Balance for Employee " + b.getEmployeeId())
                        .totalAllocated(b.getTotalAllocated() != null ? b.getTotalAllocated() : 0.0)
                        .used(b.getUsed() != null ? b.getUsed() : 0.0)
                        .remaining(b.getRemaining() != null ? b.getRemaining() : 0.0)
                        .build())
                .limit(10)
                .collect(Collectors.toList());

        // Department Statistics
        Map<String, Long> departmentStats = approvedList.stream()
                .filter(r -> r.getDepartmentId() != null)
                .collect(Collectors.groupingBy(LeaveRequest::getDepartmentId, Collectors.counting()));

        return LeaveDashboardDTO.builder()
                .pendingLeaveRequests(pendingRequests.size())
                .approvedToday(approvedToday.size())
                .rejectedToday(rejectedToday.size())
                .upcomingLeavesCount(upcomingLeaves.size())
                .pendingRequests(pendingItems)
                .approvedTodayList(approvedTodayItems)
                .upcomingLeaves(upcomingItems)
                .leaveBalanceSummary(balanceItems)
                .departmentLeaveStatistics(departmentStats)
                .build();
    }

    private LeaveDashboardDTO.LeaveSummaryItem mapToSummaryItem(LeaveRequest request) {
        return LeaveDashboardDTO.LeaveSummaryItem.builder()
                .id(request.getId())
                .employeeId(request.getEmployeeId())
                .employeeName(request.getEmployeeName())
                .leaveTypeName(request.getLeaveTypeName())
                .startDate(request.getStartDate())
                .endDate(request.getEndDate())
                .totalDays(request.getTotalDays())
                .reason(request.getReason())
                .status(request.getStatus() != null ? request.getStatus().name() : "PENDING")
                .build();
    }
}
