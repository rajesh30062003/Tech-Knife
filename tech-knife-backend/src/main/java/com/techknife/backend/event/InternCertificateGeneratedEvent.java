package com.techknife.backend.event;

import com.techknife.intern.entity.Intern;
import com.techknife.intern.entity.InternCertificate;
import lombok.Getter;
import org.springframework.context.ApplicationEvent;

@Getter
public class InternCertificateGeneratedEvent extends ApplicationEvent {

    private final Intern intern;
    private final InternCertificate certificate;

    public InternCertificateGeneratedEvent(Object source, Intern intern, InternCertificate certificate) {
        super(source);
        this.intern = intern;
        this.certificate = certificate;
    }
}
