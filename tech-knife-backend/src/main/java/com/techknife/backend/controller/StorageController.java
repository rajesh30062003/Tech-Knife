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

import java.util.Map;

@RestController
@RequestMapping("/storage")
@RequiredArgsConstructor
@Tag(name = "Cloudinary Storage Engine", description = "Cloud asset CDN upload and media management")
public class StorageController {

    private final CloudinaryService cloudinaryService;

    @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "Upload media file or document to Cloudinary CDN")
    public ResponseEntity<ApiResponse<Map<String, String>>> uploadFile(
            @RequestParam("file") MultipartFile file,
            @RequestParam(value = "folder", defaultValue = "avatars") String folder) {
        String url = cloudinaryService.uploadFile(file, folder);
        Map<String, String> responseData = Map.of("url", url, "folder", folder);
        return ResponseEntity.ok(ApiResponse.success(responseData, "File uploaded successfully to Cloudinary"));
    }
}
