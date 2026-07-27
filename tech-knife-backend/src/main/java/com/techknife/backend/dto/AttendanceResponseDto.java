package com.techknife.backend.dto;

import com.techknife.backend.constant.AttendanceStatus;
import com.techknife.backend.entity.PunchLog;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.time.LocalDate;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AttendanceResponseDto {
    private String id;
    private String userId;
    private String userEmail;
    private String userName;
    private String department;
    private LocalDate date;
    private AttendanceStatus status;
    private Instant checkInTime;
    private Instant checkOutTime;
    private long totalWorkMinutes;
    private long totalBreakMinutes;
    private long overtimeMinutes;
    private boolean isLateArrival;
    private boolean isEarlyLeaving;
    private boolean isHalfDay;
    private boolean isWorkFromHome;
    private boolean isHoliday;
    private boolean isWeekend;
    private String remarks;
    private String locationIn;
    private String locationOut;
    private String ipAddress;
    private List<PunchLog> punches;
    private boolean correctedByAdmin;
    private String correctionReason;
    private Instant createdAt;
    private Instant updatedAt;
}
