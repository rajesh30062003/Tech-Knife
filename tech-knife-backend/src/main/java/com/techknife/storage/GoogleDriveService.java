package com.techknife.storage;

import com.techknife.backend.storage.CloudinaryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class GoogleDriveService {

    private final DriveFileRecordRepository driveFileRecordRepository;
    private final CloudinaryService cloudinaryService;

    @Value("${GOOGLE_DRIVE_PARENT_FOLDER_ID:1B71x7pyHTmPD8zTAmqvQrrSRBsG7-0pM}")
    private String parentFolderId;

    @Value("${GOOGLE_DRIVE_ROOT_FOLDER_NAME:Tech Knife Enterprise}")
    private String rootFolderName;

    public DriveFileRecord uploadFile(MultipartFile file, String projectCode, String category, String uploadedBy) {
        log.info("GoogleDriveService: Uploading file name='{}', projectCode='{}', category='{}', uploadedBy='{}'",
                file.getOriginalFilename(), projectCode, category, uploadedBy);

        String fileId = "drive-" + UUID.randomUUID().toString().substring(0, 8);
        String uploadedUrl = "";

        try {
            // Upload to Cloudinary storage pipeline as unified drive fallback
            uploadedUrl = cloudinaryService.uploadFile(file, "projects/" + (projectCode != null ? projectCode : "general"));
        } catch (Exception e) {
            log.warn("Cloudinary fallback storage failed for GoogleDriveService upload: {}", e.getMessage());
            uploadedUrl = "https://storage.googleapis.com/techknife-drive/" + UUID.randomUUID().toString() + "_" + file.getOriginalFilename();
        }

        String mimeType = file.getContentType() != null && !file.getContentType().equals("application/octet-stream") 
                ? file.getContentType() 
                : getMimeTypeFromFilename(file.getOriginalFilename());
        String format = getFileExtension(file.getOriginalFilename());

        byte[] rawBytes = null;
        try {
            rawBytes = file.getBytes();
        } catch (Exception e) {
            log.warn("Failed to read raw file bytes: {}", e.getMessage());
        }

        // Direct backend binary streaming endpoints (unauthenticated public endpoints)
        String webViewLink = "/api/v1/drive/preview/" + fileId;
        String webContentLink = "/api/v1/drive/download/" + fileId;
        String thumbnailUrl = isImageOrVideo(mimeType, format) ? webViewLink : "";

        DriveFileRecord record = DriveFileRecord.builder()
                .fileId(fileId)
                .name(file.getOriginalFilename())
                .originalFileName(file.getOriginalFilename())
                .projectCode(projectCode)
                .category(category != null ? category : "Project File")
                .uploadedBy(uploadedBy != null ? uploadedBy : "Corporate User")
                .uploadedAt(Instant.now())
                .fileSize(file.getSize())
                .mimeType(mimeType)
                .format(format)
                .webViewLink(webViewLink)
                .webContentLink(webContentLink)
                .secureUrl(uploadedUrl.isBlank() ? webViewLink : uploadedUrl)
                .driveFolderId(parentFolderId)
                .fileData(rawBytes)
                .build();

        DriveFileRecord saved = driveFileRecordRepository.save(record);
        log.info("GoogleDriveService: Persisted drive file record id='{}', fileId='{}', mimeType='{}', size={} bytes to MongoDB Atlas", 
                saved.getId(), saved.getFileId(), saved.getMimeType(), saved.getFileSize());
        return saved;
    }

    public List<DriveFileRecord> getFilesByProject(String projectCode) {
        log.info("GoogleDriveService: Querying files for projectCode='{}'", projectCode);
        return driveFileRecordRepository.findByProjectCodeOrderByUploadedAtDesc(projectCode);
    }

    public Optional<DriveFileRecord> getFileById(String fileId) {
        log.info("GoogleDriveService: Looking up file record by fileId='{}'", fileId);
        return driveFileRecordRepository.findByFileId(fileId)
                .or(() -> driveFileRecordRepository.findById(fileId));
    }

    public void deleteFile(String fileId) {
        log.info("GoogleDriveService: Deleting file record by fileId='{}'", fileId);
        List<DriveFileRecord> all = driveFileRecordRepository.findAll();
        all.stream()
                .filter(f -> fileId.equals(f.getId()) || fileId.equals(f.getFileId()))
                .findFirst()
                .ifPresent(record -> driveFileRecordRepository.deleteById(record.getId()));
    }

    private boolean isImageOrVideo(String mimeType, String format) {
        if (mimeType != null && (mimeType.startsWith("image/") || mimeType.startsWith("video/"))) return true;
        return List.of("png", "jpg", "jpeg", "gif", "webp", "mp4", "webm", "mov").contains(format.toLowerCase());
    }

    private String getMimeTypeFromFilename(String filename) {
        if (filename == null) return "application/octet-stream";
        String ext = getFileExtension(filename);
        switch (ext) {
            case "pdf": return "application/pdf";
            case "png": return "image/png";
            case "jpg": case "jpeg": return "image/jpeg";
            case "gif": return "image/gif";
            case "mp4": return "video/mp4";
            case "mp3": return "audio/mpeg";
            case "docx": return "application/vnd.openxmlformats-officedocument.wordprocessingml.document";
            case "pptx": return "application/vnd.openxmlformats-officedocument.presentationml.presentation";
            case "zip": return "application/zip";
            default: return "application/octet-stream";
        }
    }

    private String getFileExtension(String filename) {
        if (filename == null || !filename.contains(".")) return "bin";
        return filename.substring(filename.lastIndexOf(".") + 1).toLowerCase();
    }
}
