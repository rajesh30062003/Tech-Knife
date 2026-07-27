package com.techknife.attendance.monthend;

import com.techknife.attendance.dto.MonthEndProcessDTO;
import com.techknife.attendance.entity.AttendanceRecord;
import com.techknife.attendance.entity.AttendanceStatus;
import com.techknife.attendance.entity.MonthlyAttendanceSummary;
import com.techknife.attendance.repository.AttendanceRecordRepository;
import com.techknife.attendance.repository.MonthlyAttendanceSummaryRepository;
import com.techknife.employee.repository.EmployeeRepository;
import com.techknife.entity.Employee;
import com.techknife.leave.entity.LeaveRequest;
import com.techknife.leave.entity.LeaveStatus;
import com.techknife.leave.entity.WFHStatus;
import com.techknife.leave.entity.WorkFromHomeRequest;
import com.techknife.leave.repository.LeaveRequestRepository;
import com.techknife.leave.repository.WorkFromHomeRequestRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MonthEndService {

    private final MonthlyAttendanceSummaryRepository summaryRepository;
    private final AttendanceRecordRepository attendanceRecordRepository;
    private final LeaveRequestRepository leaveRequestRepository;
    private final WorkFromHomeRequestRepository wfhRequestRepository;
    private final EmployeeRepository employeeRepository;

    public MonthEndProcessDTO processMonthEnd(Integer year, Integer month, String processedBy, boolean forceReprocess) {
        YearMonth yearMonth = YearMonth.of(year, month);
        LocalDate startDate = yearMonth.atDay(1);
        LocalDate endDate = yearMonth.atEndOfMonth();
        int daysInMonth = yearMonth.lengthOfMonth();

        List<Employee> employees = employeeRepository.findAll();
        List<MonthEndProcessDTO.MonthlyAttendanceSummaryDTO> summaryDTOs = new ArrayList<>();

        for (Employee emp : employees) {
            String empId = emp.getEmployeeId();

            summaryRepository.findByEmployeeIdAndYearAndMonth(empId, year, month).ifPresent(existing -> {
                if (Boolean.TRUE.equals(existing.getIsFrozen()) && !forceReprocess) {
                    throw new IllegalStateException("Attendance records for " + yearMonth + " are frozen and locked. Authorization required for reprocessing.");
                }
            });

            List<AttendanceRecord> records = attendanceRecordRepository.findByEmployeeIdAndDateBetween(empId, startDate, endDate);

            long presentCount = records.stream().filter(r -> r.getStatus() == AttendanceStatus.PRESENT).count();
            long absentCount = records.stream().filter(r -> r.getStatus() == AttendanceStatus.ABSENT).count();
            long lateCount = records.stream().filter(r -> Boolean.TRUE.equals(r.getIsLate())).count();

            List<LeaveRequest> leaveRequests = leaveRequestRepository.findByEmployeeIdAndStatus(empId, LeaveStatus.APPROVED);
            double leaveDays = leaveRequests.stream()
                    .filter(l -> l.getStartDate() != null && l.getStartDate().getMonthValue() == month && l.getStartDate().getYear() == year)
                    .mapToDouble(l -> l.getTotalDays() != null ? l.getTotalDays() : 0.0)
                    .sum();

            List<WorkFromHomeRequest> wfhRequests = wfhRequestRepository.findByEmployeeId(empId);
            double wfhDays = wfhRequests.stream()
                    .filter(w -> w.getStatus() == WFHStatus.APPROVED && w.getStartDate() != null && w.getStartDate().getMonthValue() == month && w.getStartDate().getYear() == year)
                    .mapToDouble(w -> w.getTotalDays() != null ? w.getTotalDays() : 0.0)
                    .sum();

            double otHours = records.stream()
                    .mapToDouble(r -> r.getOvertimeHours() != null ? r.getOvertimeHours() : 0.0)
                    .sum();

            double payableDays = presentCount + leaveDays + wfhDays;

            MonthlyAttendanceSummary summary = summaryRepository.findByEmployeeIdAndYearAndMonth(empId, year, month)
                    .orElse(MonthlyAttendanceSummary.builder()
                            .employeeId(empId)
                            .employeeName(emp.getFirstName() + " " + emp.getLastName())
                            .departmentId(emp.getDepartmentId())
                            .year(year)
                            .month(month)
                            .build());

            summary.setTotalDays(daysInMonth);
            summary.setPresentDays((double) presentCount);
            summary.setAbsentDays((double) absentCount);
            summary.setLeaveDays(leaveDays);
            summary.setWfhDays(wfhDays);
            summary.setLateDays((int) lateCount);
            summary.setOvertimeHours(otHours);
            summary.setPayableDays(payableDays);

            MonthlyAttendanceSummary saved = summaryRepository.save(summary);
            summaryDTOs.add(mapToDTO(saved));
        }

        return MonthEndProcessDTO.builder()
                .year(year)
                .month(month)
                .totalEmployeesProcessed(employees.size())
                .totalRecordsFrozen(0)
                .isFrozen(false)
                .processedAt(Instant.now())
                .processedBy(processedBy)
                .summaries(summaryDTOs)
                .build();
    }

    public MonthEndProcessDTO freezeAttendance(Integer year, Integer month, String frozenBy) {
        YearMonth yearMonth = YearMonth.of(year, month);
        LocalDate startDate = yearMonth.atDay(1);
        LocalDate endDate = yearMonth.atEndOfMonth();

        List<AttendanceRecord> records = attendanceRecordRepository.findByDateBetween(startDate, endDate);
        for (AttendanceRecord record : records) {
            record.setIsFrozen(true);
        }
        attendanceRecordRepository.saveAll(records);

        List<MonthlyAttendanceSummary> summaries = summaryRepository.findByYearAndMonth(year, month);
        for (MonthlyAttendanceSummary summary : summaries) {
            summary.setIsFrozen(true);
            summary.setFrozenAt(Instant.now());
            summary.setFrozenBy(frozenBy);
            summary.setIsPayrollReady(true);
        }
        List<MonthlyAttendanceSummary> savedSummaries = summaryRepository.saveAll(summaries);

        List<MonthEndProcessDTO.MonthlyAttendanceSummaryDTO> summaryDTOs = savedSummaries.stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());

        return MonthEndProcessDTO.builder()
                .year(year)
                .month(month)
                .totalEmployeesProcessed(summaries.size())
                .totalRecordsFrozen(records.size())
                .isFrozen(true)
                .processedAt(Instant.now())
                .processedBy(frozenBy)
                .summaries(summaryDTOs)
                .build();
    }

    public List<MonthEndProcessDTO.MonthlyAttendanceSummaryDTO> getPayrollReadySummary(Integer year, Integer month) {
        return summaryRepository.findByYearAndMonth(year, month).stream()
                .filter(s -> Boolean.TRUE.equals(s.getIsPayrollReady()))
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    private MonthEndProcessDTO.MonthlyAttendanceSummaryDTO mapToDTO(MonthlyAttendanceSummary summary) {
        return MonthEndProcessDTO.MonthlyAttendanceSummaryDTO.builder()
                .id(summary.getId())
                .employeeId(summary.getEmployeeId())
                .employeeName(summary.getEmployeeName())
                .departmentId(summary.getDepartmentId())
                .year(summary.getYear())
                .month(summary.getMonth())
                .totalDays(summary.getTotalDays())
                .presentDays(summary.getPresentDays())
                .absentDays(summary.getAbsentDays())
                .leaveDays(summary.getLeaveDays())
                .wfhDays(summary.getWfhDays())
                .lateDays(summary.getLateDays())
                .overtimeHours(summary.getOvertimeHours())
                .payableDays(summary.getPayableDays())
                .isFrozen(summary.getIsFrozen())
                .isPayrollReady(summary.getIsPayrollReady())
                .build();
    }
}
