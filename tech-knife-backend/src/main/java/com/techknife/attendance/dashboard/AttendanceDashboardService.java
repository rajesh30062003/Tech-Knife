package com.techknife.attendance.dashboard;

import com.techknife.attendance.dto.AttendanceDashboardDTO;
import com.techknife.attendance.entity.AttendanceRecord;
import com.techknife.attendance.entity.AttendanceStatus;
import com.techknife.attendance.repository.AttendanceRecordRepository;
import com.techknife.attendance.repository.AttendanceRegularizationRepository;
import com.techknife.attendance.entity.RegularizationStatus;
import com.techknife.employee.repository.EmployeeRepository;
import com.techknife.leave.entity.LeaveRequest;
import com.techknife.leave.entity.LeaveStatus;
import com.techknife.leave.entity.WFHStatus;
import com.techknife.leave.entity.WorkFromHomeRequest;
import com.techknife.leave.repository.LeaveRequestRepository;
import com.techknife.leave.repository.WorkFromHomeRequestRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AttendanceDashboardService {

    private final AttendanceRecordRepository attendanceRecordRepository;
    private final AttendanceRegularizationRepository regularizationRepository;
    private final LeaveRequestRepository leaveRequestRepository;
    private final WorkFromHomeRequestRepository wfhRequestRepository;
    private final EmployeeRepository employeeRepository;

    public AttendanceDashboardDTO getTodayDashboard(LocalDate date) {
        LocalDate queryDate = (date != null) ? date : LocalDate.now();

        long totalEmployees = employeeRepository.count();
        long presentCount = attendanceRecordRepository.countByDateAndStatus(queryDate, AttendanceStatus.PRESENT);
        long absentCount = attendanceRecordRepository.countByDateAndStatus(queryDate, AttendanceStatus.ABSENT);
        long lateCount = attendanceRecordRepository.countByDateAndIsLateTrue(queryDate);
        long earlyExitCount = attendanceRecordRepository.countByDateAndIsEarlyExitTrue(queryDate);

        List<LeaveRequest> onLeaveToday = leaveRequestRepository.findByStatusAndStartDateLessThanEqualAndEndDateGreaterThanEqual(
                LeaveStatus.APPROVED, queryDate, queryDate);
        long onLeaveCount = onLeaveToday.size();

        List<WorkFromHomeRequest> wfhToday = wfhRequestRepository.findByStatusAndStartDateLessThanEqualAndEndDateGreaterThanEqual(
                WFHStatus.APPROVED, queryDate, queryDate);
        long wfhCount = wfhToday.size();

        List<AttendanceRecord> overtimeRecords = attendanceRecordRepository.findByDateAndOvertimeHoursGreaterThan(queryDate, 0.0);
        long overtimeCount = overtimeRecords.size();
        double totalOvertimeHoursToday = overtimeRecords.stream()
                .mapToDouble(r -> r.getOvertimeHours() != null ? r.getOvertimeHours() : 0.0)
                .sum();

        long pendingCorrectionsCount = regularizationRepository.countByStatus(RegularizationStatus.PENDING);

        // Monthly attendance % calculation for current month
        LocalDate startOfMonth = queryDate.withDayOfMonth(1);
        List<AttendanceRecord> monthRecords = attendanceRecordRepository.findByDateBetween(startOfMonth, queryDate);
        long totalMonthRecords = monthRecords.size();
        long monthPresentCount = monthRecords.stream()
                .filter(r -> r.getStatus() == AttendanceStatus.PRESENT || r.getStatus() == AttendanceStatus.WFH)
                .count();
        double monthlyAttendancePercentage = totalMonthRecords > 0
                ? ((double) monthPresentCount / totalMonthRecords) * 100.0
                : 100.0;

        DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm");

        List<AttendanceDashboardDTO.EmployeeAttendanceSummary> lateList = attendanceRecordRepository.findByDateAndIsLateTrue(queryDate)
                .stream()
                .map(r -> AttendanceDashboardDTO.EmployeeAttendanceSummary.builder()
                        .employeeId(r.getEmployeeId())
                        .employeeName(r.getEmployeeName())
                        .departmentId(r.getDepartmentId())
                        .time(r.getCheckIn() != null ? r.getCheckIn().toString() : "N/A")
                        .remarks("Late by " + (r.getLateMinutes() != null ? r.getLateMinutes() : 0) + " mins")
                        .build())
                .collect(Collectors.toList());

        List<AttendanceDashboardDTO.EmployeeAttendanceSummary> earlyExitList = attendanceRecordRepository.findByDateAndIsEarlyExitTrue(queryDate)
                .stream()
                .map(r -> AttendanceDashboardDTO.EmployeeAttendanceSummary.builder()
                        .employeeId(r.getEmployeeId())
                        .employeeName(r.getEmployeeName())
                        .departmentId(r.getDepartmentId())
                        .time(r.getCheckOut() != null ? r.getCheckOut().toString() : "N/A")
                        .remarks("Early exit by " + (r.getEarlyExitMinutes() != null ? r.getEarlyExitMinutes() : 0) + " mins")
                        .build())
                .collect(Collectors.toList());

        List<AttendanceDashboardDTO.EmployeeAttendanceSummary> leaveList = onLeaveToday.stream()
                .map(l -> AttendanceDashboardDTO.EmployeeAttendanceSummary.builder()
                        .employeeId(l.getEmployeeId())
                        .employeeName(l.getEmployeeName())
                        .departmentId(l.getDepartmentId())
                        .time(l.getLeaveTypeName())
                        .remarks(l.getReason())
                        .build())
                .collect(Collectors.toList());

        List<AttendanceDashboardDTO.EmployeeAttendanceSummary> wfhList = wfhToday.stream()
                .map(w -> AttendanceDashboardDTO.EmployeeAttendanceSummary.builder()
                        .employeeId(w.getEmployeeId())
                        .employeeName(w.getEmployeeName())
                        .departmentId(w.getDepartmentId())
                        .time("WFH")
                        .remarks(w.getReason())
                        .build())
                .collect(Collectors.toList());

        return AttendanceDashboardDTO.builder()
                .date(queryDate)
                .totalEmployees(totalEmployees)
                .presentCount(presentCount)
                .absentCount(absentCount)
                .lateCount(lateCount)
                .earlyExitCount(earlyExitCount)
                .onLeaveCount(onLeaveCount)
                .wfhCount(wfhCount)
                .overtimeCount(overtimeCount)
                .totalOvertimeHoursToday(totalOvertimeHoursToday)
                .pendingCorrectionsCount(pendingCorrectionsCount)
                .monthlyAttendancePercentage(Math.round(monthlyAttendancePercentage * 100.0) / 100.0)
                .lateEmployees(lateList)
                .earlyExitEmployees(earlyExitList)
                .onLeaveEmployees(leaveList)
                .wfhEmployees(wfhList)
                .build();
    }
}
