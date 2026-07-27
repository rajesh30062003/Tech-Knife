package com.techknife.communication.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MessageAttachment {

    private String fileName;
    private String fileUrl;
    private String fileType;
    private Long fileSize;
    private Instant uploadedAt;
}
