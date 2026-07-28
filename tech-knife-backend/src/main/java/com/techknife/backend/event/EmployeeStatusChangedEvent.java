package com.techknife.backend.event;

import lombok.Getter;
import org.springframework.context.ApplicationEvent;

@Getter
public class EmployeeStatusChangedEvent extends ApplicationEvent {
    private final String employeeId;
    private final String oldStatus;
    private final String newStatus;

    private com.techknife.employee.entity.Employee employee;

    public EmployeeStatusChangedEvent(Object source, String employeeId, String oldStatus, String newStatus) {
        super(source);
        this.employeeId = employeeId;
        this.oldStatus = oldStatus;
        this.newStatus = newStatus;
    }

    public EmployeeStatusChangedEvent(Object source, com.techknife.employee.entity.Employee employee, com.techknife.employee.entity.EmployeeStatus oldStatus, com.techknife.employee.entity.EmployeeStatus newStatus) {
        super(source);
        this.employee = employee;
        this.employeeId = employee != null ? employee.getId() : null;
        this.oldStatus = oldStatus != null ? oldStatus.name() : null;
        this.newStatus = newStatus != null ? newStatus.name() : null;
    }

    public com.techknife.employee.entity.Employee getEmployee() {
        return this.employee;
    }
}


