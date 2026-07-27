package com.techknife.mail;

import java.util.Map;

/**
 * Interface defining contract for sending SMTP email notifications.
 */
public interface EmailService {

    /**
     * Sends a plain-text email to a single recipient.
     */
    void sendSimpleEmail(String to, String subject, String content);

    /**
     * Sends an HTML email to a single recipient.
     */
    void sendHtmlEmail(String to, String subject, String htmlContent);

    /**
     * Sends a templated HTML email rendered using Thymeleaf.
     */
    void sendTemplateEmail(String to, String subject, String templateName, Map<String, Object> templateModel);

    /**
     * Sends an email with an attached document/file.
     */
    void sendEmailWithAttachment(String to, String subject, String content, String attachmentName, byte[] attachmentData, String contentType);

    /**
     * Sends an HTML email containing an inline embedded image.
     */
    void sendEmailWithInlineImage(String to, String subject, String htmlContent, String contentId, byte[] imageData, String contentType);

    /**
     * Sends a fully configured EmailMessage object supporting multiple recipients, CC, BCC, replyTo, attachments, and inline images.
     */
    void sendEmail(EmailMessage emailMessage);
}
