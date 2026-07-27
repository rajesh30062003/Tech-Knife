package com.techknife.backend.event;

import lombok.Getter;
import org.springframework.context.ApplicationEvent;

@Getter
public class CertificateGeneratedEvent extends ApplicationEvent {
    private final String internId;
    private final String certificateNumber;
    private final String verificationCode;

    public CertificateGeneratedEvent(Object source, String internId, String certificateNumber, String verificationCode) {
        super(source);
        this.internId = internId;
        this.certificateNumber = certificateNumber;
        this.verificationCode = verificationCode;
    }
}
