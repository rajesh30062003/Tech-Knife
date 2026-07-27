package com.techknife.backend.dto;

import com.techknife.backend.constant.AttendanceStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ManualAttendanceRequestDto {

    @NotBlank(message = "User ID is required")
    private String userId;

    private String userEmail;
    private String userName;
    private String department;

    @NotNull(message = "Date is required")
    private LocalDate date;

    @NotNull(message = "Attendance status is required")
    private AttendanceStatus status;

    private Instant checkInTime;
    private Instant checkOutTime;
    private long totalBreakMinutes;
    private boolean isWfh;
    private String remarks;
}
