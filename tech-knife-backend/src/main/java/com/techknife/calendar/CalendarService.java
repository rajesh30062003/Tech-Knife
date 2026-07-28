package com.techknife.calendar;

import com.techknife.attendance.entity.AttendanceRecord;
import com.techknife.attendance.repository.AttendanceRecordRepository;
import com.techknife.calendar.dto.CalendarEventDTO;
import com.techknife.employee.repository.EmployeeRepository;
import com.techknife.employee.entity.Employee;

import com.techknife.holiday.entity.Holiday;
import com.techknife.holiday.repository.HolidayRepository;
import com.techknife.leave.entity.LeaveRequest;
import com.techknife.leave.entity.LeaveStatus;
import com.techknife.leave.entity.WFHStatus;
import com.techknife.leave.entity.WorkFromHomeRequest;
import com.techknife.leave.repository.LeaveRequestRepository;
import com.techknife.leave.repository.WorkFromHomeRequestRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CalendarService {

    private final AttendanceRecordRepository attendanceRecordRepository;
    private final LeaveRequestRepository leaveRequestRepository;
    private final WorkFromHomeRequestRepository wfhRequestRepository;
    private final HolidayRepository holidayRepository;
    private final EmployeeRepository employeeRepository;

    public List<CalendarEventDTO> getUnifiedCalendarEvents(LocalDate startDate, LocalDate endDate, String employeeId) {
        LocalDate start = (startDate != null) ? startDate : LocalDate.now().withDayOfMonth(1);
        LocalDate end = (endDate != null) ? endDate : start.plusMonths(1).minusDays(1);

        List<CalendarEventDTO> events = new ArrayList<>();

        // 1. Attendance Events
        List<AttendanceRecord> attendanceRecords = (employeeId != null && !employeeId.isBlank())
                ? attendanceRecordRepository.findByEmployeeIdAndDateBetween(employeeId, start, end)
                : attendanceRecordRepository.findByDateBetween(start, end);

        for (AttendanceRecord att : attendanceRecords) {
            events.add(CalendarEventDTO.builder()
                    .id("ATT-" + att.getId())
                    .employeeId(att.getEmployeeId())
                    .employeeName(att.getEmployeeName())
                    .type(CalendarEventDTO.EventType.ATTENDANCE)
                    .title("Attendance: " + att.getStatus())
                    .description("Work Hours: " + att.getWorkHours() + ", Overtime: " + att.getOvertimeHours())
                    .startDate(att.getDate())
                    .endDate(att.getDate())
                    .status(att.getStatus() != null ? att.getStatus().name() : "PRESENT")
                    .color("#10B981")
                    .build());
        }

        // 2. Leave Events
        List<LeaveRequest> leaves = (employeeId != null && !employeeId.isBlank())
                ? leaveRequestRepository.findByEmployeeIdAndStatus(employeeId, LeaveStatus.APPROVED)
                : leaveRequestRepository.findByStatus(LeaveStatus.APPROVED);

        for (LeaveRequest leave : leaves) {
            if (leave.getStartDate() != null && leave.getEndDate() != null &&
                    !leave.getStartDate().isAfter(end) && !leave.getEndDate().isBefore(start)) {
                events.add(CalendarEventDTO.builder()
                        .id("LEAVE-" + leave.getId())
                        .employeeId(leave.getEmployeeId())
                        .employeeName(leave.getEmployeeName())
                        .type(CalendarEventDTO.EventType.LEAVE)
                        .title("Leave: " + leave.getLeaveTypeName())
                        .description(leave.getReason())
                        .startDate(leave.getStartDate())
                        .endDate(leave.getEndDate())
                        .status("APPROVED")
                        .color("#EF4444")
                        .build());
            }
        }

        // 3. WFH Events
        List<WorkFromHomeRequest> wfhList = (employeeId != null && !employeeId.isBlank())
                ? wfhRequestRepository.findByEmployeeId(employeeId)
                : wfhRequestRepository.findByStatus(WFHStatus.APPROVED);

        for (WorkFromHomeRequest wfh : wfhList) {
            if (wfh.getStatus() == WFHStatus.APPROVED && wfh.getStartDate() != null && wfh.getEndDate() != null &&
                    !wfh.getStartDate().isAfter(end) && !wfh.getEndDate().isBefore(start)) {
                events.add(CalendarEventDTO.builder()
                        .id("WFH-" + wfh.getId())
                        .employeeId(wfh.getEmployeeId())
                        .employeeName(wfh.getEmployeeName())
                        .type(CalendarEventDTO.EventType.WFH)
                        .title("WFH: Work From Home")
                        .description(wfh.getReason())
                        .startDate(wfh.getStartDate())
                        .endDate(wfh.getEndDate())
                        .status("APPROVED")
                        .color("#3B82F6")
                        .build());
            }
        }

        // 4. Holidays
        List<Holiday> holidays = holidayRepository.findByDateBetween(start, end);
        for (Holiday h : holidays) {
            events.add(CalendarEventDTO.builder()
                    .id("HOLIDAY-" + h.getId())
                    .type(CalendarEventDTO.EventType.HOLIDAY)
                    .title("Holiday: " + h.getName())
                    .description(h.getDescription() != null ? h.getDescription() : h.getType().name())
                    .startDate(h.getDate())
                    .endDate(h.getDate())
                    .status(Boolean.TRUE.equals(h.getRestricted()) ? "RESTRICTED" : "PUBLIC")
                    .color("#F59E0B")
                    .build());
        }

        // 5. Employee Events (Birthdays, Joining Anniversary)
        List<Employee> employees = employeeRepository.findAll();
        for (Employee emp : employees) {
            if (emp.getDob() != null) {
                LocalDate bdayThisYear = emp.getDob().withYear(start.getYear());
                if (!bdayThisYear.isBefore(start) && !bdayThisYear.isAfter(end)) {
                    events.add(CalendarEventDTO.builder()
                            .id("EVENT-BDAY-" + emp.getEmployeeId())
                            .employeeId(emp.getEmployeeId())
                            .employeeName(emp.getFirstName() + " " + emp.getLastName())
                            .type(CalendarEventDTO.EventType.EMPLOYEE_EVENT)
                            .title("Birthday: " + emp.getFirstName() + " " + emp.getLastName())
                            .startDate(bdayThisYear)
                            .endDate(bdayThisYear)
                            .status("EVENT")
                            .color("#8B5CF6")
                            .build());
                }
            }
            if (emp.getJoiningDate() != null) {
                LocalDate annivThisYear = emp.getJoiningDate().withYear(start.getYear());
                if (!annivThisYear.isBefore(start) && !annivThisYear.isAfter(end)) {
                    events.add(CalendarEventDTO.builder()
                            .id("EVENT-ANNIV-" + emp.getEmployeeId())
                            .employeeId(emp.getEmployeeId())
                            .employeeName(emp.getFirstName() + " " + emp.getLastName())
                            .type(CalendarEventDTO.EventType.EMPLOYEE_EVENT)
                            .title("Work Anniversary: " + emp.getFirstName() + " " + emp.getLastName())
                            .startDate(annivThisYear)
                            .endDate(annivThisYear)
                            .status("EVENT")
                            .color("#EC4899")
                            .build());
                }
            }
        }

        return events;
    }
}
