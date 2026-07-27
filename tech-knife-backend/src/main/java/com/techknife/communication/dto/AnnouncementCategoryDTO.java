package com.techknife.communication.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AnnouncementCategoryDTO {

    private String id;

    @NotBlank(message = "Category name is required")
    private String name;

    private String description;
    private String colorCode;
    private boolean active;
    private Instant createdAt;
    private Instant updatedAt;
    private String createdBy;
}
