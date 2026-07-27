package com.techknife.widgets;

import com.techknife.attendance.entity.AttendanceRecord;
import com.techknife.attendance.entity.AttendanceStatus;
import com.techknife.attendance.entity.RegularizationStatus;
import com.techknife.attendance.repository.AttendanceRecordRepository;
import com.techknife.attendance.repository.AttendanceRegularizationRepository;
import com.techknife.attendance.repository.CompOffGrantRepository;
import com.techknife.leave.entity.LeaveRequest;
import com.techknife.leave.entity.LeaveStatus;
import com.techknife.leave.entity.WFHStatus;
import com.techknife.leave.repository.LeaveRequestRepository;
import com.techknife.leave.repository.WorkFromHomeRequestRepository;
import com.techknife.widgets.dto.HrWidgetsDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class HrWidgetsService {

    private final AttendanceRecordRepository attendanceRecordRepository;
    private final LeaveRequestRepository leaveRequestRepository;
    private final WorkFromHomeRequestRepository wfhRequestRepository;
    private final AttendanceRegularizationRepository regularizationRepository;
    private final CompOffGrantRepository compOffGrantRepository;

    public HrWidgetsDTO getHrWidgetsData() {
        LocalDate today = LocalDate.now();
        LocalDate last30Days = today.minusDays(30);

        // 1. Attendance Heatmap (Date -> Count of Present/WFH employees)
        List<AttendanceRecord> records30Days = attendanceRecordRepository.findByDateBetween(last30Days, today);
        Map<String, Integer> heatmap = new HashMap<>();
        records30Days.stream()
                .filter(r -> r.getStatus() == AttendanceStatus.PRESENT || r.getStatus() == AttendanceStatus.WFH)
                .forEach(r -> {
                    String dateKey = r.getDate().toString();
                    heatmap.put(dateKey, heatmap.getOrDefault(dateKey, 0) + 1);
                });

        // 2. Leave Calendar (Approved upcoming leaves)
        List<LeaveRequest> upcomingLeaves = leaveRequestRepository.findByStartDateGreaterThanEqualAndStatus(today, LeaveStatus.APPROVED);
        List<HrWidgetsDTO.LeaveCalendarItem> leaveCal = upcomingLeaves.stream()
                .map(l -> HrWidgetsDTO.LeaveCalendarItem.builder()
                        .employeeId(l.getEmployeeId())
                        .employeeName(l.getEmployeeName())
                        .leaveTypeName(l.getLeaveTypeName())
                        .startDate(l.getStartDate())
                        .endDate(l.getEndDate())
                        .build())
                .collect(Collectors.toList());

        // 3. Late Employees Today
        List<AttendanceRecord> lateToday = attendanceRecordRepository.findByDateAndIsLateTrue(today);
        List<HrWidgetsDTO.LateEmployeeItem> lateList = lateToday.stream()
                .map(l -> HrWidgetsDTO.LateEmployeeItem.builder()
                        .employeeId(l.getEmployeeId())
                        .employeeName(l.getEmployeeName())
                        .departmentId(l.getDepartmentId())
                        .lateMinutes(l.getLateMinutes() != null ? l.getLateMinutes() : 0)
                        .checkInTime(l.getCheckIn() != null ? l.getCheckIn().toString() : "N/A")
                        .build())
                .collect(Collectors.toList());

        // 4. Pending Approvals
        long pendingLeaves = leaveRequestRepository.findByStatus(LeaveStatus.PENDING).size();
        long pendingWfh = wfhRequestRepository.findByStatus(WFHStatus.PENDING).size();
        long pendingRegs = regularizationRepository.countByStatus(RegularizationStatus.PENDING);
        long pendingCompOffs = compOffGrantRepository.findByStatus("PENDING").size();

        HrWidgetsDTO.PendingApprovalsSummary pendingSummary = HrWidgetsDTO.PendingApprovalsSummary.builder()
                .pendingLeaves(pendingLeaves)
                .pendingWfh(pendingWfh)
                .pendingRegularizations(pendingRegs)
                .pendingCompOffs(pendingCompOffs)
                .build();

        // 5. Expiring Comp-Off (Expiring in next 15 days)
        LocalDate in15Days = today.plusDays(15);
        List<HrWidgetsDTO.ExpiringCompOffItem> expiringCompOff = compOffGrantRepository.findByStatusAndExpiryDateBefore("APPROVED", in15Days)
                .stream()
                .map(g -> HrWidgetsDTO.ExpiringCompOffItem.builder()
                        .id(g.getId())
                        .employeeId(g.getEmployeeId())
                        .employeeName(g.getEmployeeName())
                        .days(g.getDaysGranted())
                        .expiryDate(g.getExpiryDate())
                        .build())
                .collect(Collectors.toList());

        // 6. Shift Summary Today
        List<AttendanceRecord> todayRecords = attendanceRecordRepository.findByDate(today);
        Map<String, List<AttendanceRecord>> byShift = todayRecords.stream()
                .filter(r -> r.getShiftId() != null)
                .collect(Collectors.groupingBy(AttendanceRecord::getShiftId));

        List<HrWidgetsDTO.ShiftSummaryItem> shiftSummary = new ArrayList<>();
        byShift.forEach((shiftId, recs) -> {
            long present = recs.stream().filter(r -> r.getStatus() == AttendanceStatus.PRESENT || r.getStatus() == AttendanceStatus.WFH).count();
            shiftSummary.add(HrWidgetsDTO.ShiftSummaryItem.builder()
                    .shiftId(shiftId)
                    .shiftName("Shift " + shiftId)
                    .assignedEmployees(recs.size())
                    .presentToday(present)
                    .build());
        });

        return HrWidgetsDTO.builder()
                .attendanceHeatmap(heatmap)
                .leaveCalendar(leaveCal)
                .lateEmployees(lateList)
                .pendingApprovals(pendingSummary)
                .expiringCompOff(expiringCompOff)
                .shiftSummary(shiftSummary)
                .build();
    }
}
