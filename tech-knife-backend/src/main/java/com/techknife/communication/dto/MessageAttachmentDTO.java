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
public class MessageAttachmentDTO {

    private String id;
    private String driveFileId;
    private String fileName;
    private String mimeType;
    private Long fileSize;
    private String previewUrl;
    private String downloadUrl;
    private String thumbnailUrl;
    private String uploadedBy;
    private Instant uploadedAt;

    // Backward-compatibility getters
    public String getFileUrl() {
        return downloadUrl != null ? downloadUrl : (previewUrl != null ? previewUrl : "");
    }

    public String getFileType() {
        return mimeType != null ? mimeType : "application/octet-stream";
    }
}
