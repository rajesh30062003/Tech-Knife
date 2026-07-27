package com.techknife.backend.event;

import lombok.Getter;
import org.springframework.context.ApplicationEvent;

@Getter
public class MentorAssignedEvent extends ApplicationEvent {
    private final String internId;
    private final String mentorId;
    private final String assignedBy;

    public MentorAssignedEvent(Object source, String internId, String mentorId, String assignedBy) {
        super(source);
        this.internId = internId;
        this.mentorId = mentorId;
        this.assignedBy = assignedBy;
    }
}
