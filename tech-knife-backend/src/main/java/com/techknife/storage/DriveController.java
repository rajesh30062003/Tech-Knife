package com.techknife.storage;

import com.techknife.backend.dto.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.net.URI;
import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping({"/api/v1/drive", "/api/drive", "/drive"})
@RequiredArgsConstructor
@Tag(name = "Google Drive Integration", description = "Google Drive Cloud File Storage & Document Pipeline")
public class DriveController {

    private final GoogleDriveService googleDriveService;
    private final SimpMessagingTemplate messagingTemplate;
    private final com.techknife.project.service.ProjectActivityService projectActivityService;

    @GetMapping("/oauth-config-status")
    @Operation(summary = "Check Google Drive OAuth Configuration Status (Safe Diagnostics)")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getOauthConfigStatus() {
        Map<String, Object> status = googleDriveService.getOauthConfigStatus();
        return ResponseEntity.ok(ApiResponse.success(status, "Google Drive OAuth configuration status fetched"));
    }

    @GetMapping("/project/{projectCode}")
    @Operation(summary = "Get Project Documents from Google Drive Repository")
    public ResponseEntity<ApiResponse<List<DriveFileRecord>>> getProjectDocuments(@PathVariable String projectCode) {
        log.info("GET Drive documents for projectCode: {}", projectCode);
        List<DriveFileRecord> files = googleDriveService.getFilesByProject(projectCode);
        return ResponseEntity.ok(ApiResponse.success(files, "Fetched Google Drive project documents successfully"));
    }

