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
public class SharedDocumentDTO {

    private String id;
    private String customerAccountId;
    private String projectId;
    private String documentName;
    private String description;
    private String fileUrl;
    private String cloudinaryPublicId;
    private String fileType;
    private Long fileSize;
    private String category;
    private String uploadedBy;
    private Instant uploadedAt;
}
