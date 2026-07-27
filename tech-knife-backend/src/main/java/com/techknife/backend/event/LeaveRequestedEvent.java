package com.techknife.backend.event;

import lombok.Getter;
import org.springframework.context.ApplicationEvent;

@Getter
public class LeaveRequestedEvent extends ApplicationEvent {
    private final String leaveRequestId;
    private final String employeeId;
    private final String leaveTypeId;
    private final Double totalDays;

    public LeaveRequestedEvent(Object source, String leaveRequestId, String employeeId, String leaveTypeId, Double totalDays) {
        super(source);
        this.leaveRequestId = leaveRequestId;
        this.employeeId = employeeId;
        this.leaveTypeId = leaveTypeId;
        this.totalDays = totalDays;
    }
}
