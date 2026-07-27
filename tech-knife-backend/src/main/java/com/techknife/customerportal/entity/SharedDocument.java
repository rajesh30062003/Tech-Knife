package com.techknife.customerportal.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "shared_documents")
public class SharedDocument {

    @Id
    private String id;

    @Indexed
    private String customerAccountId;

    @Indexed
    private String projectId;

    private String documentName;

    private String description;

    private String fileUrl;

    private String cloudinaryPublicId;

    private String fileType;

    private Long fileSize;

    @Indexed
    private String category; // CONTRACT, INVOICE, REPORT, PROJECT_DOC, DESIGN, SOURCE_DELIVERABLE

    private String uploadedBy;

    @CreatedDate
    private Instant uploadedAt;
}
