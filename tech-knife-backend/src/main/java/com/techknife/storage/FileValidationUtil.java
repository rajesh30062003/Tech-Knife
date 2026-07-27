package com.techknife.storage;

import lombok.extern.slf4j.Slf4j;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import java.util.regex.Pattern;

/**
 * Security and format validation utility for file uploads.
 */
@Slf4j
public class FileValidationUtil {

    public static final long MAX_FILE_SIZE_BYTES = 25 * 1024 * 1024; // 25 MB
    public static final long MAX_IMAGE_SIZE_BYTES = 10 * 1024 * 1024; // 10 MB

    public static final Set<String> ALLOWED_IMAGE_EXTENSIONS = new HashSet<>(Arrays.asList(
            "png", "jpg", "jpeg", "webp", "svg"
    ));

    public static final Set<String> ALLOWED_IMAGE_MIME_TYPES = new HashSet<>(Arrays.asList(
            "image/png", "image/jpeg", "image/jpg", "image/webp", "image/svg+xml"
    ));

    public static final Set<String> ALLOWED_DOCUMENT_EXTENSIONS = new HashSet<>(Arrays.asList(
            "pdf", "docx", "doc", "xlsx", "xls", "pptx", "txt", "zip"
    ));

    public static final Set<String> ALLOWED_DOCUMENT_MIME_TYPES = new HashSet<>(Arrays.asList(
            "application/pdf",
            "application/vnd.openxmlformats-officedocument.wordprocessingml.document",
            "application/msword",
            "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet",
            "application/vnd.ms-excel",
            "application/vnd.openxmlformats-officedocument.presentationml.presentation",
            "text/plain",
            "application/zip",
            "application/x-zip-compressed",
            "application/x-zip"
    ));

    public static final Set<String> REJECTED_EXECUTABLE_EXTENSIONS = new HashSet<>(Arrays.asList(
            "exe", "bat", "cmd", "sh", "js", "msi", "jar", "jsp", "php", "vbs",
            "ps1", "py", "scr", "com", "pif", "dll", "bin", "cgi", "pl"
    ));

    public static final Set<String> ALLOWED_FOLDERS = new HashSet<>(Arrays.asList(
            "employee", "customer", "project", "company", "documents",
            "payroll", "intern", "recruitment", "cms", "temporary"
    ));

    private static final Pattern INVALID_FILENAME_CHARS = Pattern.compile("[^a-zA-Z0-9._-]");

    /**
     * Validates a MultipartFile for size, empty status, extension, and executable rejection.
     *
     * @param file input file
     */
    public static void validateFile(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            log.warn("File upload validation failed: File is empty or null");
            throw new FileStorageException("FILE_EMPTY", "Uploaded file is empty or missing");
        }

        if (file.getSize() > MAX_FILE_SIZE_BYTES) {
            log.warn("File upload validation failed: File size {} exceeds limit of {} bytes", file.getSize(), MAX_FILE_SIZE_BYTES);
            throw new FileStorageException("FILE_SIZE_EXCEEDED", "File size exceeds maximum allowed limit of 25MB");
        }

        String originalFilename = file.getOriginalFilename();
        if (!StringUtils.hasText(originalFilename)) {
            throw new FileStorageException("INVALID_FILENAME", "File original name cannot be empty");
        }

        String cleanFilename = sanitizeFilename(originalFilename);
        String extension = getFileExtension(cleanFilename);

        if (REJECTED_EXECUTABLE_EXTENSIONS.contains(extension)) {
            log.error("Security alert: Executable file upload attempt blocked for filename '{}'", originalFilename);
            throw new FileStorageException("EXECUTABLE_FILE_BLOCKED", "Executable files are strictly prohibited for security reasons");
        }

        boolean isAllowedImage = ALLOWED_IMAGE_EXTENSIONS.contains(extension);
        boolean isAllowedDoc = ALLOWED_DOCUMENT_EXTENSIONS.contains(extension);

        if (!isAllowedImage && !isAllowedDoc) {
            log.warn("File upload validation failed: Unsupported file extension '.{}'", extension);
            throw new FileStorageException("UNSUPPORTED_FILE_TYPE", "Unsupported file extension: ." + extension);
        }

