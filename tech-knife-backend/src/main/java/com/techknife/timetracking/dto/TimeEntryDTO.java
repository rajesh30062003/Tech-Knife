package com.techknife.timetracking.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TimeEntryDTO {

    private String id;
    private String employeeId;
    private String projectId;
    private String taskId;
    private String description;
    private Instant startTime;
    private Instant endTime;
    private Long durationInMinutes;
    private boolean billable;
    private Long idleTimeInMinutes;
    private boolean timerRunning;
    private boolean paused;
    private Instant lastPauseTime;
    private Instant createdAt;
    private Instant updatedAt;
}
