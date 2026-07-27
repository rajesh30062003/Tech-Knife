package com.techknife.leave.analytics;

import com.techknife.employee.repository.EmployeeRepository;
import com.techknife.holiday.repository.HolidayRepository;
import com.techknife.leave.dto.LeaveAnalyticsDTO;
import com.techknife.leave.entity.LeaveRequest;
import com.techknife.leave.entity.LeaveStatus;
import com.techknife.leave.entity.WFHStatus;
import com.techknife.leave.entity.WorkFromHomeRequest;
import com.techknife.leave.repository.LeaveRequestRepository;
import com.techknife.leave.repository.WorkFromHomeRequestRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class LeaveAnalyticsService {

    private final LeaveRequestRepository leaveRequestRepository;
    private final WorkFromHomeRequestRepository wfhRequestRepository;
    private final HolidayRepository holidayRepository;
    private final EmployeeRepository employeeRepository;

    public LeaveAnalyticsDTO getLeaveAnalytics(Integer year) {
        int reqYear = (year != null) ? year : LocalDate.now().getYear();

        List<LeaveRequest> approvedLeaves = leaveRequestRepository.findByStatus(LeaveStatus.APPROVED);
        List<WorkFromHomeRequest> approvedWfh = wfhRequestRepository.findByStatus(WFHStatus.APPROVED);

        double totalLeaveConsumption = approvedLeaves.stream()
                .mapToDouble(l -> l.getTotalDays() != null ? l.getTotalDays() : 0.0)
                .sum();

        long employeeCount = employeeRepository.count();
        double avgLeaveDays = employeeCount > 0 ? totalLeaveConsumption / employeeCount : 0.0;

        // Department Leave Trend
        Map<String, Double> deptTrend = new HashMap<>();
        Map<String, List<LeaveRequest>> byDept = approvedLeaves.stream()
                .filter(l -> l.getDepartmentId() != null)
                .collect(Collectors.groupingBy(LeaveRequest::getDepartmentId));

        byDept.forEach((dept, list) -> {
            double days = list.stream().mapToDouble(l -> l.getTotalDays() != null ? l.getTotalDays() : 0.0).sum();
            deptTrend.put(dept, days);
        });

        // Leave Type Distribution
        Map<String, Double> typeDist = new HashMap<>();
        Map<String, List<LeaveRequest>> byType = approvedLeaves.stream()
                .filter(l -> l.getLeaveTypeName() != null)
                .collect(Collectors.groupingBy(LeaveRequest::getLeaveTypeName));

        byType.forEach((type, list) -> {
            double days = list.stream().mapToDouble(l -> l.getTotalDays() != null ? l.getTotalDays() : 0.0).sum();
            typeDist.put(type, days);
        });

        // WFH Trend by Dept
        Map<String, Double> wfhTrend = new HashMap<>();
        Map<String, List<WorkFromHomeRequest>> wfhByDept = approvedWfh.stream()
                .filter(w -> w.getDepartmentId() != null)
                .collect(Collectors.groupingBy(WorkFromHomeRequest::getDepartmentId));

        wfhByDept.forEach((dept, list) -> {
            double days = list.stream().mapToDouble(w -> w.getTotalDays() != null ? w.getTotalDays() : 0.0).sum();
            wfhTrend.put(dept, days);
        });

        // Holiday Utilization
        Map<String, Double> holidayUtil = new HashMap<>();
        holidayUtil.put("Total Holidays", (double) holidayRepository.findByYear(reqYear).size());

        List<LeaveAnalyticsDTO.MonthlyTrend> monthlyTrends = new ArrayList<>();
        for (int m = 1; m <= 12; m++) {
            final int monthVal = m;
            double lDays = approvedLeaves.stream()
                    .filter(l -> l.getStartDate() != null && l.getStartDate().getMonthValue() == monthVal)
                    .mapToDouble(l -> l.getTotalDays() != null ? l.getTotalDays() : 0.0)
                    .sum();

            double wDays = approvedWfh.stream()
                    .filter(w -> w.getStartDate() != null && w.getStartDate().getMonthValue() == monthVal)
                    .mapToDouble(w -> w.getTotalDays() != null ? w.getTotalDays() : 0.0)
                    .sum();

            monthlyTrends.add(LeaveAnalyticsDTO.MonthlyTrend.builder()
                    .month("Month " + m)
                    .leaveDays(lDays)
                    .wfhDays(wDays)
                    .build());
        }

        return LeaveAnalyticsDTO.builder()
                .totalLeaveConsumption(totalLeaveConsumption)
                .averageLeaveDaysPerEmployee(Math.round(avgLeaveDays * 100.0) / 100.0)
                .departmentLeaveTrend(deptTrend)
                .leaveTypeDistribution(typeDist)
                .wfhTrend(wfhTrend)
                .holidayUtilization(holidayUtil)
                .monthlyConsumptionTrend(monthlyTrends)
                .build();
    }
}
