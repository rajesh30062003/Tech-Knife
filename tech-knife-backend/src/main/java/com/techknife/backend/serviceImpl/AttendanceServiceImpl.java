package com.techknife.backend.serviceImpl;

import com.techknife.backend.constant.AttendanceStatus;
import com.techknife.backend.constant.PunchType;
import com.techknife.backend.dto.*;
import com.techknife.backend.entity.Attendance;
import com.techknife.backend.entity.PunchLog;
import com.techknife.backend.validator.AttendanceValidator;
import com.techknife.backend.exception.BadRequestException;
import com.techknife.backend.exception.ResourceNotFoundException;
import com.techknife.backend.mapper.AttendanceMapper;
import com.techknife.backend.repository.AttendanceRepository;
import com.techknife.backend.service.AuditLogService;
import com.techknife.backend.service.AttendanceService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.*;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class AttendanceServiceImpl implements AttendanceService {

    private final AttendanceRepository attendanceRepository;
    private final AttendanceMapper attendanceMapper;
    private final AttendanceValidator attendanceValidator;
    private final AuditLogService auditLogService;

    @Override
    @Transactional
    public AttendanceResponseDto checkIn(CheckInRequestDto dto) {
        LocalDate today = LocalDate.now();
        Instant now = Instant.now();

        attendanceValidator.validateCheckInTime(now);

        Optional<Attendance> existing = attendanceRepository.findByUserIdAndDate(dto.getUserId(), today);
        if (existing.isPresent() && existing.get().getCheckInTime() != null) {
            throw new BadRequestException("User has already checked in for today: " + today);
        }

        boolean isLate = attendanceValidator.isLateArrival(now);
        boolean isWeekend = today.getDayOfWeek() == DayOfWeek.SATURDAY || today.getDayOfWeek() == DayOfWeek.SUNDAY;

        AttendanceStatus initialStatus = isWeekend ? AttendanceStatus.WEEKEND
                : (dto.isWfh() ? AttendanceStatus.WFH : (isLate ? AttendanceStatus.LATE : AttendanceStatus.PRESENT));

        PunchLog initialPunch = PunchLog.builder()
                .punchType(PunchType.CHECK_IN)
                .timestamp(now)
                .location(dto.getLocation() != null ? dto.getLocation() : "HQ Main Campus")
                .ipAddress(dto.getIpAddress() != null ? dto.getIpAddress() : "127.0.0.1")
                .notes(dto.getNotes() != null ? dto.getNotes() : "Daily Check-In")
                .editedByAdmin(false)
                .build();

        Attendance attendance = existing.orElseGet(() -> Attendance.builder()
                .userId(dto.getUserId())
                .userEmail(dto.getUserEmail())
                .userName(dto.getUserName() != null ? dto.getUserName() : "Employee " + dto.getUserId())
                .department(dto.getDepartment() != null ? dto.getDepartment() : "Engineering")
                .date(today)
                .punches(new ArrayList<>())
                .build());

        attendance.setCheckInTime(now);
        attendance.setStatus(initialStatus);
        attendance.setLateArrival(isLate);
        attendance.setWorkFromHome(dto.isWfh());
        attendance.setWeekend(isWeekend);
        attendance.setLocationIn(dto.getLocation());
        attendance.setIpAddress(dto.getIpAddress());
        getOrInitPunches(attendance).add(initialPunch);

        Attendance saved = attendanceRepository.save(attendance);
        log.info("Check-in successful for user: {} on date: {}", dto.getUserId(), today);

        try {
            auditLogService.logAction(dto.getUserId(), dto.getUserName(), "ATTENDANCE", "CHECK_IN",
                    "User checked in at " + now.toString() + " from " + dto.getLocation(), dto.getIpAddress(), "System");
        } catch (Exception e) {
            log.warn("Failed to capture audit log for check-in: {}", e.getMessage());
        }

        return attendanceMapper.toDto(saved);
    }

    private static List<PunchLog> getOrInitPunches(Attendance a) {
        if (a.getPunches() == null) {
            a.setPunches(new ArrayList<>());
        }
        return a.getPunches();
    }

    @Override
    @Transactional
    public AttendanceResponseDto checkOut(String attendanceId, CheckOutRequestDto dto) {
        Attendance attendance = attendanceRepository.findById(attendanceId)
                .orElseThrow(() -> new ResourceNotFoundException("Attendance record not found with ID: " + attendanceId));

        Instant now = Instant.now();
        attendanceValidator.validateCheckOutTime(attendance.getCheckInTime(), now);

        attendance.setCheckOutTime(now);
        attendance.setLocationOut(dto.getLocation());

        boolean isEarly = attendanceValidator.isEarlyLeaving(now);
        attendance.setEarlyLeaving(isEarly);

        // Calculate total work minutes & overtime
        long diffSeconds = Duration.between(attendance.getCheckInTime(), now).getSeconds();
        long grossMinutes = Math.max(0, diffSeconds / 60);
        long netWorkMinutes = Math.max(0, grossMinutes - attendance.getTotalBreakMinutes());

        attendance.setTotalWorkMinutes(netWorkMinutes);

        // Standard work shift is 8 hours (480 minutes)
        if (netWorkMinutes > 480) {
            attendance.setOvertimeMinutes(netWorkMinutes - 480);
        } else {
            attendance.setOvertimeMinutes(0);
        }

        if (netWorkMinutes < 240) {
            attendance.setHalfDay(true);
            attendance.setStatus(AttendanceStatus.HALF_DAY);
        }

        PunchLog checkOutPunch = PunchLog.builder()
                .punchType(PunchType.CHECK_OUT)
                .timestamp(now)
                .location(dto.getLocation())
                .ipAddress(dto.getIpAddress())
                .notes(dto.getNotes() != null ? dto.getNotes() : "Daily Check-Out")
                .build();

        if (attendance.getPunches() == null) {
            attendance.setPunches(new ArrayList<>());
        }
        attendance.getPunches().add(checkOutPunch);

        Attendance updated = attendanceRepository.save(attendance);

        try {
            auditLogService.logAction(attendance.getUserId(), attendance.getUserName(), "ATTENDANCE", "CHECK_OUT",
                    "User checked out. Net work time: " + (netWorkMinutes / 60) + "h " + (netWorkMinutes % 60) + "m",
                    dto.getIpAddress(), "System");
        } catch (Exception e) {
            log.warn("Failed to write audit log: {}", e.getMessage());
        }

        return attendanceMapper.toDto(updated);
    }

    @Override
    @Transactional
    public AttendanceResponseDto toggleBreak(String attendanceId, BreakRequestDto dto) {
        Attendance attendance = attendanceRepository.findById(attendanceId)
                .orElseThrow(() -> new ResourceNotFoundException("Attendance record not found with ID: " + attendanceId));

        Instant now = Instant.now();
        if (attendance.getPunches() == null) {
            attendance.setPunches(new ArrayList<>());
        }

        PunchLog breakPunch = PunchLog.builder()
                .punchType(dto.getPunchType())
                .timestamp(now)
                .location(dto.getLocation())
                .ipAddress(dto.getIpAddress())
                .notes(dto.getNotes() != null ? dto.getNotes() : "Break log")
                .build();

        attendance.getPunches().add(breakPunch);

        // Recalculate break time if break ended
        if (dto.getPunchType() == PunchType.BREAK_END) {
            recalculateBreakTime(attendance);
        }

        Attendance updated = attendanceRepository.save(attendance);
        return attendanceMapper.toDto(updated);
    }

    private void recalculateBreakTime(Attendance attendance) {
        List<PunchLog> punches = attendance.getPunches();
        if (punches == null || punches.isEmpty()) return;

        long totalBreakSeconds = 0;
        Instant currentBreakStart = null;

        for (PunchLog p : punches) {
            if (p.getPunchType() == PunchType.BREAK_START) {
                currentBreakStart = p.getTimestamp();
            } else if (p.getPunchType() == PunchType.BREAK_END && currentBreakStart != null) {
                totalBreakSeconds += Duration.between(currentBreakStart, p.getTimestamp()).getSeconds();
                currentBreakStart = null;
            }
        }

        attendance.setTotalBreakMinutes(totalBreakSeconds / 60);

        // Re-adjust net work minutes if checked out
        if (attendance.getCheckInTime() != null && attendance.getCheckOutTime() != null) {
            long gross = Duration.between(attendance.getCheckInTime(), attendance.getCheckOutTime()).getSeconds() / 60;
            long net = Math.max(0, gross - attendance.getTotalBreakMinutes());
            attendance.setTotalWorkMinutes(net);
            attendance.setOvertimeMinutes(net > 480 ? net - 480 : 0);
        }
    }

    @Override
    public AttendanceResponseDto getTodayAttendance(String userId) {
        LocalDate today = LocalDate.now();
        Attendance record = attendanceRepository.findByUserIdAndDate(userId, today)
                .orElse(null);
        return attendanceMapper.toDto(record);
    }

    @Override
    public List<AttendanceResponseDto> getUserAttendanceHistory(String userId, LocalDate fromDate, LocalDate toDate) {
        if (fromDate == null) fromDate = LocalDate.now().minusDays(30);
        if (toDate == null) toDate = LocalDate.now();

        List<Attendance> list = attendanceRepository.findByUserIdAndDateBetween(userId, fromDate, toDate);
        return list.stream().map(attendanceMapper::toDto).collect(Collectors.toList());
    }

    @Override
    public List<AttendanceResponseDto> getUserMonthlyCalendar(String userId, int year, int month) {
        LocalDate startDate = LocalDate.of(year, month, 1);
        LocalDate endDate = startDate.plusMonths(1).minusDays(1);

        List<Attendance> list = attendanceRepository.findByUserIdAndDateBetween(userId, startDate, endDate);
        return list.stream().map(attendanceMapper::toDto).collect(Collectors.toList());
    }

    @Override
    @Transactional
    public AttendanceResponseDto correctAttendance(String id, AttendanceCorrectionRequestDto dto, String adminEmail) {
        Attendance attendance = attendanceRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Attendance record not found with ID: " + id));

        attendance.setCheckInTime(dto.getCheckInTime());
        attendance.setCheckOutTime(dto.getCheckOutTime());
        if (dto.getStatus() != null) {
            attendance.setStatus(dto.getStatus());
        }
        if (dto.getTotalBreakMinutes() >= 0) {
            attendance.setTotalBreakMinutes(dto.getTotalBreakMinutes());
        }
        if (dto.getRemarks() != null) {
            attendance.setRemarks(dto.getRemarks());
        }

        // Recalculate metrics
        if (dto.getCheckInTime() != null && dto.getCheckOutTime() != null) {
            long gross = Duration.between(dto.getCheckInTime(), dto.getCheckOutTime()).getSeconds() / 60;
            long net = Math.max(0, gross - attendance.getTotalBreakMinutes());
            attendance.setTotalWorkMinutes(net);
            attendance.setOvertimeMinutes(net > 480 ? net - 480 : 0);
        }

        attendance.setCorrectedByAdmin(true);
        attendance.setCorrectionReason(dto.getReason());

        Attendance saved = attendanceRepository.save(attendance);

        try {
            auditLogService.logAction(adminEmail, "Admin", "ATTENDANCE", "CORRECTION",
                    "Corrected attendance for ID: " + id + ". Reason: " + dto.getReason(), "127.0.0.1", "Admin Portal");
        } catch (Exception e) {
            log.warn("Failed audit log write for correction: {}", e.getMessage());
        }

        return attendanceMapper.toDto(saved);
    }

    @Override
    @Transactional
    public AttendanceResponseDto createManualAttendance(ManualAttendanceRequestDto dto, String adminEmail) {
        Optional<Attendance> existing = attendanceRepository.findByUserIdAndDate(dto.getUserId(), dto.getDate());
        Attendance attendance = existing.orElseGet(() -> Attendance.builder()
                .userId(dto.getUserId())
                .userEmail(dto.getUserEmail())
                .userName(dto.getUserName() != null ? dto.getUserName() : "Employee " + dto.getUserId())
                .department(dto.getDepartment() != null ? dto.getDepartment() : "General")
                .date(dto.getDate())
                .punches(new ArrayList<>())
                .build());

        attendance.setStatus(dto.getStatus());
        attendance.setCheckInTime(dto.getCheckInTime());
        attendance.setCheckOutTime(dto.getCheckOutTime());
        attendance.setTotalBreakMinutes(dto.getTotalBreakMinutes());
        attendance.setWorkFromHome(dto.isWfh());
        attendance.setRemarks(dto.getRemarks());
        attendance.setCorrectedByAdmin(true);
        attendance.setCorrectionReason("Manual Attendance Entry by Admin (" + adminEmail + ")");

        if (dto.getCheckInTime() != null && dto.getCheckOutTime() != null) {
            long gross = Duration.between(dto.getCheckInTime(), dto.getCheckOutTime()).getSeconds() / 60;
            long net = Math.max(0, gross - dto.getTotalBreakMinutes());
            attendance.setTotalWorkMinutes(net);
            attendance.setOvertimeMinutes(net > 480 ? net - 480 : 0);
        }

        Attendance saved = attendanceRepository.save(attendance);
        return attendanceMapper.toDto(saved);
    }

    @Override
    @Transactional
    public List<AttendanceResponseDto> bulkImportAttendance(BulkAttendanceImportDto dto, String adminEmail) {
        List<AttendanceResponseDto> results = new ArrayList<>();
        if (dto.getRecords() == null) return results;

        for (ManualAttendanceRequestDto item : dto.getRecords()) {
            AttendanceResponseDto res = createManualAttendance(item, adminEmail);
            results.add(res);
        }

        try {
            auditLogService.logAction(adminEmail, "Admin", "ATTENDANCE", "BULK_IMPORT",
                    "Bulk imported " + results.size() + " attendance records in batch: " + dto.getBatchName(), "127.0.0.1", "Admin Portal");
        } catch (Exception e) {
            log.warn("Failed bulk import audit log: {}", e.getMessage());
        }

        return results;
    }

    @Override
    public List<AttendanceSummaryDto> getMonthlyAttendanceSummary(int year, int month, String department) {
        LocalDate startDate = LocalDate.of(year, month, 1);
        LocalDate endDate = startDate.plusMonths(1).minusDays(1);

        List<Attendance> records = attendanceRepository.findByDateBetween(startDate, endDate);

        if (department != null && !department.isBlank()) {
            records = records.stream()
                    .filter(r -> department.equalsIgnoreCase(r.getDepartment()))
                    .collect(Collectors.toList());
        }

        Map<String, List<Attendance>> byUser = records.stream()
                .collect(Collectors.groupingBy(Attendance::getUserId));

        List<AttendanceSummaryDto> summaries = new ArrayList<>();

        for (Map.Entry<String, List<Attendance>> entry : byUser.entrySet()) {
            List<Attendance> userRecords = entry.getValue();
            Attendance sample = userRecords.get(0);

            int present = 0, absent = 0, late = 0, half = 0, wfh = 0, holiday = 0, weekend = 0, leave = 0;
            long totalMinutes = 0, totalOtMinutes = 0;

            Map<String, Integer> breakdown = new HashMap<>();

            for (Attendance r : userRecords) {
                if (r.getStatus() != null) {
                    breakdown.put(r.getStatus().name(), breakdown.getOrDefault(r.getStatus().name(), 0) + 1);
                }
                if (r.getStatus() == AttendanceStatus.PRESENT) present++;
                else if (r.getStatus() == AttendanceStatus.ABSENT) absent++;
                else if (r.getStatus() == AttendanceStatus.LATE) { late++; present++; }
                else if (r.getStatus() == AttendanceStatus.HALF_DAY) half++;
                else if (r.getStatus() == AttendanceStatus.WFH) { wfh++; present++; }
                else if (r.getStatus() == AttendanceStatus.HOLIDAY) holiday++;
                else if (r.getStatus() == AttendanceStatus.WEEKEND) weekend++;
                else if (r.getStatus() == AttendanceStatus.LEAVE) leave++;

                totalMinutes += r.getTotalWorkMinutes();
                totalOtMinutes += r.getOvertimeMinutes();
            }

            int totalWorkingDays = endDate.getDayOfMonth() - (weekend + holiday);
            double percentage = totalWorkingDays > 0 ? ((double) present / totalWorkingDays) * 100 : 0.0;

            summaries.add(AttendanceSummaryDto.builder()
                    .userId(entry.getKey())
                    .userName(sample.getUserName())
                    .department(sample.getDepartment())
                    .periodYear(year)
                    .periodMonth(month)
                    .totalDays(userRecords.size())
                    .presentDays(present)
                    .absentDays(absent)
                    .lateDays(late)
                    .halfDays(half)
                    .wfhDays(wfh)
                    .holidayDays(holiday)
                    .weekendDays(weekend)
                    .leaveDays(leave)
                    .totalWorkingHours(totalMinutes / 60.0)
                    .totalOvertimeHours(totalOtMinutes / 60.0)
                    .averageDailyHours(userRecords.isEmpty() ? 0 : (totalMinutes / 60.0) / userRecords.size())
                    .attendancePercentage(Math.min(100.0, Math.round(percentage * 10.0) / 10.0))
                    .statusBreakdown(breakdown)
                    .build());
        }

        return summaries;
    }

    @Override
    public AttendanceSummaryDto getYearlyAttendanceSummary(int year, String userId) {
        LocalDate startDate = LocalDate.of(year, 1, 1);
        LocalDate endDate = LocalDate.of(year, 12, 31);

        List<Attendance> records = attendanceRepository.findByUserIdAndDateBetween(userId, startDate, endDate);

        if (records.isEmpty()) {
            return AttendanceSummaryDto.builder()
                    .userId(userId)
                    .periodYear(year)
                    .totalDays(0)
                    .build();
        }

        Attendance sample = records.get(0);
        int present = 0, absent = 0, late = 0, half = 0, wfh = 0, holiday = 0, weekend = 0, leave = 0;
        long totalMinutes = 0, totalOtMinutes = 0;

        for (Attendance r : records) {
            if (r.getStatus() == AttendanceStatus.PRESENT) present++;
            else if (r.getStatus() == AttendanceStatus.ABSENT) absent++;
            else if (r.getStatus() == AttendanceStatus.LATE) late++;
            else if (r.getStatus() == AttendanceStatus.HALF_DAY) half++;
            else if (r.getStatus() == AttendanceStatus.WFH) wfh++;
            else if (r.getStatus() == AttendanceStatus.HOLIDAY) holiday++;
            else if (r.getStatus() == AttendanceStatus.WEEKEND) weekend++;
            else if (r.getStatus() == AttendanceStatus.LEAVE) leave++;

            totalMinutes += r.getTotalWorkMinutes();
            totalOtMinutes += r.getOvertimeMinutes();
        }

        return AttendanceSummaryDto.builder()
                .userId(userId)
                .userName(sample.getUserName())
                .department(sample.getDepartment())
                .periodYear(year)
                .totalDays(records.size())
                .presentDays(present)
                .absentDays(absent)
                .lateDays(late)
                .halfDays(half)
                .wfhDays(wfh)
                .holidayDays(holiday)
                .weekendDays(weekend)
                .leaveDays(leave)
                .totalWorkingHours(totalMinutes / 60.0)
                .totalOvertimeHours(totalOtMinutes / 60.0)
                .averageDailyHours(records.isEmpty() ? 0 : (totalMinutes / 60.0) / records.size())
                .attendancePercentage(records.isEmpty() ? 0 : Math.round(((double) (present + wfh) / records.size()) * 100.0 * 10) / 10.0)
                .build();
    }

    @Override
    public AttendanceResponseDto getAttendanceById(String id) {
        Attendance attendance = attendanceRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Attendance record not found with ID: " + id));
        return attendanceMapper.toDto(attendance);
    }
}
