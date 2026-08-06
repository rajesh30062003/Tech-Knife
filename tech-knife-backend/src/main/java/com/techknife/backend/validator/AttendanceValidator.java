package com.techknife.backend.validator;

import com.techknife.backend.constant.AttendanceStatus;
import com.techknife.backend.exception.BadRequestException;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.time.LocalTime;
import java.time.ZoneId;

@Component
public class AttendanceValidator {

    private static final LocalTime SHIFT_START_TIME = LocalTime.of(9, 0); // 09:00 AM
    private static final LocalTime LATE_THRESHOLD_TIME = LocalTime.of(9, 15); // 09:15 AM
    private static final LocalTime SHIFT_END_TIME = LocalTime.of(18, 0); // 06:00 PM
    private static final LocalTime EARLY_LEAVING_THRESHOLD = LocalTime.of(17, 30); // 05:30 PM

    public void validateCheckInTime(Instant checkInTime) {
        if (checkInTime == null) {
            throw new BadRequestException("Check-in timestamp cannot be null");
        }
        if (checkInTime.isAfter(Instant.now().plusSeconds(300))) {
            throw new BadRequestException("Check-in time cannot be in the future");
        }
    }

    public void validateCheckOutTime(Instant checkInTime, Instant checkOutTime) {
        if (checkOutTime == null) {
            throw new BadRequestException("Check-out timestamp cannot be null");
        }
        if (checkInTime != null && checkOutTime.isBefore(checkInTime)) {
            throw new BadRequestException("Check-out time cannot be before Check-in time");
        }
    }

    public boolean isLateArrival(Instant checkInTime) {
        if (checkInTime == null) return false;
        LocalTime time = checkInTime.atZone(ZoneId.systemDefault()).toLocalTime();
        return time.isAfter(LATE_THRESHOLD_TIME);
    }

    public boolean isEarlyLeaving(Instant checkOutTime) {
        if (checkOutTime == null) return false;
        LocalTime time = checkOutTime.atZone(ZoneId.systemDefault()).toLocalTime();
        return time.isBefore(EARLY_LEAVING_THRESHOLD);
    }

    public AttendanceStatus calculateStatus(Instant checkIn, Instant checkOut, long workMinutes, boolean isWfh, boolean isHoliday, boolean isWeekend) {
        if (isHoliday) return AttendanceStatus.HOLIDAY;
        if (isWeekend) return AttendanceStatus.WEEKEND;
        if (checkIn == null) return AttendanceStatus.ABSENT;
        
        if (workMinutes < 240 && workMinutes > 0) {
            return AttendanceStatus.HALF_DAY;
        }
        
        if (isWfh) return AttendanceStatus.WFH;
        if (isLateArrival(checkIn)) return AttendanceStatus.LATE;

        return AttendanceStatus.PRESENT;
    }
}
