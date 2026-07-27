package com.techknife.backend.listener;

import com.techknife.backend.event.UserRegisteredEvent;
import com.techknife.backend.mail.EmailService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class UserEventListener {

    private final EmailService emailService;

    @Async
    @EventListener
    public void handleUserRegisteredEvent(UserRegisteredEvent event) {
        log.info("User registered event triggered for user: {}", event.getUser().getEmail());
        String subject = "Welcome to Tech Knife Enterprise Platform";
        String body = "<h1>Welcome, " + event.getUser().getFirstName() + "!</h1><p>Your account has been initialized.</p>";
        emailService.sendEmail(event.getUser().getEmail(), subject, body);
    }
}
