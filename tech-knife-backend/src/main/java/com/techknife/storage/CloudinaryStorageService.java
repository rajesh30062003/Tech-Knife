package com.techknife.storage;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.Instant;
import java.time.format.DateTimeParseException;
import java.util.HashMap;
import java.util.Map;

/**
 * Production Cloudinary storage implementation providing file upload, deletion, replacement, and URL generation.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class CloudinaryStorageService implements FileStorageService {

    private final Cloudinary cloudinary;

    @Override
    public FileUploadResponse uploadFile(FileUploadRequest request) {
        if (request == null || request.getFile() == null) {
            throw new FileStorageException("INVALID_REQUEST", "Upload request or file payload is missing");
        }

        MultipartFile file = request.getFile();
        FileValidationUtil.validateFile(file);

        String originalFilename = file.getOriginalFilename();
        String extension = FileValidationUtil.getFileExtension(originalFilename);
        String targetFolder = FileValidationUtil.validateAndNormalizeFolder(request.getFolder());

        String resourceType = FileValidationUtil.determineResourceType(extension);

        String publicId;
        if (StringUtils.hasText(request.getCustomFileName())) {
            String cleanCustomName = FileValidationUtil.sanitizeFilename(request.getCustomFileName());
            publicId = targetFolder + "/" + cleanCustomName;
        } else {
            publicId = FileValidationUtil.generateUniquePublicId(originalFilename, targetFolder);
        }

        log.info("Initiating Cloudinary upload for file '{}' to publicId '{}' (resourceType: '{}')",
                originalFilename, publicId, resourceType);

        try {
            Map<String, Object> uploadParams = new HashMap<>();
            uploadParams.put("public_id", publicId);
            uploadParams.put("resource_type", resourceType);
            uploadParams.put("overwrite", request.isOverwrite());
            uploadParams.put("unique_filename", false);
            uploadParams.put("use_filename", true);

            if (request.getTags() != null && !request.getTags().isEmpty()) {
                uploadParams.put("tags", String.join(",", request.getTags().keySet()));
            }

            if (StringUtils.hasText(request.getDescription())) {
                uploadParams.put("context", "description=" + request.getDescription());
            }

            if (request.getOptions() != null && !request.getOptions().isEmpty()) {
                uploadParams.putAll(request.getOptions());
            }

            Map<?, ?> uploadResult = cloudinary.uploader().upload(file.getBytes(), uploadParams);

            log.info("Cloudinary upload successful for publicId '{}'", publicId);
            return mapToUploadResponse(uploadResult, originalFilename, targetFolder, request.getDescription());

        } catch (IOException ex) {
            log.error("Cloudinary I/O upload failure for file '{}' (publicId: '{}'): {}",
                    originalFilename, publicId, ex.getMessage(), ex);
            throw new FileStorageException("STORAGE_UPLOAD_FAILED",
                    "Failed to upload file to Cloudinary storage: " + ex.getMessage(), publicId, originalFilename, ex);
        } catch (Exception ex) {
            log.error("Cloudinary upload error for file '{}' (publicId: '{}'): {}",
                    originalFilename, publicId, ex.getMessage(), ex);
            throw new FileStorageException("STORAGE_API_ERROR",
                    "Cloudinary API error during file upload: " + ex.getMessage(), publicId, originalFilename, ex);
        }
    }

    @Override
    public FileUploadResponse uploadFile(MultipartFile file, String folder) {
        FileUploadRequest request = FileUploadRequest.builder()
                .file(file)
                .folder(folder)
                .build();
        return uploadFile(request);
    }

    @Override
    public FileUploadResponse uploadImage(MultipartFile file, String folder) {
        FileValidationUtil.validateImage(file);
        return uploadFile(file, folder);
    }

    @Override
    public FileUploadResponse uploadDocument(MultipartFile file, String folder) {
        FileValidationUtil.validateDocument(file);
        return uploadFile(file, folder);
    }

    @Override
    public boolean deleteFile(String publicId) {
        if (!StringUtils.hasText(publicId)) {
            log.warn("Delete request rejected: publicId is blank");
            return false;
        }

        boolean deleted = deleteFile(publicId, "image");
        if (!deleted) {
            log.debug("Asset not found under 'image' resource type, retrying delete under 'raw' for publicId '{}'", publicId);
            deleted = deleteFile(publicId, "raw");
        }
        return deleted;
    }

    @Override
    public boolean deleteFile(String publicId, String resourceType) {
        if (!StringUtils.hasText(publicId)) {
            return false;
        }

        try {
            log.info("Deleting Cloudinary asset with publicId '{}' (resourceType: '{}')", publicId, resourceType);
            Map<String, Object> params = ObjectUtils.asMap(
                    "resource_type", StringUtils.hasText(resourceType) ? resourceType : "image",
                    "invalidate", true
            );
            Map<?, ?> result = cloudinary.uploader().destroy(publicId, params);
            String resultStatus = (String) result.get("result");
            boolean success = "ok".equalsIgnoreCase(resultStatus);

            if (success) {
                log.info("Successfully deleted Cloudinary asset with publicId '{}'", publicId);
            } else {
                log.warn("Cloudinary delete result for publicId '{}' was '{}'", publicId, resultStatus);
            }
            return success;
        } catch (IOException ex) {
            log.error("Failed to delete Cloudinary asset with publicId '{}': {}", publicId, ex.getMessage(), ex);
            return false;
        }
    }

    @Override
    public FileUploadResponse renameFile(String oldPublicId, String newPublicId) {
        if (!StringUtils.hasText(oldPublicId) || !StringUtils.hasText(newPublicId)) {
            throw new FileStorageException("INVALID_RENAME_ID", "Both old and new public IDs are required for rename operation");
        }

        try {
            log.info("Renaming Cloudinary asset from '{}' to '{}'", oldPublicId, newPublicId);
            Map<?, ?> renameResult = cloudinary.uploader().rename(oldPublicId, newPublicId, ObjectUtils.asMap(
                    "overwrite", true,
                    "invalidate", true
            ));

            log.info("Successfully renamed Cloudinary asset to '{}'", newPublicId);
            return mapToUploadResponse(renameResult, newPublicId, "", "Renamed asset");
        } catch (IOException ex) {
            log.error("Failed to rename Cloudinary asset from '{}' to '{}': {}", oldPublicId, newPublicId, ex.getMessage(), ex);
            throw new FileStorageException("STORAGE_RENAME_FAILED", "Failed to rename file asset in Cloudinary: " + ex.getMessage(), oldPublicId, oldPublicId, ex);
        }
    }

    @Override
    public FileUploadResponse replaceFile(String publicId, MultipartFile newFile) {
        if (!StringUtils.hasText(publicId)) {
            throw new FileStorageException("INVALID_PUBLIC_ID", "Target public ID is required for file replacement");
        }

        FileValidationUtil.validateFile(newFile);

        log.info("Replacing Cloudinary file asset at publicId '{}'", publicId);
        FileUploadRequest request = FileUploadRequest.builder()
                .file(newFile)
                .customFileName(publicId)
                .overwrite(true)
                .build();

        return uploadFile(request);
    }

    @Override
    public FileMetadata getFileMetadata(String publicId) {
        if (!StringUtils.hasText(publicId)) {
            throw new FileStorageException("INVALID_PUBLIC_ID", "Public ID is required to retrieve metadata");
        }

        try {
            Map<?, ?> resourceInfo;
            try {
                resourceInfo = cloudinary.api().resource(publicId, ObjectUtils.asMap("resource_type", "image"));
            } catch (Exception ex) {
                log.debug("Resource not found under 'image' type, attempting 'raw' for publicId '{}'", publicId);
                resourceInfo = cloudinary.api().resource(publicId, ObjectUtils.asMap("resource_type", "raw"));
            }

            return mapToFileMetadata(resourceInfo);

        } catch (Exception ex) {
            log.error("Failed to fetch Cloudinary resource metadata for publicId '{}': {}", publicId, ex.getMessage(), ex);
            throw new FileStorageException("METADATA_FETCH_FAILED", "Could not retrieve file metadata from Cloudinary: " + ex.getMessage(), publicId, publicId, ex);
        }
    }

    @Override
    public String generatePublicUrl(String publicId) {
        if (!StringUtils.hasText(publicId)) {
            return "";
        }
        return cloudinary.url().secure(false).generate(publicId);
    }

    @Override
    public String generateSecureUrl(String publicId) {
        if (!StringUtils.hasText(publicId)) {
            return "";
        }
        return cloudinary.url().secure(true).generate(publicId);
    }

    private FileUploadResponse mapToUploadResponse(Map<?, ?> uploadResult, String originalFileName, String folder, String description) {
        String publicId = (String) uploadResult.get("public_id");
        String url = (String) uploadResult.get("url");
        String secureUrl = (String) uploadResult.get("secure_url");
        String format = (String) uploadResult.get("format");
        String resourceType = (String) uploadResult.get("resource_type");
        String etag = (String) uploadResult.get("etag");

        long size = 0L;
        Object bytesObj = uploadResult.get("bytes");
        if (bytesObj instanceof Number) {
            size = ((Number) bytesObj).longValue();
        }

        Integer width = null;
        Object widthObj = uploadResult.get("width");
        if (widthObj instanceof Number) {
            width = ((Number) widthObj).intValue();
        }

        Integer height = null;
        Object heightObj = uploadResult.get("height");
        if (heightObj instanceof Number) {
            height = ((Number) heightObj).intValue();
        }

        Instant createdAt = parseCreatedAt(uploadResult.get("created_at"));

        return FileUploadResponse.builder()
                .publicId(publicId)
                .url(url)
                .secureUrl(secureUrl)
                .fileName(publicId != null && publicId.contains("/") ? publicId.substring(publicId.lastIndexOf('/') + 1) : publicId)
                .originalFileName(originalFileName)
                .format(format)
                .resourceType(resourceType)
                .size(size)
                .width(width)
                .height(height)
                .folder(folder)
                .createdAt(createdAt)
                .etag(etag)
                .description(description)
                .build();
    }

    private FileMetadata mapToFileMetadata(Map<?, ?> info) {
        String publicId = (String) info.get("public_id");
        String url = (String) info.get("url");
        String secureUrl = (String) info.get("secure_url");
        String format = (String) info.get("format");
        String resourceType = (String) info.get("resource_type");
        String etag = (String) info.get("etag");

        long size = 0L;
        Object bytesObj = info.get("bytes");
        if (bytesObj instanceof Number) {
            size = ((Number) bytesObj).longValue();
        }

        long version = 1L;
        Object verObj = info.get("version");
        if (verObj instanceof Number) {
            version = ((Number) verObj).longValue();
        }

        Integer width = null;
        Object widthObj = info.get("width");
        if (widthObj instanceof Number) {
            width = ((Number) widthObj).intValue();
        }

        Integer height = null;
        Object heightObj = info.get("height");
        if (heightObj instanceof Number) {
            height = ((Number) heightObj).intValue();
        }

        String folder = publicId != null && publicId.contains("/") ? publicId.substring(0, publicId.lastIndexOf('/')) : "";

        return FileMetadata.builder()
                .publicId(publicId)
                .fileName(publicId != null && publicId.contains("/") ? publicId.substring(publicId.lastIndexOf('/') + 1) : publicId)
                .format(format)
                .resourceType(resourceType)
                .size(size)
                .width(width)
                .height(height)
                .url(url)
                .secureUrl(secureUrl)
                .folder(folder)
                .createdAt(parseCreatedAt(info.get("created_at")))
                .version(version)
                .etag(etag)
                .build();
    }

    private Instant parseCreatedAt(Object createdAtObj) {
        if (createdAtObj == null) {
            return Instant.now();
        }
        try {
            return Instant.parse(createdAtObj.toString());
        } catch (DateTimeParseException ex) {
            return Instant.now();
        }
    }
}
