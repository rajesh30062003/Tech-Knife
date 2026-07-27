package com.techknife.attendance.analytics;

import com.techknife.attendance.dto.AttendanceAnalyticsDTO;
import com.techknife.attendance.entity.AttendanceRecord;
import com.techknife.attendance.entity.AttendanceStatus;
import com.techknife.attendance.repository.AttendanceRecordRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AttendanceAnalyticsService {

    private final AttendanceRecordRepository attendanceRecordRepository;

    public AttendanceAnalyticsDTO getAttendanceAnalytics(Integer year, Integer month) {
        LocalDate now = LocalDate.now();
        int reqYear = (year != null) ? year : now.getYear();
        int reqMonth = (month != null) ? month : now.getMonthValue();

        LocalDate startDate = LocalDate.of(reqYear, reqMonth, 1);
        LocalDate endDate = startDate.plusMonths(1).minusDays(1);

        List<AttendanceRecord> records = attendanceRecordRepository.findByDateBetween(startDate, endDate);

        long totalCount = records.size();
        long presentCount = records.stream()
                .filter(r -> r.getStatus() == AttendanceStatus.PRESENT || r.getStatus() == AttendanceStatus.WFH)
                .count();

        double monthlyPct = totalCount > 0 ? ((double) presentCount / totalCount) * 100.0 : 100.0;

        double avgWorkHours = records.stream()
                .mapToDouble(r -> r.getWorkHours() != null ? r.getWorkHours() : 0.0)
                .average()
                .orElse(8.0);

        // Grouping by week or day for trends
        Map<String, List<AttendanceRecord>> groupedByWeek = records.stream()
                .collect(Collectors.groupingBy(r -> "Week " + ((r.getDate().getDayOfMonth() - 1) / 7 + 1)));

        List<AttendanceAnalyticsDTO.TrendData> lateTrend = new ArrayList<>();
        List<AttendanceAnalyticsDTO.TrendData> earlyExitTrend = new ArrayList<>();
        List<AttendanceAnalyticsDTO.TrendData> overtimeTrend = new ArrayList<>();

        groupedByWeek.forEach((week, weekRecords) -> {
            long lateNum = weekRecords.stream().filter(r -> Boolean.TRUE.equals(r.getIsLate())).count();
            long earlyNum = weekRecords.stream().filter(r -> Boolean.TRUE.equals(r.getIsEarlyExit())).count();
            double otSum = weekRecords.stream().mapToDouble(r -> r.getOvertimeHours() != null ? r.getOvertimeHours() : 0.0).sum();

            lateTrend.add(AttendanceAnalyticsDTO.TrendData.builder().label(week).value(lateNum).build());
            earlyExitTrend.add(AttendanceAnalyticsDTO.TrendData.builder().label(week).value(earlyNum).build());
            overtimeTrend.add(AttendanceAnalyticsDTO.TrendData.builder().label(week).value(otSum).build());
        });

        // Department attendance map
        Map<String, Double> deptAttendance = new HashMap<>();
        Map<String, List<AttendanceRecord>> byDept = records.stream()
                .filter(r -> r.getDepartmentId() != null)
                .collect(Collectors.groupingBy(AttendanceRecord::getDepartmentId));

        byDept.forEach((dept, deptRecs) -> {
            long p = deptRecs.stream().filter(r -> r.getStatus() == AttendanceStatus.PRESENT || r.getStatus() == AttendanceStatus.WFH).count();
            double pct = deptRecs.size() > 0 ? ((double) p / deptRecs.size()) * 100.0 : 0.0;
            deptAttendance.put(dept, Math.round(pct * 100.0) / 100.0);
        });

        // Branch attendance map
        Map<String, Double> branchAttendance = new HashMap<>();
        Map<String, List<AttendanceRecord>> byBranch = records.stream()
                .filter(r -> r.getBranchId() != null)
                .collect(Collectors.groupingBy(AttendanceRecord::getBranchId));

        byBranch.forEach((branch, branchRecs) -> {
            long p = branchRecs.stream().filter(r -> r.getStatus() == AttendanceStatus.PRESENT || r.getStatus() == AttendanceStatus.WFH).count();
            double pct = branchRecs.size() > 0 ? ((double) p / branchRecs.size()) * 100.0 : 0.0;
            branchAttendance.put(branch, Math.round(pct * 100.0) / 100.0);
        });

        // Shift utilization
        Map<String, Double> shiftUtilization = new HashMap<>();
        Map<String, List<AttendanceRecord>> byShift = records.stream()
                .filter(r -> r.getShiftId() != null)
                .collect(Collectors.groupingBy(AttendanceRecord::getShiftId));

        byShift.forEach((shift, shiftRecs) -> {
            shiftUtilization.put(shift, (double) shiftRecs.size());
        });

        return AttendanceAnalyticsDTO.builder()
                .monthlyAttendancePercentage(Math.round(monthlyPct * 100.0) / 100.0)
                .averageWorkingHours(Math.round(avgWorkHours * 100.0) / 100.0)
                .lateArrivalTrend(lateTrend)
                .earlyExitTrend(earlyExitTrend)
                .overtimeTrend(overtimeTrend)
                .departmentAttendance(deptAttendance)
                .branchAttendance(branchAttendance)
                .shiftUtilization(shiftUtilization)
                .build();
    }
}
