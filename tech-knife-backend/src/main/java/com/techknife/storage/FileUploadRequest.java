package com.techknife.storage;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.Map;

/**
 * DTO request object containing upload parameter configurations and file content.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FileUploadRequest {

    @NotNull(message = "File content is required for upload")
    private MultipartFile file;

    private String folder;

    private String customFileName;

    private String description;

    @Builder.Default
    private boolean overwrite = false;

    @Builder.Default
    private Map<String, String> tags = new HashMap<>();

    @Builder.Default
    private Map<String, Object> options = new HashMap<>();
}
