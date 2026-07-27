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
public class AnnouncementDTO {

    private String id;

    @NotBlank(message = "Title is required")
    private String title;

    @NotBlank(message = "Content is required")
    private String content;

    private String categoryId;
    private String categoryName;
    private String status;
    private String priority;
    private String targetType;
    private List<String> targetValues;
    private String authorId;
    private String authorName;
    private Instant publishedAt;
    private Instant expiresAt;
    private long readCount;
    private boolean isReadByCurrentUser;
    private Instant createdAt;
    private Instant updatedAt;
    private String createdBy;
}
