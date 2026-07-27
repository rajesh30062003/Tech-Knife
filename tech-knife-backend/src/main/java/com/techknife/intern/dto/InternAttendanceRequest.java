package com.techknife.intern.dto;

import jakarta.validation.constraints.Min;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class InternAttendanceRequest {

    @Min(0)
    private int totalWorkingDays;

    @Min(0)
    private int daysPresent;

    @Min(0)
    private int daysAbsent;

    @Min(0)
    private int paidLeaves;
}
