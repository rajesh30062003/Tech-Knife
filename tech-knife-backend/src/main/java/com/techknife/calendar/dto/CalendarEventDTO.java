package com.techknife.calendar.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CalendarEventDTO {

    private String id;
    private String employeeId;
    private String employeeName;
    private EventType type; // ATTENDANCE, LEAVE, HOLIDAY, WFH, SHIFT_SCHEDULE, EMPLOYEE_EVENT
    private String title;
    private String description;
    private LocalDate startDate;
    private LocalDate endDate;
    private String status; // PRESENT, ABSENT, APPROVED, PENDING, HOLIDAY_OPTIONAL, etc.
    private String color;
    private Object metadata;

    public enum EventType {
        ATTENDANCE,
        LEAVE,
        HOLIDAY,
        WFH,
        SHIFT_SCHEDULE,
        EMPLOYEE_EVENT
    }
}
