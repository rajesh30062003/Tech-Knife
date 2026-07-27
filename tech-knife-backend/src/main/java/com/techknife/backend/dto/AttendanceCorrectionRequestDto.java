package com.techknife.backend.dto;

import com.techknife.backend.constant.AttendanceStatus;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AttendanceCorrectionRequestDto {

    private Instant checkInTime;
    private Instant checkOutTime;
    private AttendanceStatus status;
    private long totalBreakMinutes;
    private String remarks;

    @NotBlank(message = "Correction reason is mandatory for administrative audit trail")
    private String reason;
}