    @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "Upload Project Document to Google Drive")
    public ResponseEntity<ApiResponse<DriveFileRecord>> uploadDocument(
            @RequestParam("file") MultipartFile file,
            @RequestParam("projectCode") String projectCode,
            @RequestParam(value = "category", defaultValue = "Project File") String category,
            @RequestParam(value = "uploadedBy", defaultValue = "Executive Admin") String uploadedBy) {

        log.info("POST Upload Drive Document: file={}, projectCode={}, category={}",
                file.getOriginalFilename(), projectCode, category);

        try {
            DriveFileRecord record = googleDriveService.uploadFile(file, projectCode, category, uploadedBy);

            try {
                projectActivityService.logActivity(
                        projectCode, projectCode,
                        "Document Uploaded", "DOCUMENT",
                        "Uploaded document '" + record.getName() + "' (" + category + ").",
                        "Document", null, record.getName()
                );
            } catch (Exception e) {
                log.warn("Activity logging failed for document upload: {}", e.getMessage());
            }

            try {
                messagingTemplate.convertAndSend("/topic/project." + projectCode, Map.of(
                        "eventType", "DOCUMENT_UPLOADED",
                        "fileId", record.getFileId(),
                        "fileName", record.getName(),
                        "url", record.getWebViewLink(),
                        "category", record.getCategory()
                ));
            } catch (Exception e) {
                log.warn("STOMP broadcast for document upload warning: {}", e.getMessage());
            }

            return ResponseEntity.ok(ApiResponse.success(record, "Document uploaded to Google Drive successfully"));
        } catch (Exception e) {
            log.error("[DriveController] Upload document failed: {}", e.getMessage(), e);
            String safeMsg = "Document upload is temporarily unavailable due to storage configuration or service error.";
            if (e.getMessage() != null && e.getMessage().contains("Missing Google OAuth")) {
                safeMsg = "Document upload is temporarily unavailable because Google Drive storage credentials are missing.";
            }
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ApiResponse.error(safeMsg));
        }
    }

    @GetMapping("/download/{fileId}")
    @Operation(summary = "Download Document from Google Drive")
    public ResponseEntity<byte[]> downloadFile(@PathVariable String fileId) {
        log.info("----------------------------------");
        log.info("REQUEST RECEIVED: endpoint=/api/v1/drive/download/{}", fileId);
        DriveFileRecord record = googleDriveService.getFileById(fileId).orElse(null);

        if (record == null) {
            log.warn("Download record NOT FOUND for fileId: {}", fileId);
            return ResponseEntity.notFound().build();
        }

        try {
            String pCode = record.getProjectCode() != null ? record.getProjectCode() : "PROJECT-SYS";
            projectActivityService.logActivity(
                    pCode, pCode,
                    "Document Downloaded", "DOCUMENT",
                    "Downloaded document '" + record.getName() + "'.",
                    "Document", record.getName(), null
            );
        } catch (Exception e) {
            log.warn("Activity logging failed for document download: {}", e.getMessage());
        }

        byte[] bytes = record.getFileData();
        if (bytes == null || bytes.length == 0) {
            if (record.getSecureUrl() != null && (record.getSecureUrl().startsWith("http://") || record.getSecureUrl().startsWith("https://"))) {
                try {
                    bytes = new java.net.URL(record.getSecureUrl()).openStream().readAllBytes();
                } catch (Exception e) {
                    log.warn("Could not fetch remote bytes from secureUrl '{}': {}", record.getSecureUrl(), e.getMessage());
                }
            }
        }

        if (bytes == null || bytes.length == 0) {
            log.error("Download failed: File bytes missing/empty for fileId: {}", fileId);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }

        String mime = record.getMimeType() != null ? record.getMimeType() : "application/octet-stream";
        String filename = record.getName() != null ? record.getName() : "download.pdf";

        log.info("fileId: {}, mimeType: {}, filename: {}, contentLength: {} bytes, user: {}",
                fileId, mime, filename, bytes.length, record.getUploadedBy());
        log.info("Returning binary stream (byte[]) - Content-Type: {}, Content-Length: {}", mime, bytes.length);
        log.info("----------------------------------");

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(mime))
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + filename + "\"")
                .header(HttpHeaders.CONTENT_LENGTH, String.valueOf(bytes.length))
                .body(bytes);
    }

    @GetMapping("/preview/{fileId}")
    @Operation(summary = "Preview Document from Google Drive")
    public ResponseEntity<byte[]> previewFile(@PathVariable String fileId) {
        log.info("----------------------------------");
        log.info("REQUEST RECEIVED: endpoint=/api/v1/drive/preview/{}", fileId);
        DriveFileRecord record = googleDriveService.getFileById(fileId).orElse(null);

        if (record == null) {
            log.warn("Preview record NOT FOUND for fileId: {}", fileId);
            return ResponseEntity.notFound().build();
        }

        byte[] bytes = record.getFileData();
        if (bytes == null || bytes.length == 0) {
            if (record.getSecureUrl() != null && (record.getSecureUrl().startsWith("http://") || record.getSecureUrl().startsWith("https://"))) {
                try {
                    bytes = new java.net.URL(record.getSecureUrl()).openStream().readAllBytes();
                } catch (Exception e) {
                    log.warn("Could not fetch remote preview bytes from secureUrl '{}': {}", record.getSecureUrl(), e.getMessage());
                }
            }
        }

        if (bytes == null || bytes.length == 0) {
            log.error("Preview failed: File bytes missing/empty for fileId: {}", fileId);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }

        String mime = record.getMimeType() != null ? record.getMimeType() : "application/pdf";
        String filename = record.getName() != null ? record.getName() : "document.pdf";

        log.info("fileId: {}, mimeType: {}, filename: {}, contentLength: {} bytes, user: {}",
                fileId, mime, filename, bytes.length, record.getUploadedBy());
        log.info("Returning binary stream (byte[]) - Content-Type: {}, Content-Length: {}", mime, bytes.length);
        log.info("----------------------------------");

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(mime))
                .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + filename + "\"")
                .header(HttpHeaders.CONTENT_LENGTH, String.valueOf(bytes.length))
                .body(bytes);
    }

    @DeleteMapping("/{fileId}")
    @Operation(summary = "Delete Document from Google Drive")
    public ResponseEntity<ApiResponse<Void>> deleteDocument(@PathVariable String fileId) {
        log.info("DELETE Drive document fileId: {}", fileId);
        googleDriveService.deleteFile(fileId);
        return ResponseEntity.ok(ApiResponse.success(null, "Document deleted from Google Drive successfully"));
    }

    @GetMapping("/oauth2callback")
    @Operation(summary = "Google Drive OAuth 2.0 Callback Endpoint")
    public ResponseEntity<ApiResponse<Map<String, Object>>> oauth2Callback(@RequestParam(value = "code", required = false) String code) {
        log.info("Google Drive OAuth2 callback invoked with authorization code: {}", code);
        if (code != null && !code.isBlank()) {
            Map<String, Object> tokens = googleDriveService.exchangeAuthorizationCode(code);
            log.info("Successfully exchanged authorization code for OAuth tokens: {}", tokens);
            return ResponseEntity.ok(ApiResponse.success(tokens, "Google Drive OAuth 2.0 Authentication Success!"));
        }
        return ResponseEntity.ok(ApiResponse.success(Map.of("message", "No code provided"), "Google Drive Auth Warning"));
    }
}
