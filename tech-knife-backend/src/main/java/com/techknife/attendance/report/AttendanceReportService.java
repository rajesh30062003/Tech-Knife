package com.techknife.attendance.report;

import com.techknife.attendance.dto.AttendanceReportDTO;
import com.techknife.attendance.dto.CompOffDTO;
import com.techknife.attendance.entity.AttendanceRecord;
import com.techknife.attendance.repository.AttendanceRecordRepository;
import com.techknife.attendance.repository.CompOffGrantRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AttendanceReportService {

    private final AttendanceRecordRepository attendanceRecordRepository;
    private final CompOffGrantRepository compOffGrantRepository;

    public List<AttendanceReportDTO> getAttendanceRegister(LocalDate startDate, LocalDate endDate, String departmentId) {
        List<AttendanceRecord> records = (startDate != null && endDate != null)
                ? attendanceRecordRepository.findByDateBetween(startDate, endDate)
                : attendanceRecordRepository.findAll();

        if (departmentId != null && !departmentId.isBlank()) {
            records = records.stream()
                    .filter(r -> departmentId.equals(r.getDepartmentId()))
                    .collect(Collectors.toList());
        }

        return records.stream().map(this::mapToDTO).collect(Collectors.toList());
    }

    public List<AttendanceReportDTO> getDailyReport(LocalDate date) {
        LocalDate targetDate = (date != null) ? date : LocalDate.now();
        return attendanceRecordRepository.findByDate(targetDate)
                .stream().map(this::mapToDTO).collect(Collectors.toList());
    }

    public List<AttendanceReportDTO> getWeeklyReport(LocalDate startDate) {
        LocalDate start = (startDate != null) ? startDate : LocalDate.now().minusDays(7);
        LocalDate end = start.plusDays(7);
        return attendanceRecordRepository.findByDateBetween(start, end)
                .stream().map(this::mapToDTO).collect(Collectors.toList());
    }

    public List<AttendanceReportDTO> getMonthlyReport(Integer year, Integer month) {
        int reqYear = (year != null) ? year : LocalDate.now().getYear();
        int reqMonth = (month != null) ? month : LocalDate.now().getMonthValue();
        LocalDate start = LocalDate.of(reqYear, reqMonth, 1);
        LocalDate end = start.plusMonths(1).minusDays(1);

        return attendanceRecordRepository.findByDateBetween(start, end)
                .stream().map(this::mapToDTO).collect(Collectors.toList());
    }

    public List<AttendanceReportDTO> getShiftReport(String shiftId, LocalDate date) {
        LocalDate targetDate = (date != null) ? date : LocalDate.now();
        return attendanceRecordRepository.findByDate(targetDate).stream()
                .filter(r -> shiftId == null || shiftId.equals(r.getShiftId()))
                .map(this::mapToDTO).collect(Collectors.toList());
    }

    public List<AttendanceReportDTO> getLateArrivalReport(LocalDate startDate, LocalDate endDate) {
        LocalDate start = (startDate != null) ? startDate : LocalDate.now().minusDays(30);
        LocalDate end = (endDate != null) ? endDate : LocalDate.now();

        return attendanceRecordRepository.findByDateBetween(start, end).stream()
                .filter(r -> Boolean.TRUE.equals(r.getIsLate()))
                .map(this::mapToDTO).collect(Collectors.toList());
    }

    public List<AttendanceReportDTO> getOvertimeReport(LocalDate startDate, LocalDate endDate) {
        LocalDate start = (startDate != null) ? startDate : LocalDate.now().minusDays(30);
        LocalDate end = (endDate != null) ? endDate : LocalDate.now();

        return attendanceRecordRepository.findByDateBetween(start, end).stream()
                .filter(r -> r.getOvertimeHours() != null && r.getOvertimeHours() > 0.0)
                .map(this::mapToDTO).collect(Collectors.toList());
    }

    public List<CompOffDTO> getCompOffReport() {
        return compOffGrantRepository.findAll().stream()
                .map(g -> CompOffDTO.builder()
                        .id(g.getId())
                        .employeeId(g.getEmployeeId())
                        .employeeName(g.getEmployeeName())
                        .workedDate(g.getWorkedDate())
                        .daysGranted(g.getDaysGranted())
                        .reason(g.getReason())
                        .status(g.getStatus())
                        .expiryDate(g.getExpiryDate())
                        .approverId(g.getApproverId())
                        .createdAt(g.getCreatedAt())
                        .build())
                .collect(Collectors.toList());
    }

    public String exportToCsv(List<AttendanceReportDTO> reports) {
        StringBuilder csv = new StringBuilder();
        csv.append("Employee ID,Employee Name,Department ID,Date,Check In,Check Out,Status,Work Hours,Overtime Hours,Is Late,Is Early Exit,Remarks\n");
        for (AttendanceReportDTO r : reports) {
            csv.append(String.format("%s,\"%s\",%s,%s,%s,%s,%s,%.2f,%.2f,%b,%b,\"%s\"\n",
                    r.getEmployeeId(),
                    r.getEmployeeName() != null ? r.getEmployeeName() : "",
                    r.getDepartmentId() != null ? r.getDepartmentId() : "",
                    r.getDate(),
                    r.getCheckIn() != null ? r.getCheckIn() : "",
                    r.getCheckOut() != null ? r.getCheckOut() : "",
                    r.getStatus(),
                    r.getWorkHours() != null ? r.getWorkHours() : 0.0,
                    r.getOvertimeHours() != null ? r.getOvertimeHours() : 0.0,
                    r.getIsLate() != null ? r.getIsLate() : false,
                    r.getIsEarlyExit() != null ? r.getIsEarlyExit() : false,
                    r.getRemarks() != null ? r.getRemarks() : ""
            ));
        }
        return csv.toString();
    }

    private AttendanceReportDTO mapToDTO(AttendanceRecord record) {
        return AttendanceReportDTO.builder()
                .employeeId(record.getEmployeeId())
                .employeeName(record.getEmployeeName())
                .departmentId(record.getDepartmentId())
                .branchId(record.getBranchId())
                .date(record.getDate())
                .checkIn(record.getCheckIn() != null ? record.getCheckIn().toString() : null)
                .checkOut(record.getCheckOut() != null ? record.getCheckOut().toString() : null)
                .status(record.getStatus() != null ? record.getStatus().name() : null)
                .workHours(record.getWorkHours())
                .overtimeHours(record.getOvertimeHours())
                .isLate(record.getIsLate())
                .isEarlyExit(record.getIsEarlyExit())
                .lateMinutes(record.getLateMinutes())
                .earlyExitMinutes(record.getEarlyExitMinutes())
                .remarks(record.getRemarks())
                .build();
    }
}
