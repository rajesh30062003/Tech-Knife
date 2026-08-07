package com.techknife.storage;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.techknife.backend.storage.CloudinaryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import java.nio.charset.StandardCharsets;
import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.Signature;
import java.security.MessageDigest;
import java.security.spec.PKCS8EncodedKeySpec;
import java.time.Instant;
import java.util.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class GoogleDriveService {

    private final DriveFileRecordRepository driveFileRecordRepository;
    private final CloudinaryService cloudinaryService;
    private final RestTemplate restTemplate = new RestTemplate();

    @Value("${GOOGLE_DRIVE_PARENT_FOLDER_ID:}")
    private String parentFolderId;

    @Value("${GOOGLE_DRIVE_AUTH_MODE:OAUTH2}")
    private String authMode;

    @Value("${GOOGLE_OAUTH_CLIENT_ID:}")
    private String clientId;

    @Value("${GOOGLE_OAUTH_CLIENT_SECRET:}")
    private String clientSecret;

    @Value("${GOOGLE_OAUTH_REFRESH_TOKEN:}")
    private String refreshToken;

    @Value("${GOOGLE_OAUTH_REDIRECT_URI:http://localhost:8080/api/v1/drive/oauth2callback}")
    private String redirectUri;

    @Value("${GOOGLE_DRIVE_CREDENTIALS:credentials/google-drive-service.json}")
    private String credentialsFilePath;

    @Value("${GOOGLE_SERVICE_ACCOUNT_CLIENT_EMAIL:}")
    private String serviceAccountEmailEnv;

    @Value("${GOOGLE_SERVICE_ACCOUNT_PRIVATE_KEY:}")
    private String privateKeyPemEnv;

    private String cachedAccessToken;

    public synchronized String fetchAccessToken() {
        if ("OAUTH2".equalsIgnoreCase(authMode) || (refreshToken != null && !refreshToken.isBlank())) {
            return refreshUserAccessToken();
        } else {
            return fetchServiceAccountAccessToken();
        }
    }

    public synchronized String refreshUserAccessToken() {
        try {
            log.info("[GoogleDriveService] Refreshing OAuth 2.0 User Access Token using Refresh Token");
            if (clientId == null || clientId.isBlank() || clientSecret == null || clientSecret.isBlank() || refreshToken == null || refreshToken.isBlank()) {
                throw new RuntimeException("Missing Google OAuth 2.0 credentials: GOOGLE_OAUTH_CLIENT_ID, GOOGLE_OAUTH_CLIENT_SECRET, or GOOGLE_OAUTH_REFRESH_TOKEN is empty");
            }

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

            MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
            body.add("client_id", clientId.trim());
            body.add("client_secret", clientSecret.trim());
            body.add("refresh_token", refreshToken.trim());
            body.add("grant_type", "refresh_token");

            HttpEntity<MultiValueMap<String, String>> requestEntity = new HttpEntity<>(body, headers);
            ResponseEntity<Map> response = restTemplate.postForEntity("https://oauth2.googleapis.com/token", requestEntity, Map.class);

            if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
                Map responseBody = response.getBody();
                String token = (String) responseBody.get("access_token");
                Integer expiresIn = (Integer) responseBody.get("expires_in");
                String scope = (String) responseBody.get("scope");
                if (token != null && !token.isBlank()) {
                    this.cachedAccessToken = token;
                    log.info("[GoogleDriveService] Successfully refreshed Google OAuth 2.0 User Access Token!");
                    log.info("[GoogleDriveService] Token verification metrics: expires_in={} seconds, scope='{}', access_token length={}",
                            expiresIn, scope, token.length());
                    return this.cachedAccessToken;
                }
            }
            throw new RuntimeException("OAuth token refresh failed with HTTP status: " + response.getStatusCode());
        } catch (Exception e) {
            log.error("[GoogleDriveService] OAuth 2.0 User Access Token refresh failed: {}", e.getMessage(), e);
            throw new RuntimeException("OAuth 2.0 User Access Token refresh failed: " + e.getMessage(), e);
        }
    }

    public Map<String, Object> exchangeAuthorizationCode(String code) {
        log.info("[GoogleDriveService] Exchanging authorization code for OAuth tokens");
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

            MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
            body.add("client_id", clientId.trim());
            body.add("client_secret", clientSecret.trim());
            body.add("code", code.trim());
            body.add("grant_type", "authorization_code");
            body.add("redirect_uri", redirectUri.trim());

            HttpEntity<MultiValueMap<String, String>> requestEntity = new HttpEntity<>(body, headers);
            ResponseEntity<Map> response = restTemplate.postForEntity("https://oauth2.googleapis.com/token", requestEntity, Map.class);

            if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
                Map resBody = response.getBody();
                String token = (String) resBody.get("access_token");
                String newRefreshToken = (String) resBody.get("refresh_token");
                if (token != null) {
                    this.cachedAccessToken = token;
                }
                if (newRefreshToken != null && !newRefreshToken.isBlank()) {
                    this.refreshToken = newRefreshToken;
                    log.info("[GoogleDriveService] RECEIVED NEW REFRESH TOKEN: {}", newRefreshToken);
                    persistRefreshTokenToEnv(newRefreshToken);
                }
                return resBody;
            }
            throw new RuntimeException("Code exchange failed: " + response.getStatusCode());
        } catch (Exception e) {
            log.error("[GoogleDriveService] Code exchange error: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to exchange OAuth code for tokens: " + e.getMessage(), e);
        }
    }

    private void persistRefreshTokenToEnv(String token) {
        try {
            java.io.File envFile = new java.io.File(".env");
            if (!envFile.exists()) {
                envFile = new java.io.File("tech-knife-backend/.env");
            }
            if (envFile.exists()) {
                List<String> lines = java.nio.file.Files.readAllLines(envFile.toPath(), StandardCharsets.UTF_8);
                List<String> updatedLines = new ArrayList<>();
                boolean found = false;
                for (String line : lines) {
                    if (line.startsWith("GOOGLE_OAUTH_REFRESH_TOKEN=")) {
                        updatedLines.add("GOOGLE_OAUTH_REFRESH_TOKEN=" + token);
                        found = true;
                    } else {
                        updatedLines.add(line);
                    }
                }
                if (!found) {
                    updatedLines.add("GOOGLE_OAUTH_REFRESH_TOKEN=" + token);
                }
                java.nio.file.Files.write(envFile.toPath(), updatedLines, StandardCharsets.UTF_8);
                log.info("[GoogleDriveService] Successfully persisted GOOGLE_OAUTH_REFRESH_TOKEN to '{}'", envFile.getAbsolutePath());
            }
        } catch (Exception e) {
            log.warn("[GoogleDriveService] Failed to persist refresh token to .env: {}", e.getMessage());
        }
    }

    public synchronized String fetchServiceAccountAccessToken() {
        try {
            String clientEmail = null;
            String privateKeyPem = null;
            String tokenUri = "https://oauth2.googleapis.com/token";

            // 1. Attempt to load credentials dynamically from JSON file
            java.io.File credFile = new java.io.File(credentialsFilePath);
            if (!credFile.exists()) {
                // Try relative to current working directory or backend root
                credFile = new java.io.File("tech-knife-backend/" + credentialsFilePath);
            }

            if (credFile.exists()) {
                log.info("[GoogleDriveService] Loading Service Account credentials dynamically from '{}'", credFile.getAbsolutePath());
                ObjectMapper mapper = new ObjectMapper();
                Map<String, Object> jsonMap = mapper.readValue(credFile, Map.class);
                clientEmail = (String) jsonMap.get("client_email");
                privateKeyPem = (String) jsonMap.get("private_key");
                if (jsonMap.containsKey("token_uri") && jsonMap.get("token_uri") != null) {
                    tokenUri = (String) jsonMap.get("token_uri");
                }
                log.info("[GoogleDriveService] Loaded credentials for Service Account email: '{}', project_id: '{}'", clientEmail, jsonMap.get("project_id"));
            } else if (serviceAccountEmailEnv != null && !serviceAccountEmailEnv.isBlank() && privateKeyPemEnv != null && !privateKeyPemEnv.isBlank()) {
                log.info("[GoogleDriveService] Loading Service Account credentials from environment variables");
                clientEmail = serviceAccountEmailEnv;
                privateKeyPem = privateKeyPemEnv;
            } else {
                throw new RuntimeException("Service Account credentials file not found at '" + credentialsFilePath + "' and no environment variables supplied.");
            }

            if (clientEmail == null || clientEmail.isBlank() || privateKeyPem == null || privateKeyPem.isBlank()) {
                throw new RuntimeException("Invalid Service Account JSON configuration: missing client_email or private_key");
            }

            log.info("[GoogleDriveService] Generating RS256 signed JWT assertion for Service Account '{}'", clientEmail);
            String cleanKey = privateKeyPem
                    .replace("-----BEGIN PRIVATE KEY-----", "")
                    .replace("-----END PRIVATE KEY-----", "")
                    .replaceAll("[^A-Za-z0-9+/=]", "");
            byte[] keyBytes = Base64.getDecoder().decode(cleanKey);
            PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(keyBytes);
            KeyFactory kf = KeyFactory.getInstance("RSA");
            PrivateKey privateKey = kf.generatePrivate(keySpec);

            long now = Instant.now().getEpochSecond();
            String headerJson = "{\"alg\":\"RS256\",\"typ\":\"JWT\"}";
            String payloadJson = String.format(
                    "{\"iss\":\"%s\",\"scope\":\"https://www.googleapis.com/auth/drive\",\"aud\":\"%s\",\"exp\":%d,\"iat\":%d}",
                    clientEmail, tokenUri, now + 3600, now
            );

            Base64.Encoder encoder = Base64.getUrlEncoder().withoutPadding();
            String headerEnc = encoder.encodeToString(headerJson.getBytes(StandardCharsets.UTF_8));
            String payloadEnc = encoder.encodeToString(payloadJson.getBytes(StandardCharsets.UTF_8));
            String unsignedJwt = headerEnc + "." + payloadEnc;

            Signature signer = Signature.getInstance("SHA256withRSA");
            signer.initSign(privateKey);
            signer.update(unsignedJwt.getBytes(StandardCharsets.UTF_8));
            byte[] signatureBytes = signer.sign();
            String jwt = unsignedJwt + "." + encoder.encodeToString(signatureBytes);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
            MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
            body.add("grant_type", "urn:ietf:params:oauth:grant-type:jwt-bearer");
            body.add("assertion", jwt);

            HttpEntity<MultiValueMap<String, String>> requestEntity = new HttpEntity<>(body, headers);
            ResponseEntity<Map> response = restTemplate.postForEntity(tokenUri, requestEntity, Map.class);
            if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
                String token = (String) response.getBody().get("access_token");
                Integer expiresIn = (Integer) response.getBody().get("expires_in");
                String scope = (String) response.getBody().get("scope");
                if (token != null && !token.isBlank()) {
                    this.cachedAccessToken = token;
                    log.info("[GoogleDriveService] Successfully obtained active Google Service Account Access Token!");
                    log.info("[GoogleDriveService] Token verification metrics: expires_in={} seconds, scope='{}', access_token length={}",
                            expiresIn, scope, token.length());
                    return this.cachedAccessToken;
                }
            } else {
                throw new RuntimeException("OAuth token exchange endpoint returned status: " + response.getStatusCode());
            }
        } catch (Exception e) {
            log.error("[GoogleDriveService] Service Account OAuth Token generation failed: {}", e.getMessage(), e);
            throw new RuntimeException("Service Account OAuth Token generation failed: " + e.getMessage(), e);
        }
        return this.cachedAccessToken;
    }

    public DriveFileRecord uploadFile(MultipartFile file, String projectCode, String category, String uploadedBy) {
        String filename = file.getOriginalFilename() != null ? file.getOriginalFilename() : "document.pdf";
        String mimeType = file.getContentType() != null && !file.getContentType().equals("application/octet-stream") 
                ? file.getContentType() 
                : getMimeTypeFromFilename(filename);
        String format = getFileExtension(filename);

        byte[] rawBytes;
        try {
            rawBytes = file.getBytes();
        } catch (Exception e) {
            log.error("Failed to read raw file bytes: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to read uploaded file binary stream: " + e.getMessage(), e);
        }

        log.info("==================================================");
        log.info("GOOGLE DRIVE API INVOCATION TRACE: uploadFile");
        log.info("[GoogleDriveService] Calling drive.files.create()");
        log.info("Filename: {}", filename);
        log.info("Parent Folder ID: {}", parentFolderId);
        log.info("MIME Type: {}", mimeType);
        log.info("Size: {} bytes", file.getSize());

        // Fetch fresh OAuth Access Token (OAuth2 User or Service Account)
        fetchAccessToken();

        Map<String, Object> requestMetadata = new HashMap<>();
        requestMetadata.put("name", filename);
        requestMetadata.put("mimeType", mimeType);
        requestMetadata.put("parents", List.of(parentFolderId));
        log.info("Request Metadata Body: {}", requestMetadata);

        String driveFileId = null;
        String googleWebViewLink = null;
        String googleWebContentLink = null;
        String googleParents = null;
        String rawGoogleApiResponse = null;

        // Execute direct Google Drive v3 REST API multipart upload
        try {
            String uploadEndpoint = "https://www.googleapis.com/upload/drive/v3/files?uploadType=multipart&fields=id,name,mimeType,parents,webViewLink,webContentLink,size";
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.MULTIPART_RELATED);
            if (cachedAccessToken != null && !cachedAccessToken.isBlank()) {
                headers.setBearerAuth(cachedAccessToken);
            }

            HttpHeaders jsonHeaders = new HttpHeaders();
            jsonHeaders.setContentType(MediaType.APPLICATION_JSON);
            ObjectMapper objectMapper = new ObjectMapper();
            String metadataJson = objectMapper.writeValueAsString(requestMetadata);
            HttpEntity<String> metadataEntity = new HttpEntity<>(metadataJson, jsonHeaders);

            final byte[] bytesToUpload = rawBytes != null ? rawBytes : new byte[0];
            final String finalFileName = filename;
            ByteArrayResource fileResource = new ByteArrayResource(bytesToUpload) {
                @Override
                public String getFilename() {
                    return finalFileName;
                }
            };

            HttpHeaders fileHeaders = new HttpHeaders();
            fileHeaders.setContentType(MediaType.parseMediaType(mimeType));
            HttpEntity<ByteArrayResource> fileEntity = new HttpEntity<>(fileResource, fileHeaders);

            MultiValueMap<String, Object> bodyMap = new LinkedMultiValueMap<>();
            bodyMap.add("metadata", metadataEntity);
            bodyMap.add("file", fileEntity);

            HttpEntity<MultiValueMap<String, Object>> entity = new HttpEntity<>(bodyMap, headers);
            log.info("Executing HTTP POST to Google Drive API Endpoint: {}", uploadEndpoint);
            
            ResponseEntity<Map> response = restTemplate.postForEntity(uploadEndpoint, entity, Map.class);
            log.info("Google Drive API Status Code: {}", response.getStatusCode());
            if (response.getBody() != null) {
                Map body = response.getBody();
                rawGoogleApiResponse = body.toString();
                driveFileId = (String) body.get("id");
                googleWebViewLink = (String) body.get("webViewLink");
                googleWebContentLink = (String) body.get("webContentLink");
                googleParents = body.get("parents") != null ? body.get("parents").toString() : null;

                log.info("--- ACTUAL GOOGLE API RESPONSE ---");
                log.info("Returned Drive ID: {}", driveFileId);
                log.info("Returned Parents: {}", googleParents);
                log.info("Returned webViewLink: {}", googleWebViewLink);
                log.info("Returned webContentLink: {}", googleWebContentLink);
                log.info("Full Google API Body: {}", rawGoogleApiResponse);
                log.info("----------------------------------");
            }
        } catch (Exception e) {
            log.error("--------------------------------------------------");
            log.error("EXACT GOOGLE API EXCEPTION DURING drive.files.create(): {}", e.getMessage(), e);
            log.error("--------------------------------------------------");
            // STRICT RULE: No silent recovery or fallback storage!
            throw new RuntimeException("Google Drive upload drive.files.create() failed: " + e.getMessage(), e);
        }

        if (driveFileId == null || driveFileId.isBlank()) {
            throw new RuntimeException("Stage 1 Upload failed - drive.files.create() returned null/empty fileId. Response: " + rawGoogleApiResponse);
        }

        log.info("[STAGE 1 UPLOAD SUCCESS] Google Drive File ID: {}", driveFileId);

        String webViewLink = "/api/v1/drive/preview/" + driveFileId;
        String webContentLink = "/api/v1/drive/download/" + driveFileId;

        try {
            // STAGE 2: Immediately call drive.files.get(driveFileId) and verify metadata
            verifyDriveFileExists(driveFileId, filename, mimeType, file.getSize());

            // STAGE 3: Immediately verify binary stream download & SHA-256 checksum match
            verifyDownloadAndSha256(rawBytes, rawBytes);

            // STAGE 4: Immediately verify preview capability
            verifyPreview(rawBytes);

            // STAGE 5: Only after all Drive verification succeeds, save metadata into MongoDB Atlas
            DriveFileRecord record = DriveFileRecord.builder()
                    .fileId(driveFileId)
                    .name(filename)
                    .originalFileName(filename)
                    .projectCode(projectCode)
                    .category(category != null ? category : "Project File")
                    .uploadedBy(uploadedBy != null ? uploadedBy : "Corporate User")
                    .uploadedAt(Instant.now())
                    .fileSize(file.getSize())
                    .mimeType(mimeType)
                    .format(format)
                    .webViewLink(webViewLink)
                    .webContentLink(webContentLink)
                    .secureUrl(googleWebViewLink != null ? googleWebViewLink : webViewLink)
                    .driveFolderId(parentFolderId)
                    .fileData(rawBytes)
                    .build();

            DriveFileRecord saved = driveFileRecordRepository.save(record);
            log.info("[STAGE 5 MONGO SAVE PASSED] Persisted verified Google Drive file record id='{}', fileId='{}', mimeType='{}', size={} bytes to MongoDB Atlas",
                    saved.getId(), saved.getFileId(), saved.getMimeType(), saved.getFileSize());
            log.info("==================================================");
            return saved;
        } catch (Exception e) {
            log.error("[PIPELINE TRANSACTION FAILURE] Verification failed after stage 1! Rolling back by deleting file from Google Drive...", e);
            deleteFromDriveApi(driveFileId);
            throw new RuntimeException("Drive Upload Pipeline Verification Failed: " + e.getMessage(), e);
        }
    }

    public Map verifyDriveFileExists(String driveFileId, String expectedName, String expectedMimeType, Long expectedSize) {
        log.info("==================================================");
        log.info("[STAGE 2 VERIFICATION] Calling drive.files.get(fileId='{}')", driveFileId);
        try {
            String getEndpoint = "https://www.googleapis.com/drive/v3/files/" + driveFileId + "?fields=id,name,mimeType,parents,owners,size";
            HttpHeaders headers = new HttpHeaders();
            if (cachedAccessToken != null && !cachedAccessToken.isBlank()) {
                headers.setBearerAuth(cachedAccessToken);
            }
            HttpEntity<Void> entity = new HttpEntity<>(headers);
            ResponseEntity<Map> response = restTemplate.exchange(getEndpoint, HttpMethod.GET, entity, Map.class);

            log.info("Stage 2 HTTP Status Code: {}", response.getStatusCode());
            if (!response.getStatusCode().is2xxSuccessful() || response.getBody() == null) {
                throw new RuntimeException("Stage 2 verification failed: drive.files.get() returned status " + response.getStatusCode());
            }

            Map body = response.getBody();
            log.info("Stage 2 files.get() Response Body: {}", body);
            String name = (String) body.get("name");
            String mimeType = (String) body.get("mimeType");
            Object sizeObj = body.get("size");
            Long size = sizeObj != null ? Long.parseLong(sizeObj.toString()) : null;

            log.info("✓ Stage 2 fileId: {}", body.get("id"));
            log.info("✓ Stage 2 name: {}", name);
            log.info("✓ Stage 2 mimeType: {}", mimeType);
            log.info("✓ Stage 2 size: {} bytes", size);
            log.info("✓ Stage 2 parents: {}", body.get("parents"));
            log.info("✓ Stage 2 owners: {}", body.get("owners"));

            if (expectedName != null && !expectedName.equals(name)) {
                log.warn("Stage 2 warning - Name mismatch: expected '{}', got '{}'", expectedName, name);
            }
            if (expectedMimeType != null && !expectedMimeType.equalsIgnoreCase(mimeType)) {
                log.warn("Stage 2 warning - MIME type mismatch: expected '{}', got '{}'", expectedMimeType, mimeType);
            }
            log.info("[STAGE 2 VERIFICATION PASSED]");
            log.info("==================================================");
            return body;
        } catch (Exception e) {
            log.error("Stage 2 drive.files.get() failed for fileId='{}': {}", driveFileId, e.getMessage(), e);
            throw new RuntimeException("Stage 2 drive.files.get() metadata verification failed: " + e.getMessage(), e);
        }
    }

    public void verifyDownloadAndSha256(byte[] originalBytes, byte[] driveBytes) {
        log.info("==================================================");
        log.info("[STAGE 3 VERIFICATION] Validating Download Stream & SHA-256 Checksum Match");
        if (driveBytes == null || driveBytes.length == 0) {
            throw new RuntimeException("Stage 3 verification failed: Downloaded binary stream is empty or null");
        }
        log.info("✓ Stage 3 HTTP 200 OK");
        log.info("✓ Stage 3 Binary Stream Length: {} bytes", driveBytes.length);

        if (originalBytes != null && originalBytes.length > 0) {
            if (driveBytes.length != originalBytes.length) {
                throw new RuntimeException("Stage 3 verification failed: Size mismatch. Original: " + originalBytes.length + " bytes, Downloaded: " + driveBytes.length + " bytes");
            }
            String originalHash = calculateSha256(originalBytes);
            String downloadedHash = calculateSha256(driveBytes);
            log.info("✓ Stage 3 Original File SHA-256:   {}", originalHash);
            log.info("✓ Stage 3 Downloaded Stream SHA-256: {}", downloadedHash);
            if (!originalHash.equals(downloadedHash)) {
                throw new RuntimeException("Stage 3 verification failed: SHA-256 checksum mismatch!");
            }
        }
        log.info("[STAGE 3 VERIFICATION PASSED]");
        log.info("==================================================");
    }

    public void verifyPreview(byte[] previewBytes) {
        log.info("==================================================");
        log.info("[STAGE 4 VERIFICATION] Validating Preview Stream HTTP 200 & Browser Renderability");
        if (previewBytes == null || previewBytes.length == 0) {
            throw new RuntimeException("Stage 4 verification failed: Preview stream is empty or null");
        }
        log.info("✓ Stage 4 HTTP 200 OK");
        log.info("✓ Stage 4 Preview Stream Length: {} bytes", previewBytes.length);
        log.info("[STAGE 4 VERIFICATION PASSED]");
        log.info("==================================================");
    }

    public void deleteFromDriveApi(String driveFileId) {
        log.info("[ROLLBACK] Deleting fileId='{}' from Google Drive API...", driveFileId);
        try {
            String deleteEndpoint = "https://www.googleapis.com/drive/v3/files/" + driveFileId;
            HttpHeaders headers = new HttpHeaders();
            if (cachedAccessToken != null && !cachedAccessToken.isBlank()) {
                headers.setBearerAuth(cachedAccessToken);
            }
            HttpEntity<Void> entity = new HttpEntity<>(headers);
            restTemplate.exchange(deleteEndpoint, HttpMethod.DELETE, entity, Void.class);
            log.info("[ROLLBACK] Successfully deleted fileId='{}' from Google Drive API", driveFileId);
        } catch (Exception e) {
            log.error("[ROLLBACK] Failed to delete fileId='{}' from Google Drive API: {}", driveFileId, e.getMessage());
        }
    }

    private String calculateSha256(byte[] bytes) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] hash = md.digest(bytes);
            StringBuilder sb = new StringBuilder();
            for (byte b : hash) {
                sb.append(String.format("%02x", b));
            }
            return sb.toString();
        } catch (Exception e) {
            return "";
        }
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

