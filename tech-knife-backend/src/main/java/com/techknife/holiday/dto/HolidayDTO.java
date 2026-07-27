package com.techknife.holiday.dto;

import com.techknife.holiday.entity.HolidayType;
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
public class HolidayDTO {

    private String id;

    @NotBlank(message = "Holiday name is required")
    private String name;

    private String description;

    @NotNull(message = "Holiday date is required")
    private LocalDate date;

    @NotNull(message = "Year is required")
    private Integer year;

    @NotNull(message = "Holiday type is required")
    private HolidayType type;

    private String branchId;

    private String state;

    private Boolean restricted;

    private Boolean active;

    private Instant createdAt;

    private Instant updatedAt;
}
