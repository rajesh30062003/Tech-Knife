package com.techknife.storage;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

/**
 * DTO response object containing Cloudinary file upload results and metadata.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FileUploadResponse {

    private String publicId;
    private String url;
    private String secureUrl;
    private String fileName;
    private String originalFileName;
    private String format;
    private String resourceType;
    private long size;
    private Integer width;
    private Integer height;
    private String folder;
    private Instant createdAt;
    private String etag;
    private String description;
}
