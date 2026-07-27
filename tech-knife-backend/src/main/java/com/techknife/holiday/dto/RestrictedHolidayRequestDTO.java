package com.techknife.holiday.dto;

import com.techknife.leave.entity.LeaveStatus;
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
public class RestrictedHolidayRequestDTO {

    private String id;

    @NotBlank(message = "Employee ID is required")
    private String employeeId;

    private String employeeName;

    @NotBlank(message = "Holiday ID is required")
    private String holidayId;

    private String holidayName;

    private String holidayDate;

    private Integer year;

    private String reason;

    private LeaveStatus status;

    private String approverId;

    private String approverName;

    private String comments;

    private Instant actionedAt;

    private Instant createdAt;

    private Instant updatedAt;
}
