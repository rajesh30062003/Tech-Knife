package com.techknife.intern.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateAttendanceSummaryRequest {

    @NotBlank(message = "Month-Year is mandatory (e.g. 2026-07)")
    private String monthYear;

    @NotNull
    private Integer totalWorkingDays;

    @NotNull
    private Integer presentDays;

    @NotNull
    private Integer absentDays;

    @NotNull
    private Integer leaveDays;
}
