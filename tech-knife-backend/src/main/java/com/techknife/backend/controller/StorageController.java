package com.techknife.backend.controller;

import com.techknife.backend.dto.ApiResponse;
import com.techknife.backend.storage.CloudinaryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.springframework.messaging.simp.SimpMessagingTemplate;

@RestController
@RequestMapping({"/api/v1/storage", "/api/storage", "/storage"})
@RequiredArgsConstructor
@Tag(name = "Cloudinary Storage Engine", description = "Cloud asset CDN upload and media management")
public class StorageController {

    private final CloudinaryService cloudinaryService;
    private final SimpMessagingTemplate messagingTemplate;

    @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "Upload media file or document to Cloudinary CDN")
    public ResponseEntity<ApiResponse<Map<String, Object>>> uploadFile(
            @RequestParam("file") MultipartFile file,
            @RequestParam(value = "folder", required = false) String folder,
            @RequestParam(value = "category", required = false) String category,
            @RequestParam(value = "uploadedBy", required = false) String uploadedBy,
            @RequestParam(value = "module", required = false) String module,
            @RequestParam(value = "projectCode", required = false) String projectCode) {

        String targetFolder = folder != null && !folder.isBlank() ? folder :
                (category != null ? category.toLowerCase().replaceAll("\\s+", "_") : "avatars");

        String url = cloudinaryService.uploadFile(file, targetFolder);

        String id = "file-" + UUID.randomUUID().toString().substring(0, 8);
        String filename = file.getOriginalFilename() != null ? file.getOriginalFilename() : "file";
        String format = filename.contains(".") ? filename.substring(filename.lastIndexOf(".") + 1) : "bin";

        Map<String, Object> responseData = new HashMap<>();
        responseData.put("id", id);
        responseData.put("name", filename);
        responseData.put("category", category != null ? category : "Documents");
        responseData.put("url", url);
        responseData.put("publicId", "techknife/" + targetFolder + "/" + id);
        responseData.put("fileSize", file.getSize());
        responseData.put("format", format);
        responseData.put("uploadedBy", uploadedBy != null ? uploadedBy : "Admin User");
        responseData.put("uploadedByEmail", "admin@techknife.io");
        responseData.put("module", module != null ? module : "General Storage");
        responseData.put("isPrivate", false);
        responseData.put("createdAt", java.time.Instant.now().toString());

        try {
            String target = (projectCode != null && !projectCode.isBlank()) ? projectCode : targetFolder;
            messagingTemplate.convertAndSend("/topic/project." + target, Map.of(
                "eventType", "DOCUMENT_UPLOADED",
                "fileName", filename,
                "url", url,
                "folder", targetFolder
            ));
        } catch (Exception ignored) {}

        return ResponseEntity.ok(ApiResponse.success(responseData, "File uploaded successfully to Cloudinary"));
    }
}

