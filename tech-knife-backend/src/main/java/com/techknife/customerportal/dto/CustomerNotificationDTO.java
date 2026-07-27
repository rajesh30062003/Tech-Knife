package com.techknife.customerportal.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CustomerNotificationDTO {

    private String id;
    private String customerAccountId;
    private String title;
    private String message;
    private String type;
    private Boolean isRead;
    private String linkUrl;
    private Instant createdAt;
}
