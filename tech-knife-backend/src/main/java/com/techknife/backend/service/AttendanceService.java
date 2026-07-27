package com.techknife.backend.service;

import com.techknife.backend.dto.*;

import java.time.LocalDate;
import java.util.List;

public interface AttendanceService {

    AttendanceResponseDto checkIn(CheckInRequestDto dto);

    AttendanceResponseDto checkOut(String attendanceId, CheckOutRequestDto dto);

    AttendanceResponseDto toggleBreak(String attendanceId, BreakRequestDto dto);

    AttendanceResponseDto getTodayAttendance(String userId);

    List<AttendanceResponseDto> getUserAttendanceHistory(String userId, LocalDate fromDate, LocalDate toDate);

    List<AttendanceResponseDto> getUserMonthlyCalendar(String userId, int year, int month);

    AttendanceResponseDto correctAttendance(String id, AttendanceCorrectionRequestDto dto, String adminEmail);

    AttendanceResponseDto createManualAttendance(ManualAttendanceRequestDto dto, String adminEmail);

    List<AttendanceResponseDto> bulkImportAttendance(BulkAttendanceImportDto dto, String adminEmail);

    List<AttendanceSummaryDto> getMonthlyAttendanceSummary(int year, int month, String department);

    AttendanceSummaryDto getYearlyAttendanceSummary(int year, String userId);

    AttendanceResponseDto getAttendanceById(String id);
}
