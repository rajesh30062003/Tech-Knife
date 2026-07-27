package com.techknife.communication.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AnnouncementReadDTO {

    private String id;
    private String announcementId;
    private String userId;
    private Instant readAt;
}
