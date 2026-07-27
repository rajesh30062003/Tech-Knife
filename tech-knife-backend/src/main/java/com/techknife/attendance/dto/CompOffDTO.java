package com.techknife.attendance.dto;

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
public class CompOffDTO {

    private String id;
    private String employeeId;
    private String employeeName;
    private LocalDate workedDate;
    private Double daysGranted;
    private String reason;
    private String status;
    private LocalDate expiryDate;
    private String approverId;
    private String approverComments;
    private Instant createdAt;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Request {
        @NotBlank(message = "Employee ID is required")
        private String employeeId;

        @NotNull(message = "Worked date is required")
        private LocalDate workedDate;

        private Double daysGranted;

        @NotBlank(message = "Reason is required")
        private String reason;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Balance {
        private String employeeId;
        private Double availableDays;
        private Double usedDays;
        private Double expiredDays;
    }
}
