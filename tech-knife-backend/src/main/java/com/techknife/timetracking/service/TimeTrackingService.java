package com.techknife.timetracking.service;

import com.techknife.timetracking.dto.StartTimerRequest;
import com.techknife.timetracking.dto.TimeEntryDTO;
import com.techknife.timetracking.entity.TimeEntry;
import com.techknife.timetracking.repository.TimeEntryRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class TimeTrackingService {

    private final TimeEntryRepository timeEntryRepository;

    public TimeEntryDTO startTimer(StartTimerRequest request) {
        // Stop any running timer for this employee first
        Optional<TimeEntry> running = timeEntryRepository.findByEmployeeIdAndTimerRunningTrue(request.getEmployeeId());
        running.ifPresent(entry -> stopTimer(entry.getId()));

        TimeEntry entry = TimeEntry.builder()
                .employeeId(request.getEmployeeId())
                .projectId(request.getProjectId())
                .taskId(request.getTaskId())
                .description(request.getDescription())
                .startTime(Instant.now())
                .billable(request.isBillable())
                .timerRunning(true)
                .paused(false)
                .idleTimeInMinutes(0L)
                .durationInMinutes(0L)
                .build();

        TimeEntry saved = timeEntryRepository.save(entry);
        return mapToDTO(saved);
    }

    public TimeEntryDTO pauseTimer(String entryId) {
        TimeEntry entry = timeEntryRepository.findById(entryId)
                .orElseThrow(() -> new NoSuchElementException("Time entry not found: " + entryId));

        if (!entry.isTimerRunning() || entry.isPaused()) {
            return mapToDTO(entry);
        }

        entry.setPaused(true);
        entry.setLastPauseTime(Instant.now());
        TimeEntry saved = timeEntryRepository.save(entry);
        return mapToDTO(saved);
    }

    public TimeEntryDTO resumeTimer(String entryId) {
        TimeEntry entry = timeEntryRepository.findById(entryId)
                .orElseThrow(() -> new NoSuchElementException("Time entry not found: " + entryId));

        if (!entry.isPaused()) {
            return mapToDTO(entry);
        }

        if (entry.getLastPauseTime() != null) {
            long pauseDurationInMinutes = Duration.between(entry.getLastPauseTime(), Instant.now()).toMinutes();
            entry.setIdleTimeInMinutes(entry.getIdleTimeInMinutes() + Math.max(0, pauseDurationInMinutes));
        }

        entry.setPaused(false);
        entry.setLastPauseTime(null);
        TimeEntry saved = timeEntryRepository.save(entry);
        return mapToDTO(saved);
    }

    public TimeEntryDTO stopTimer(String entryId) {
        TimeEntry entry = timeEntryRepository.findById(entryId)
                .orElseThrow(() -> new NoSuchElementException("Time entry not found: " + entryId));

        if (!entry.isTimerRunning()) {
            return mapToDTO(entry);
        }

        Instant now = Instant.now();
        entry.setEndTime(now);
        entry.setTimerRunning(false);
        entry.setPaused(false);

        long totalMinutes = Duration.between(entry.getStartTime(), now).toMinutes();
        long netMinutes = Math.max(0, totalMinutes - entry.getIdleTimeInMinutes());
        entry.setDurationInMinutes(netMinutes);

        TimeEntry saved = timeEntryRepository.save(entry);
        return mapToDTO(saved);
    }

    public TimeEntryDTO addManualTimeEntry(TimeEntryDTO dto) {
        long duration = 0;
        if (dto.getStartTime() != null && dto.getEndTime() != null) {
            duration = Math.max(0, Duration.between(dto.getStartTime(), dto.getEndTime()).toMinutes());
        } else if (dto.getDurationInMinutes() != null) {
            duration = dto.getDurationInMinutes();
        }

        TimeEntry entry = TimeEntry.builder()
                .employeeId(dto.getEmployeeId())
                .projectId(dto.getProjectId())
                .taskId(dto.getTaskId())
                .description(dto.getDescription())
                .startTime(dto.getStartTime() != null ? dto.getStartTime() : Instant.now())
                .endTime(dto.getEndTime())
                .durationInMinutes(duration)
                .billable(dto.isBillable())
                .idleTimeInMinutes(dto.getIdleTimeInMinutes() != null ? dto.getIdleTimeInMinutes() : 0L)
                .timerRunning(false)
                .paused(false)
                .build();

        TimeEntry saved = timeEntryRepository.save(entry);
        return mapToDTO(saved);
    }

    public TimeEntryDTO getActiveTimer(String employeeId) {
        return timeEntryRepository.findByEmployeeIdAndTimerRunningTrue(employeeId)
                .map(this::mapToDTO)
                .orElse(null);
    }

    public List<TimeEntryDTO> getTimeEntriesByEmployee(String employeeId) {
        return timeEntryRepository.findByEmployeeId(employeeId).stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    public List<TimeEntryDTO> getTimeEntriesByProject(String projectId) {
        return timeEntryRepository.findByProjectId(projectId).stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    public TimeEntryDTO mapToDTO(TimeEntry entry) {
        return TimeEntryDTO.builder()
                .id(entry.getId())
                .employeeId(entry.getEmployeeId())
                .projectId(entry.getProjectId())
                .taskId(entry.getTaskId())
                .description(entry.getDescription())
                .startTime(entry.getStartTime())
                .endTime(entry.getEndTime())
                .durationInMinutes(entry.getDurationInMinutes())
                .billable(entry.isBillable())
                .idleTimeInMinutes(entry.getIdleTimeInMinutes())
                .timerRunning(entry.isTimerRunning())
                .paused(entry.isPaused())
                .lastPauseTime(entry.getLastPauseTime())
                .createdAt(entry.getCreatedAt())
                .updatedAt(entry.getUpdatedAt())
                .build();
    }
}
