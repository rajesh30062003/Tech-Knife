package com.techknife.backend.event;

import com.techknife.leave.entity.LeaveStatus;
import lombok.Getter;
import org.springframework.context.ApplicationEvent;

@Getter
public class LeaveStatusChangedEvent extends ApplicationEvent {
    private final String leaveRequestId;
    private final String employeeId;
    private final LeaveStatus status;
    private final String approverId;

    public LeaveStatusChangedEvent(Object source, String leaveRequestId, String employeeId, LeaveStatus status, String approverId) {
        super(source);
        this.leaveRequestId = leaveRequestId;
        this.employeeId = employeeId;
        this.status = status;
        this.approverId = approverId;
    }
}
