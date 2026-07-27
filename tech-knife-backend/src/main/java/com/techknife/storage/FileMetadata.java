package com.techknife.storage;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

/**
 * Model representing file resource metadata retrieved from Cloudinary storage.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FileMetadata {

    private String publicId;
    private String fileName;
    private String format;
    private String resourceType;
    private long size;
    private Integer width;
    private Integer height;
    private String url;
    private String secureUrl;
    private String folder;
    private Instant createdAt;
    private long version;
    private String etag;
}
