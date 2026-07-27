package com.techknife.timetracking.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "time_entries")
public class TimeEntry {

    @Id
    private String id;

    @Indexed
    private String employeeId;

    @Indexed
    private String projectId;

    @Indexed
    private String taskId;

    private String description;

    private Instant startTime;

    private Instant endTime;

    private Long durationInMinutes;

    @Builder.Default
    private boolean billable = true;

    @Builder.Default
    private Long idleTimeInMinutes = 0L;

    @Builder.Default
    private boolean timerRunning = false;

    @Builder.Default
    private boolean paused = false;

    private Instant lastPauseTime;

    @CreatedDate
    private Instant createdAt;

    @LastModifiedDate
    private Instant updatedAt;
}
