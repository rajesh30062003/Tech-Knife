package com.techknife.backend.event;

import lombok.Getter;
import org.springframework.context.ApplicationEvent;

@Getter
public class EmployeeStatusChangedEvent extends ApplicationEvent {
    private final String employeeId;
    private final String oldStatus;
    private final String newStatus;

    public EmployeeStatusChangedEvent(Object source, String employeeId, String oldStatus, String newStatus) {
        super(source);
        this.employeeId = employeeId;
        this.oldStatus = oldStatus;
        this.newStatus = newStatus;
    }
}
