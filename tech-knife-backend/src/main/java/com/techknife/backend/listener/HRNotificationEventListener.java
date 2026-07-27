package com.techknife.backend.listener;

import com.techknife.backend.event.EmployeeCreatedEvent;
import com.techknife.backend.event.EmployeeStatusChangedEvent;
import com.techknife.backend.event.InternCertificateGeneratedEvent;
import com.techknife.backend.event.InternCreatedEvent;
import com.techknife.backend.event.InternMentorAssignedEvent;
import com.techknife.backend.mail.EmailService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class HRNotificationEventListener {

    private final EmailService emailService;

    @Async
    @EventListener
    public void handleEmployeeCreated(EmployeeCreatedEvent event) {
        log.info("New Employee Created Notification Event for ID: {}, Email: {}",
                event.getEmployee().getEmployeeId(), event.getEmployee().getOfficialEmail());
        try {
            String subject = "Welcome to Tech Knife Enterprise - Employee Onboarding";
            String body = "<h1>Welcome, " + event.getEmployee().getFirstName() + " " + event.getEmployee().getLastName() + "!</h1>"
                    + "<p>Your employee profile has been created with Employee ID: <strong>" + event.getEmployee().getEmployeeId() + "</strong>.</p>";
            emailService.sendEmail(event.getEmployee().getOfficialEmail(), subject, body);
        } catch (Exception e) {
            log.warn("Failed to send welcome email to new employee: {}", e.getMessage());
        }
    }

    @Async
    @EventListener
    public void handleEmployeeStatusChanged(EmployeeStatusChangedEvent event) {
        log.info("Employee Status Changed Event for ID: {} from {} to {}",
                event.getEmployee().getEmployeeId(), event.getOldStatus(), event.getNewStatus());
        try {
            String subject = "Tech Knife Enterprise - Employment Status Updated";
            String body = "<h2>Employment Status Update</h2>"
                    + "<p>Dear " + event.getEmployee().getFirstName() + ",</p>"
                    + "<p>Your employment status has been updated to: <strong>" + event.getNewStatus() + "</strong>.</p>";
            emailService.sendEmail(event.getEmployee().getOfficialEmail(), subject, body);
        } catch (Exception e) {
            log.warn("Failed to send status update email to employee: {}", e.getMessage());
        }
    }

    @Async
    @EventListener
    public void handleInternCreated(InternCreatedEvent event) {
        log.info("New Intern Onboarded Notification Event for Code: {}, Email: {}",
                event.getIntern().getInternCode(), event.getIntern().getOfficialEmail());
        try {
            String subject = "Welcome to Tech Knife Internship Program";
            String body = "<h1>Welcome to Tech Knife, " + event.getIntern().getFirstName() + "!</h1>"
                    + "<p>Your intern code is: <strong>" + event.getIntern().getInternCode() + "</strong>.</p>";
            emailService.sendEmail(event.getIntern().getOfficialEmail(), subject, body);
        } catch (Exception e) {
            log.warn("Failed to send welcome email to new intern: {}", e.getMessage());
        }
    }

    @Async
    @EventListener
    public void handleInternMentorAssigned(InternMentorAssignedEvent event) {
        log.info("Intern Mentor Assigned Event for Intern Code: {}, Mentor ID: {}",
                event.getIntern().getInternCode(), event.getMentorId());
    }

    @Async
    @EventListener
    public void handleInternCertificateGenerated(InternCertificateGeneratedEvent event) {
        log.info("Intern Certificate Generated Event for Intern Code: {}, Cert No: {}",
                event.getIntern().getInternCode(), event.getCertificate().getCertificateNumber());
        try {
            String subject = "Tech Knife Internship Completion Certificate";
            String body = "<h2>Congratulations, " + event.getIntern().getFirstName() + "!</h2>"
                    + "<p>Your internship completion certificate has been generated.</p>"
                    + "<p>Certificate Number: <strong>" + event.getCertificate().getCertificateNumber() + "</strong></p>"
                    + "<p>Verification Code: <strong>" + event.getCertificate().getVerificationCode() + "</strong></p>";
            if (event.getCertificate().getDownloadUrl() != null) {
                body += "<p><a href='" + event.getCertificate().getDownloadUrl() + "'>Download Certificate</a></p>";
            }
            emailService.sendEmail(event.getIntern().getOfficialEmail(), subject, body);
        } catch (Exception e) {
            log.warn("Failed to send certificate email to intern: {}", e.getMessage());
        }
    }
}
