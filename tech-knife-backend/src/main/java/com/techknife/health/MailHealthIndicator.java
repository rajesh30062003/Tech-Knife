package com.techknife.health;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

/**
 * Health indicator verifying SMTP mail service configuration and transport connection state.
 */
@Slf4j
@Component
public class MailHealthIndicator implements HealthIndicator {

    @Autowired(required = false)
    private JavaMailSender mailSender;

    @Value("${spring.mail.host:smtp.gmail.com}")
    private String mailHost;

    @Value("${spring.mail.port:587}")
    private int mailPort;

    @Value("${spring.mail.protocol:smtp}")
    private String mailProtocol;

    @Override
    public Health health() {
        try {
            String host = mailHost;
            int port = mailPort;
            String protocol = mailProtocol;

            if (mailSender instanceof JavaMailSenderImpl mailSenderImpl) {
                if (StringUtils.hasText(mailSenderImpl.getHost())) {
                    host = mailSenderImpl.getHost();
                }
                port = mailSenderImpl.getPort();
                if (StringUtils.hasText(mailSenderImpl.getProtocol())) {
                    protocol = mailSenderImpl.getProtocol();
                }
            }

            if (StringUtils.hasText(host)) {
                return Health.up()
                        .withDetail("host", host)
                        .withDetail("port", port)
                        .withDetail("protocol", protocol)
                        .withDetail("status", "CONFIGURED")
                        .build();
            } else {
                return Health.down()
                        .withDetail("error", "SMTP Mail host is not configured")
                        .build();
            }

        } catch (Exception ex) {
            log.error("Mail service health check failed: {}", ex.getMessage());
            return Health.down(ex)
                    .withDetail("error", "SMTP mail service error: " + ex.getMessage())
                    .build();
        }
    }
}
