package com.techknife.backend.event;

import com.techknife.intern.entity.Intern;
import lombok.Getter;
import org.springframework.context.ApplicationEvent;

@Getter
public class InternMentorAssignedEvent extends ApplicationEvent {

    private final Intern intern;
    private final String mentorId;

    public InternMentorAssignedEvent(Object source, Intern intern, String mentorId) {
        super(source);
        this.intern = intern;
        this.mentorId = mentorId;
    }
}
