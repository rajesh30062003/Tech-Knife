package com.techknife.attendance.dto;

import com.techknife.attendance.entity.RegularizationStatus;
import com.techknife.attendance.entity.RegularizationType;
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
public class AttendanceRegularizationDTO {

    private String id;
    private String employeeId;
    private String employeeName;
    private String departmentId;
    private String attendanceRecordId;
    private LocalDate date;
    private RegularizationType type;
    private Instant requestedCheckIn;
    private Instant requestedCheckOut;
    private String reason;
    private RegularizationStatus status;
    private String approverId;
    private String approverName;
    private String approverComments;
    private Instant approvedOrRejectedAt;
    private Instant createdAt;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Request {
        @NotBlank(message = "Employee ID is required")
        private String employeeId;

        @NotNull(message = "Date is required")
        private LocalDate date;

        @NotNull(message = "Regularization type is required")
        private RegularizationType type;

        private Instant requestedCheckIn;
        private Instant requestedCheckOut;

        @NotBlank(message = "Reason is required")
        private String reason;
    }
}
