package com.techknife.backend.scheduler;

import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class SystemHealthScheduler {

    @Scheduled(cron = "0 0 * * * *") // Every hour
    public void logSystemHeartbeat() {
        log.info("System Health Heartbeat: Tech Knife Enterprise Backend Engine operational.");
    }
}
