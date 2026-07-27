package com.techknife.project.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TaskCommentDTO {

    private String id;

    @NotBlank(message = "Task ID is required")
    private String taskId;

    private String authorId;

    private String authorName;

    @NotBlank(message = "Content is required")
    private String content;

    @Builder.Default
    private List<String> mentions = new ArrayList<>();

    private String parentCommentId;

    private Instant createdAt;

    private Instant updatedAt;
}
