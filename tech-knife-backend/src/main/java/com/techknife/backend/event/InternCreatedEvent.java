package com.techknife.backend.event;

import lombok.Getter;
import org.springframework.context.ApplicationEvent;

@Getter
public class InternCreatedEvent extends ApplicationEvent {
    private final String internId;
    private final String internCode;
    private final String officialEmail;
    private final String fullName;

    public InternCreatedEvent(Object source, String internId, String internCode, String officialEmail, String fullName) {
        super(source);
        this.internId = internId;
        this.internCode = internCode;
        this.officialEmail = officialEmail;
        this.fullName = fullName;
    }
}
