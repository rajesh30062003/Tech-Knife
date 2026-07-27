package com.techknife.mail;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Data transfer model representing a complete email message configuration.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EmailMessage {

    private String from;
    private String fromName;

    @Builder.Default
    private List<String> to = new ArrayList<>();

    @Builder.Default
    private List<String> cc = new ArrayList<>();

    @Builder.Default
    private List<String> bcc = new ArrayList<>();

    private String replyTo;
    private String subject;
    private String content;

    @Builder.Default
    private boolean html = true;

    private String templateName;

    @Builder.Default
    private Map<String, Object> templateModel = new HashMap<>();

    @Builder.Default
    private List<EmailAttachment> attachments = new ArrayList<>();

    @Builder.Default
    private List<EmailInlineImage> inlineImages = new ArrayList<>();

    /**
     * Model representing an email file attachment.
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class EmailAttachment {
        private String name;
        private byte[] data;
        private String contentType;
        private InputStream inputStream;
    }

    /**
     * Model representing an email inline embedded image.
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class EmailInlineImage {
        private String contentId;
        private byte[] data;
        private String contentType;
    }
}