        String contentType = file.getContentType();
        if (StringUtils.hasText(contentType)) {
            String mimeType = contentType.toLowerCase().trim();
            boolean isAllowedMime = ALLOWED_IMAGE_MIME_TYPES.contains(mimeType) || ALLOWED_DOCUMENT_MIME_TYPES.contains(mimeType);
            if (!isAllowedMime && !mimeType.equalsIgnoreCase("application/octet-stream")) {
                log.warn("File upload validation failed: Unsupported MIME type '{}'", mimeType);
                throw new FileStorageException("UNSUPPORTED_MIME_TYPE", "Unsupported file MIME type: " + mimeType);
            }
        }
    }

    /**
     * Validates specifically that the file is an allowed image.
     */
    public static void validateImage(MultipartFile file) {
        validateFile(file);

        if (file.getSize() > MAX_IMAGE_SIZE_BYTES) {
            throw new FileStorageException("IMAGE_SIZE_EXCEEDED", "Image file size exceeds maximum allowed limit of 10MB");
        }

        String extension = getFileExtension(file.getOriginalFilename());
        if (!ALLOWED_IMAGE_EXTENSIONS.contains(extension)) {
            throw new FileStorageException("INVALID_IMAGE_TYPE", "File is not a valid image format. Allowed formats: PNG, JPG, JPEG, WEBP, SVG");
        }
    }

    /**
     * Validates specifically that the file is an allowed document.
     */
    public static void validateDocument(MultipartFile file) {
        validateFile(file);

        String extension = getFileExtension(file.getOriginalFilename());
        if (!ALLOWED_DOCUMENT_EXTENSIONS.contains(extension)) {
            throw new FileStorageException("INVALID_DOCUMENT_TYPE", "File is not a valid document format. Allowed formats: PDF, DOCX, DOC, XLSX, XLS, PPTX, TXT, ZIP");
        }
    }

    /**
     * Validates that the requested storage folder is one of the allowed system folders.
     */
    public static String validateAndNormalizeFolder(String folder) {
        if (!StringUtils.hasText(folder)) {
            return "temporary";
        }

        String normalizedFolder = folder.toLowerCase().trim().replaceAll("[^a-z0-9_-]", "");
        if (!ALLOWED_FOLDERS.contains(normalizedFolder)) {
            log.info("Folder '{}' not in strict whitelist, placing in 'temporary/{}'", folder, normalizedFolder);
            return "temporary/" + normalizedFolder;
        }

        return normalizedFolder;
    }

    /**
     * Sanitizes filename preventing path traversal attacks (strips directory paths and dangerous characters).
     */
    public static String sanitizeFilename(String filename) {
        if (!StringUtils.hasText(filename)) {
            return "unnamed_file";
        }

        // Strip path traversal attempts like ../ or ..\
        String clean = filename.replaceAll("^[./\\\\]+", "");
        clean = clean.substring(clean.lastIndexOf('/') + 1);
        clean = clean.substring(clean.lastIndexOf('\\') + 1);

        // Replace spaces with underscores
        clean = clean.replaceAll("\\s+", "_");

        // Strip special non-alphanumeric characters except dot, underscore, dash
        return INVALID_FILENAME_CHARS.matcher(clean).replaceAll("");
    }

    /**
     * Extracts lowercase file extension from filename.
     */
    public static String getFileExtension(String filename) {
        if (!StringUtils.hasText(filename)) {
            return "";
        }
        int lastDot = filename.lastIndexOf('.');
        if (lastDot == -1 || lastDot == filename.length() - 1) {
            return "";
        }
        return filename.substring(lastDot + 1).toLowerCase();
    }

    /**
     * Generates a unique public ID for Cloudinary using a UUID prefix and sanitized filename.
     */
    public static String generateUniquePublicId(String filename, String folder) {
        String cleanName = sanitizeFilename(filename);
        String nameWithoutExt = cleanName.contains(".") ? cleanName.substring(0, cleanName.lastIndexOf('.')) : cleanName;
        String uniqueName = UUID.randomUUID().toString().substring(0, 8) + "_" + nameWithoutExt;

        String normalizedFolder = validateAndNormalizeFolder(folder);
        return normalizedFolder + "/" + uniqueName;
    }

    /**
     * Determines Cloudinary resource_type ("image", "raw", or "auto") based on file extension.
     */
    public static String determineResourceType(String extension) {
        if (ALLOWED_IMAGE_EXTENSIONS.contains(extension.toLowerCase())) {
            return "image";
        }
        return "raw";
    }
}
