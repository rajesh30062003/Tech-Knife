package com.techknife.employee.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EmployeeDocument {
    private String id;
    private DocumentType documentType;
    private String documentName;
    private String documentUrl;
    private String publicId;
    private Long fileSize;
    private Instant uploadedAt;
    private String uploadedBy;
}
