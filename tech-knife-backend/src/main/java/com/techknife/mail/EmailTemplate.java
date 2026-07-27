package com.techknife.mail;

import lombok.Getter;

/**
 * Enum defining available email template definitions and file paths.
 */
@Getter
public enum EmailTemplate {

    BASE("email/base-template", "Base Enterprise System Layout"),
    SYSTEM_NOTICE("email/system-notice", "System Administrative Notice"),
    GENERAL_NOTIFICATION("email/general-notification", "General Application Notification");

    private final String templatePath;
    private final String description;

    EmailTemplate(String templatePath, String description) {
        this.templatePath = templatePath;
        this.description = description;
    }
}
