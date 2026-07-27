package com.techknife.backend.mapper;

import com.techknife.backend.dto.AttendanceResponseDto;
import com.techknife.backend.entity.Attendance;
import org.springframework.stereotype.Component;

import java.util.ArrayList;

@Component
public class AttendanceMapper {

    public AttendanceResponseDto toDto(Attendance entity) {
        if (entity == null) {
            return null;
        }

        return AttendanceResponseDto.builder()
                .id(entity.getId())
                .userId(entity.getUserId())
                .userEmail(entity.getUserEmail())
                .userName(entity.getUserName())
                .department(entity.getDepartment())
                .date(entity.getDate())
                .status(entity.getStatus())
                .checkInTime(entity.getCheckInTime())
                .checkOutTime(entity.getCheckOutTime())
                .totalWorkMinutes(entity.getTotalWorkMinutes())
                .totalBreakMinutes(entity.getTotalBreakMinutes())
                .overtimeMinutes(entity.getOvertimeMinutes())
                .isLateArrival(entity.isLateArrival())
                .isEarlyLeaving(entity.isEarlyLeaving())
                .isHalfDay(entity.isHalfDay())
                .isWorkFromHome(entity.isWorkFromHome())
                .isHoliday(entity.isHoliday())
                .isWeekend(entity.isWeekend())
                .remarks(entity.getRemarks())
                .locationIn(entity.getLocationIn())
                .locationOut(entity.getLocationOut())
                .ipAddress(entity.getIpAddress())
                .punches(entity.getPunches() != null ? new ArrayList<>(entity.getPunches()) : new ArrayList<>())
                .correctedByAdmin(entity.isCorrectedByAdmin())
                .correctionReason(entity.getCorrectionReason())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .build();
    }
}
