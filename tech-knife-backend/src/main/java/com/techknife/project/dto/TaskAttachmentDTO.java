package com.techknife.project.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TaskAttachmentDTO {

    private String id;
    private String taskId;
    private String projectId;
    private String fileName;
    private String fileUrl;
    private String fileType;
    private Long fileSize;
    private String uploadedBy;
    private String uploadedByName;
    private Instant createdAt;
}
