package com.techknife.backend.listener;

import com.techknife.backend.event.CertificateGeneratedEvent;
import com.techknife.backend.event.EmployeeCreatedEvent;
import com.techknife.backend.event.EmployeeStatusChangedEvent;
import com.techknife.backend.event.InternCreatedEvent;
import com.techknife.backend.event.LeaveRequestedEvent;
import com.techknife.backend.event.LeaveStatusChangedEvent;
import com.techknife.backend.event.MentorAssignedEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

/**
 * Event Listener handling async HR & Intern events across the enterprise platform.
 */
@Slf4j
@Component
public class HREventListener {

    @Async
    @EventListener
    public void handleEmployeeCreated(EmployeeCreatedEvent event) {
        log.info("[NOTIFICATION] New Employee Onboarded: ID={}, Code={}, Name={}, Email={}",
                event.getEmployeeId(), event.getEmployeeCode(), event.getFullName(), event.getOfficialEmail());
    }

    @Async
    @EventListener
    public void handleInternCreated(InternCreatedEvent event) {
        log.info("[NOTIFICATION] New Intern Onboarded: ID={}, Code={}, Name={}, Email={}",
                event.getInternId(), event.getInternCode(), event.getFullName(), event.getOfficialEmail());
    }

    @Async
    @EventListener
    public void handleMentorAssigned(MentorAssignedEvent event) {
        log.info("[NOTIFICATION] Mentor Assigned: InternID={}, MentorID={}, AssignedBy={}",
                event.getInternId(), event.getMentorId(), event.getAssignedBy());
    }

    @Async
    @EventListener
    public void handleCertificateGenerated(CertificateGeneratedEvent event) {
        log.info("[NOTIFICATION] Certificate Generated: InternID={}, CertNo={}, VerificationCode={}",
                event.getInternId(), event.getCertificateNumber(), event.getVerificationCode());
    }

    @Async
    @EventListener
    public void handleEmployeeStatusChanged(EmployeeStatusChangedEvent event) {
        log.info("[NOTIFICATION] Employee Status Changed: EmployeeID={}, OldStatus={}, NewStatus={}",
                event.getEmployeeId(), event.getOldStatus(), event.getNewStatus());
    }

    @Async
    @EventListener
    public void handleLeaveRequested(LeaveRequestedEvent event) {
        log.info("[NOTIFICATION] Leave Requested: LeaveRequestID={}, EmployeeID={}, LeaveTypeID={}, TotalDays={}",
                event.getLeaveRequestId(), event.getEmployeeId(), event.getLeaveTypeId(), event.getTotalDays());
    }

    @Async
    @EventListener
    public void handleLeaveStatusChanged(LeaveStatusChangedEvent event) {
        log.info("[NOTIFICATION] Leave Status Updated: LeaveRequestID={}, EmployeeID={}, Status={}, ActionedBy={}",
                event.getLeaveRequestId(), event.getEmployeeId(), event.getStatus(), event.getApproverId());
    }
}
