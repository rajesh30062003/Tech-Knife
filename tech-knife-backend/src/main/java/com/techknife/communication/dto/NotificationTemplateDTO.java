package com.techknife.communication.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NotificationTemplateDTO {

    private String id;

    @NotBlank(message = "Template code is required")
    private String templateCode;

    @NotBlank(message = "Template name is required")
    private String name;

    private String subject;

    @NotBlank(message = "Body template is required")
    private String bodyTemplate;

    private String channelType;
    private boolean active;
    private List<String> variables;
    private Instant createdAt;
    private Instant updatedAt;
    private String createdBy;
}
