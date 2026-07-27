package com.techknife.mail;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.InputStreamResource;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.Map;
import java.util.regex.Pattern;

/**
 * Production implementation of {@link EmailService} managing SMTP transmission, validation, multi-part handling, and template compilation.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class EmailServiceImpl implements EmailService {

    private static final Pattern EMAIL_PATTERN = Pattern.compile("^[A-Za-z0-9+_.-]+@(.+)$");

    private final JavaMailSender javaMailSender;
    private final EmailTemplateService emailTemplateService;

    @Value("${app.mail.from:noreply@techknife.com}")
    private String defaultFromEmail;

    @Value("${app.mail.from-name:Tech Knife Enterprise}")
    private String defaultFromName;

    @Override
    public void sendSimpleEmail(String to, String subject, String content) {
        EmailMessage message = EmailMessage.builder()
                .to(Collections.singletonList(to))
                .subject(subject)
                .content(content)
                .html(false)
                .build();
        sendEmail(message);
    }

    @Override
    public void sendHtmlEmail(String to, String subject, String htmlContent) {
        EmailMessage message = EmailMessage.builder()
                .to(Collections.singletonList(to))
                .subject(subject)
                .content(htmlContent)
                .html(true)
                .build();
        sendEmail(message);
    }

    @Override
    public void sendTemplateEmail(String to, String subject, String templateName, Map<String, Object> templateModel) {
        EmailMessage message = EmailMessage.builder()
                .to(Collections.singletonList(to))
                .subject(subject)
                .templateName(templateName)
                .templateModel(templateModel)
                .html(true)
                .build();
        sendEmail(message);
    }

    @Override
    public void sendEmailWithAttachment(String to, String subject, String content, String attachmentName, byte[] attachmentData, String contentType) {
        EmailMessage.EmailAttachment attachment = EmailMessage.EmailAttachment.builder()
                .name(attachmentName)
                .data(attachmentData)
                .contentType(contentType)
                .build();

        EmailMessage message = EmailMessage.builder()
                .to(Collections.singletonList(to))
                .subject(subject)
                .content(content)
                .html(true)
                .attachments(Collections.singletonList(attachment))
                .build();
        sendEmail(message);
    }

    @Override
    public void sendEmailWithInlineImage(String to, String subject, String htmlContent, String contentId, byte[] imageData, String contentType) {
        EmailMessage.EmailInlineImage inlineImage = EmailMessage.EmailInlineImage.builder()
                .contentId(contentId)
                .data(imageData)
                .contentType(contentType)
                .build();

        EmailMessage message = EmailMessage.builder()
                .to(Collections.singletonList(to))
                .subject(subject)
                .content(htmlContent)
                .html(true)
                .inlineImages(Collections.singletonList(inlineImage))
                .build();
        sendEmail(message);
    }

    @Override
    public void sendEmail(EmailMessage emailMessage) {
        validateEmailMessage(emailMessage);

        try {
            // Render template if templateName provided
            if (StringUtils.hasText(emailMessage.getTemplateName())) {
                log.debug("Rendering template '{}' for email subject '{}'", emailMessage.getTemplateName(), emailMessage.getSubject());
                String renderedBody = emailTemplateService.renderTemplate(
                        emailMessage.getTemplateName(),
                        emailMessage.getTemplateModel()
                );
                emailMessage.setContent(renderedBody);
                emailMessage.setHtml(true);
            }

            if (!StringUtils.hasText(emailMessage.getContent())) {
                throw new EmailException("INVALID_EMAIL_CONTENT", "Email body content is empty");
            }

            // Determine if multipart message is needed
            boolean isMultipart = emailMessage.isHtml() ||
                    !emailMessage.getAttachments().isEmpty() ||
                    !emailMessage.getInlineImages().isEmpty();

            MimeMessage mimeMessage = javaMailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, isMultipart, StandardCharsets.UTF_8.name());

            // Set From address
            String fromEmail = StringUtils.hasText(emailMessage.getFrom()) ? emailMessage.getFrom() : defaultFromEmail;
            String fromName = StringUtils.hasText(emailMessage.getFromName()) ? emailMessage.getFromName() : defaultFromName;
            try {
                helper.setFrom(new InternetAddress(fromEmail, fromName));
            } catch (UnsupportedEncodingException e) {
                helper.setFrom(fromEmail);
            }

            // Set Recipients
            helper.setTo(emailMessage.getTo().toArray(new String[0]));

            if (emailMessage.getCc() != null && !emailMessage.getCc().isEmpty()) {
                helper.setCc(emailMessage.getCc().toArray(new String[0]));
            }

            if (emailMessage.getBcc() != null && !emailMessage.getBcc().isEmpty()) {
                helper.setBcc(emailMessage.getBcc().toArray(new String[0]));
            }

            if (StringUtils.hasText(emailMessage.getReplyTo())) {
                helper.setReplyTo(emailMessage.getReplyTo());
            }

            // Set Subject and Body
            helper.setSubject(emailMessage.getSubject());
            helper.setText(emailMessage.getContent(), emailMessage.isHtml());

            // Add Attachments
            for (EmailMessage.EmailAttachment attachment : emailMessage.getAttachments()) {
                if (!StringUtils.hasText(attachment.getName())) {
                    throw new EmailException("INVALID_ATTACHMENT", "Attachment name must not be blank");
                }
                if (attachment.getData() != null && attachment.getData().length > 0) {
                    ByteArrayResource resource = new ByteArrayResource(attachment.getData());
                    String contentType = StringUtils.hasText(attachment.getContentType()) ? attachment.getContentType() : "application/octet-stream";
                    helper.addAttachment(attachment.getName(), resource, contentType);
                } else if (attachment.getInputStream() != null) {
                    InputStreamResource resource = new InputStreamResource(attachment.getInputStream());
                    String contentType = StringUtils.hasText(attachment.getContentType()) ? attachment.getContentType() : "application/octet-stream";
                    helper.addAttachment(attachment.getName(), resource, contentType);
                } else {
                    throw new EmailException("INVALID_ATTACHMENT", "Attachment data is missing for file: " + attachment.getName());
                }
            }

            // Add Inline Images
            for (EmailMessage.EmailInlineImage inlineImage : emailMessage.getInlineImages()) {
                if (!StringUtils.hasText(inlineImage.getContentId())) {
                    throw new EmailException("INVALID_INLINE_IMAGE", "Inline image contentId must not be blank");
                }
                if (inlineImage.getData() != null && inlineImage.getData().length > 0) {
                    ByteArrayResource resource = new ByteArrayResource(inlineImage.getData());
                    String contentType = StringUtils.hasText(inlineImage.getContentType()) ? inlineImage.getContentType() : "image/png";
                    helper.addInline(inlineImage.getContentId(), resource, contentType);
                } else {
                    throw new EmailException("INVALID_INLINE_IMAGE", "Inline image data is missing for contentId: " + inlineImage.getContentId());
                }
            }

            log.info("Sending email to '{}' with subject '{}'", emailMessage.getTo(), emailMessage.getSubject());
            javaMailSender.send(mimeMessage);
            log.info("Email successfully sent to '{}' with subject '{}'", emailMessage.getTo(), emailMessage.getSubject());

        } catch (MailException ex) {
            log.error("SMTP connection or transmission failure while sending email to '{}': {}", emailMessage.getTo(), ex.getMessage(), ex);
            throw new EmailException("SMTP_TRANSMISSION_FAILED", "Failed to send email due to SMTP connection error: " + ex.getMessage(), ex);
        } catch (MessagingException ex) {
            log.error("MIME message creation/formatting error for email to '{}': {}", emailMessage.getTo(), ex.getMessage(), ex);
            throw new EmailException("MIME_CREATION_FAILED", "Failed to construct MIME email message: " + ex.getMessage(), ex);
        } catch (EmailException ex) {
            throw ex;
        } catch (Exception ex) {
            log.error("Unexpected failure while sending email to '{}': {}", emailMessage.getTo(), ex.getMessage(), ex);
            throw new EmailException("EMAIL_SEND_ERROR", "Unexpected error occurred during email transmission: " + ex.getMessage(), ex);
        }
    }

    private void validateEmailMessage(EmailMessage message) {
        if (message == null) {
            throw new EmailException("INVALID_EMAIL_MESSAGE", "EmailMessage payload must not be null");
        }
        if (message.getTo() == null || message.getTo().isEmpty()) {
            throw new EmailException("INVALID_RECIPIENT", "At least one recipient ('to') email address is required");
        }
        for (String recipient : message.getTo()) {
            if (!StringUtils.hasText(recipient) || !EMAIL_PATTERN.matcher(recipient).matches()) {
                throw new EmailException("INVALID_RECIPIENT_FORMAT", "Invalid recipient email address format: " + recipient);
            }
        }
        if (!StringUtils.hasText(message.getSubject())) {
            throw new EmailException("INVALID_SUBJECT", "Email subject must not be empty or blank");
        }
    }
}
