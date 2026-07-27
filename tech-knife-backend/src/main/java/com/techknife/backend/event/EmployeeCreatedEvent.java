package com.techknife.backend.event;

import lombok.Getter;
import org.springframework.context.ApplicationEvent;

@Getter
public class EmployeeCreatedEvent extends ApplicationEvent {
    private final String employeeId;
    private final String employeeCode;
    private final String officialEmail;
    private final String fullName;

    public EmployeeCreatedEvent(Object source, String employeeId, String employeeCode, String officialEmail, String fullName) {
        super(source);
        this.employeeId = employeeId;
        this.employeeCode = employeeCode;
        this.officialEmail = officialEmail;
        this.fullName = fullName;
    }
}
