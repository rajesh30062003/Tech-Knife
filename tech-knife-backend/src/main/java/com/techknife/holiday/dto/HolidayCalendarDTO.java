package com.techknife.holiday.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class HolidayCalendarDTO {

    private String id;

    @NotBlank(message = "Calendar name is required")
    private String name;

    @NotNull(message = "Year is required")
    private Integer year;

    private String branchId;

    private String branchName;

    private List<String> holidayIds;

    private Integer maxRestrictedHolidaysAllowed;

    private Boolean active;

    private Instant createdAt;

    private Instant updatedAt;
}
