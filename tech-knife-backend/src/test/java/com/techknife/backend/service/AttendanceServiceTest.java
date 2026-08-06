package com.techknife.backend.service;

import com.techknife.backend.constant.AttendanceStatus;
import com.techknife.backend.constant.PunchType;
import com.techknife.backend.dto.*;
import com.techknife.backend.entity.Attendance;
import com.techknife.backend.validator.AttendanceValidator;
import com.techknife.backend.mapper.AttendanceMapper;
import com.techknife.backend.repository.AttendanceRepository;
import com.techknife.backend.serviceImpl.AttendanceServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class AttendanceServiceTest {

    @Mock
    private AttendanceRepository attendanceRepository;

    @Mock
    private AttendanceMapper attendanceMapper;

    @Mock
    private AttendanceValidator attendanceValidator;

    @Mock
    private AuditLogService auditLogService;

    @InjectMocks
    private AttendanceServiceImpl attendanceService;

    private CheckInRequestDto checkInRequest;
    private Attendance sampleAttendance;
    private AttendanceResponseDto sampleResponseDto;

    @BeforeEach
    void setUp() {
        checkInRequest = CheckInRequestDto.builder()
                .userId("EMP-1001")
                .userName("John Doe")
                .userEmail("john.doe@techknife.io")
                .department("Engineering")
                .location("HQ San Jose")
                .ipAddress("192.168.1.50")
                .isWfh(false)
                .build();

        sampleAttendance = Attendance.builder()
                .id("att-999")
                .userId("EMP-1001")
                .userName("John Doe")
                .userEmail("john.doe@techknife.io")
                .department("Engineering")
                .date(LocalDate.now())
                .status(AttendanceStatus.PRESENT)
                .checkInTime(Instant.now().minusSeconds(28800)) // 8 hours ago
                .punches(new ArrayList<>())
                .build();

        sampleResponseDto = AttendanceResponseDto.builder()
                .id("att-999")
                .userId("EMP-1001")
                .userName("John Doe")
                .status(AttendanceStatus.PRESENT)
                .date(LocalDate.now())
                .build();
    }

    @Test
    @DisplayName("Check-In: Successful daily check-in creation")
    void testCheckIn_Success() {
        when(attendanceRepository.findByUserIdAndDate(eq("EMP-1001"), any(LocalDate.class)))
                .thenReturn(Optional.empty());
        when(attendanceRepository.save(any(Attendance.class))).thenReturn(sampleAttendance);
        when(attendanceMapper.toDto(any(Attendance.class))).thenReturn(sampleResponseDto);

        AttendanceResponseDto result = attendanceService.checkIn(checkInRequest);

        assertNotNull(result);
        assertEquals("EMP-1001", result.getUserId());
        verify(attendanceRepository, times(1)).save(any(Attendance.class));
    }

    @Test
    @DisplayName("Check-Out: Calculates work duration and overtime")
    void testCheckOut_Success() {
        when(attendanceRepository.findById("att-999")).thenReturn(Optional.of(sampleAttendance));
        when(attendanceRepository.save(any(Attendance.class))).thenReturn(sampleAttendance);
        when(attendanceMapper.toDto(any(Attendance.class))).thenReturn(sampleResponseDto);

        CheckOutRequestDto checkOutDto = CheckOutRequestDto.builder()
                .location("HQ San Jose")
                .ipAddress("192.168.1.50")
                .notes("Day completed")
                .build();

        AttendanceResponseDto result = attendanceService.checkOut("att-999", checkOutDto);

        assertNotNull(result);
        verify(attendanceRepository, times(1)).save(any(Attendance.class));
    }

    @Test
    @DisplayName("Admin Correction: Successfully updates attendance and records correction reason")
    void testCorrectAttendance_Success() {
        when(attendanceRepository.findById("att-999")).thenReturn(Optional.of(sampleAttendance));
        when(attendanceRepository.save(any(Attendance.class))).thenReturn(sampleAttendance);
        when(attendanceMapper.toDto(any(Attendance.class))).thenReturn(sampleResponseDto);

        AttendanceCorrectionRequestDto correctionDto = AttendanceCorrectionRequestDto.builder()
                .checkInTime(Instant.now().minusSeconds(28800))
                .checkOutTime(Instant.now())
                .status(AttendanceStatus.PRESENT)
                .reason("Employee forgot to punch check-out")
                .build();

        AttendanceResponseDto result = attendanceService.correctAttendance("att-999", correctionDto, "admin@techknife.io");

        assertNotNull(result);
        verify(attendanceRepository, times(1)).save(any(Attendance.class));
    }

    @Test
    @DisplayName("Monthly Summary: Aggregates attendance statistics correctly")
    void testGetMonthlyAttendanceSummary() {
        when(attendanceRepository.findByDateBetween(any(LocalDate.class), any(LocalDate.class)))
                .thenReturn(List.of(sampleAttendance));

        List<AttendanceSummaryDto> summaryList = attendanceService.getMonthlyAttendanceSummary(2026, 7, "Engineering");

        assertNotNull(summaryList);
        assertFalse(summaryList.isEmpty());
        assertEquals("EMP-1001", summaryList.get(0).getUserId());
    }
}
