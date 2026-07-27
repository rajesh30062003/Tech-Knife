package com.techknife.employee.service;

import com.techknife.employee.dto.EmployeeTimelineResponse;
import com.techknife.employee.entity.TimelineEventType;

import java.util.List;

public interface EmployeeTimelineService {

    List<EmployeeTimelineResponse> getTimelineForEmployee(String employeeId);

    void recordTimelineEvent(String employeeId, TimelineEventType eventType, String oldValue, String newValue, String description, String changedBy);
}
