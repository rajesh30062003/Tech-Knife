package com.techknife.storage;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "drive_file_records")
public class DriveFileRecord {

    @Id
    private String id;
    private String fileId;
    private String name;
    private String originalFileName;
    private String projectCode;
    private String category;
    private String uploadedBy;
    private String uploadedByEmail;
    private Instant uploadedAt;
    private long fileSize;
    private String mimeType;
    private String format;
    private String webViewLink;
    private String webContentLink;
    private String secureUrl;
    private String driveFolderId;
    private byte[] fileData;
}
