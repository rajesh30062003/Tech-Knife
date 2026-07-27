package com.techknife.employee.service.impl;

import com.techknife.employee.dto.EmployeeTimelineResponse;
import com.techknife.employee.entity.EmployeeTimeline;
import com.techknife.employee.entity.TimelineEventType;
import com.techknife.employee.repository.EmployeeTimelineRepository;
import com.techknife.employee.service.EmployeeTimelineService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class EmployeeTimelineServiceImpl implements EmployeeTimelineService {

    private final EmployeeTimelineRepository timelineRepository;

    @Override
    @Transactional(readOnly = true)
    public List<EmployeeTimelineResponse> getTimelineForEmployee(String employeeId) {
        return timelineRepository.findByEmployeeIdOrderByTimestampDesc(employeeId).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void recordTimelineEvent(String employeeId, TimelineEventType eventType, String oldValue, String newValue, String description, String changedBy) {
        EmployeeTimeline timeline = EmployeeTimeline.builder()
                .employeeId(employeeId)
                .eventType(eventType)
                .oldValue(oldValue)
                .newValue(newValue)
                .description(description)
                .changedBy(changedBy != null ? changedBy : "SYSTEM")
                .timestamp(Instant.now())
                .build();

        timelineRepository.save(timeline);
        log.info("Recorded timeline event {} for employee ID: {}", eventType, employeeId);
    }

    private EmployeeTimelineResponse mapToResponse(EmployeeTimeline timeline) {
        return EmployeeTimelineResponse.builder()
                .id(timeline.getId())
                .employeeId(timeline.getEmployeeId())
                .eventType(timeline.getEventType())
                .oldValue(timeline.getOldValue())
                .newValue(timeline.getNewValue())
                .description(timeline.getDescription())
                .changedBy(timeline.getChangedBy())
                .timestamp(timeline.getTimestamp())
                .build();
    }
}
