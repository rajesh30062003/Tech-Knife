package com.techknife.storage;

import org.springframework.web.multipart.MultipartFile;

/**
 * Interface contract for managing file uploads, replacements, deletions, metadata retrievals, and secure URL generation with Cloudinary.
 */
public interface FileStorageService {

    /**
     * Uploads a file with full custom parameters defined in {@link FileUploadRequest}.
     */
    FileUploadResponse uploadFile(FileUploadRequest request);

    /**
     * Helper to upload a file to a designated target folder.
     */
    FileUploadResponse uploadFile(MultipartFile file, String folder);

    /**
     * Helper to validate and upload an image file.
     */
    FileUploadResponse uploadImage(MultipartFile file, String folder);

    /**
     * Helper to validate and upload a document file.
     */
    FileUploadResponse uploadDocument(MultipartFile file, String folder);

    /**
     * Deletes a file from Cloudinary storage by public ID using auto-detected resource type.
     */
    boolean deleteFile(String publicId);

    /**
     * Deletes a file from Cloudinary storage by public ID and explicit resource type ("image", "raw", "video").
     */
    boolean deleteFile(String publicId, String resourceType);

    /**
     * Renames an existing file asset in Cloudinary.
     */
    FileUploadResponse renameFile(String oldPublicId, String newPublicId);

    /**
     * Replaces an existing file in Cloudinary with a new uploaded file keeping the target public ID location.
     */
    FileUploadResponse replaceFile(String publicId, MultipartFile newFile);

    /**
     * Retrieves full resource metadata for a file stored in Cloudinary.
     */
    FileMetadata getFileMetadata(String publicId);

    /**
     * Generates a public standard HTTP URL for a given public ID.
     */
    String generatePublicUrl(String publicId);

    /**
     * Generates a secure HTTPS URL for a given public ID.
     */
    String generateSecureUrl(String publicId);
}
