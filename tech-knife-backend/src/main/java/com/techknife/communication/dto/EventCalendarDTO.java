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
public class EventCalendarDTO {

    private String id;

    @NotBlank(message = "Calendar name is required")
    private String name;

    private String description;
    private String colorCode;
    private String ownerId;
    private boolean isPublic;
    private List<String> sharedWithUserIds;
    private Instant createdAt;
    private Instant updatedAt;
    private String createdBy;
}
