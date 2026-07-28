package com.techknife.backend.event;

import lombok.Getter;
import org.springframework.context.ApplicationEvent;

@Getter
public class EmployeeCreatedEvent extends ApplicationEvent {
    private final String employeeId;
    private final String employeeCode;
    private final String officialEmail;
    private final String fullName;

    private com.techknife.employee.entity.Employee employee;

    public EmployeeCreatedEvent(Object source, String employeeId, String employeeCode, String officialEmail, String fullName) {
        super(source);
        this.employeeId = employeeId;
        this.employeeCode = employeeCode;
        this.officialEmail = officialEmail;
        this.fullName = fullName;
    }

    public EmployeeCreatedEvent(Object source, com.techknife.employee.entity.Employee employee) {
        super(source);
        this.employee = employee;
        this.employeeId = employee != null ? employee.getId() : null;
        this.employeeCode = employee != null ? employee.getEmployeeId() : null;
        this.officialEmail = employee != null ? employee.getOfficialEmail() : null;
        this.fullName = employee != null ? ((employee.getFirstName() != null ? employee.getFirstName() : "") + " " + (employee.getLastName() != null ? employee.getLastName() : "")).trim() : null;
    }

    public com.techknife.employee.entity.Employee getEmployee() {
        return this.employee;
    }
}


