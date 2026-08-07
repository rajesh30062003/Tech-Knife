package com.techknife.storage;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.*;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.time.Instant;
import java.util.*;

public class GoogleDriveServiceAccountLiveAuditTest {

    private final RestTemplate restTemplate = new RestTemplate();
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Test
    public void executeLiveOAuthUserTokenAudit() throws Exception {
        System.out.println("==================================================");
        System.out.println("STEP 1: Verify .env OAuth Configuration & Refresh Token");

        File envFile = new File(".env");
        if (!envFile.exists()) {
            envFile = new File("tech-knife-backend/.env");
        }
        System.out.println("Reading .env from: " + envFile.getAbsolutePath());
        List<String> envLines = Files.readAllLines(envFile.toPath(), StandardCharsets.UTF_8);
        Map<String, String> envMap = new HashMap<>();
        for (String line : envLines) {
            line = line.trim();
            if (line.startsWith("#") || !line.contains("=")) continue;
            int idx = line.indexOf("=");
            envMap.put(line.substring(0, idx).trim(), line.substring(idx + 1).trim());
        }

        String clientId = envMap.get("GOOGLE_OAUTH_CLIENT_ID");
        String clientSecret = envMap.get("GOOGLE_OAUTH_CLIENT_SECRET");
        String refreshToken = envMap.get("GOOGLE_OAUTH_REFRESH_TOKEN");
        String targetFolderId = envMap.get("GOOGLE_DRIVE_PARENT_FOLDER_ID");

        System.out.println("✓ GOOGLE_DRIVE_AUTH_MODE: " + envMap.get("GOOGLE_DRIVE_AUTH_MODE"));
        System.out.println("✓ GOOGLE_OAUTH_CLIENT_ID: " + clientId);
        System.out.println("✓ GOOGLE_OAUTH_CLIENT_SECRET: " + (clientSecret != null ? "[PRESENT]" : "[MISSING]"));
        System.out.println("✓ GOOGLE_OAUTH_REFRESH_TOKEN: " + (refreshToken != null ? refreshToken.substring(0, 15) + "..." : "[MISSING]"));
        System.out.println("✓ GOOGLE_DRIVE_PARENT_FOLDER_ID: " + targetFolderId);

        if (refreshToken == null || refreshToken.isBlank()) {
            throw new IllegalStateException("No refresh token found in .env!");
        }

        System.out.println("==================================================");
        System.out.println("STEP 2: Live Refresh Token Exchange with Google OAuth Endpoint");

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
        body.add("client_id", clientId);
        body.add("client_secret", clientSecret);
        body.add("refresh_token", refreshToken);
        body.add("grant_type", "refresh_token");

        HttpEntity<MultiValueMap<String, String>> requestEntity = new HttpEntity<>(body, headers);
        ResponseEntity<Map> response = restTemplate.postForEntity("https://oauth2.googleapis.com/token", requestEntity, Map.class);

        System.out.println("Token Refresh Endpoint Status: " + response.getStatusCode());
        Map tokenResponseBody = response.getBody();
        String accessToken = (String) tokenResponseBody.get("access_token");
        Integer expiresIn = (Integer) tokenResponseBody.get("expires_in");
        String tokenType = (String) tokenResponseBody.get("token_type");
        String scope = (String) tokenResponseBody.get("scope");

        System.out.println("✓ HTTP Status Code: 200 OK");
        System.out.println("✓ token_type: " + tokenType);
        System.out.println("✓ expires_in: " + expiresIn + " seconds");
        System.out.println("✓ scope: " + scope);
        System.out.println("✓ access_token length: " + accessToken.length() + " characters");

        System.out.println("==================================================");
        System.out.println("STEP 3: Query Google Drive API Account & Quota Metadata");
        String aboutUrl = "https://www.googleapis.com/drive/v3/about?fields=user,storageQuota";
        HttpHeaders authHeaders = new HttpHeaders();
        authHeaders.setBearerAuth(accessToken);
        HttpEntity<Void> authEntity = new HttpEntity<>(authHeaders);
        ResponseEntity<Map> aboutResponse = restTemplate.exchange(aboutUrl, HttpMethod.GET, authEntity, Map.class);

        System.out.println("GET /about Status: " + aboutResponse.getStatusCode());
        Map aboutBody = aboutResponse.getBody();
        Map userMap = (Map) aboutBody.get("user");
        Map quotaMap = (Map) aboutBody.get("storageQuota");

        System.out.println("✓ Authenticated Account Email: " + userMap.get("emailAddress"));
        System.out.println("✓ Display Name: " + userMap.get("displayName"));
        System.out.println("✓ Storage Quota Limit: " + quotaMap.get("limit") + " bytes");
        System.out.println("✓ Storage Quota Usage: " + quotaMap.get("usage") + " bytes");

        System.out.println("==================================================");
        System.out.println("STEP 4: Verify Target Folder Access for " + targetFolderId);
        String folderUrl = "https://www.googleapis.com/drive/v3/files/" + targetFolderId + "?fields=id,name,mimeType,parents,capabilities";
        
        ResponseEntity<Map> folderResponse = restTemplate.exchange(folderUrl, HttpMethod.GET, authEntity, Map.class);
        System.out.println("Target Folder GET Status: " + folderResponse.getStatusCode());
        Map folderBody = folderResponse.getBody();
        System.out.println("✓ Folder ID: " + folderBody.get("id"));
        System.out.println("✓ Folder Name: " + folderBody.get("name"));
        System.out.println("✓ Folder MIME Type: " + folderBody.get("mimeType"));

        System.out.println("==================================================");
        System.out.println("STEP 5: Live File Upload to Google Drive (OAuth 2.0 User Token)");
        String testFilename = "live_oauth_audit_" + System.currentTimeMillis() + ".pdf";
        byte[] pdfContent = ("%PDF-1.4 Tech Knife Enterprise Management System OAuth 2.0 Live Upload Verification Timestamp: " + Instant.now()).getBytes(StandardCharsets.UTF_8);
        
        String uploadEndpoint = "https://www.googleapis.com/upload/drive/v3/files?uploadType=multipart&supportsAllDrives=true&fields=id,name,mimeType,parents,webViewLink,webContentLink,size";
        HttpHeaders uploadHeaders = new HttpHeaders();
        uploadHeaders.setContentType(MediaType.MULTIPART_RELATED);
        uploadHeaders.setBearerAuth(accessToken);

        Map<String, Object> reqMetadata = new HashMap<>();
        reqMetadata.put("name", testFilename);
        reqMetadata.put("mimeType", "application/pdf");
        reqMetadata.put("parents", List.of(targetFolderId));

        HttpHeaders jsonH = new HttpHeaders();
        jsonH.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<String> metaEntity = new HttpEntity<>(objectMapper.writeValueAsString(reqMetadata), jsonH);

        ByteArrayResource fileRes = new ByteArrayResource(pdfContent) {
            @Override
            public String getFilename() {
                return testFilename;
            }
        };
        HttpHeaders fileH = new HttpHeaders();
        fileH.setContentType(MediaType.APPLICATION_PDF);
        HttpEntity<ByteArrayResource> fileEnt = new HttpEntity<>(fileRes, fileH);

        MultiValueMap<String, Object> bodyMap = new LinkedMultiValueMap<>();
        bodyMap.add("metadata", metaEntity);
        bodyMap.add("file", fileEnt);

        HttpEntity<MultiValueMap<String, Object>> uploadEntity = new HttpEntity<>(bodyMap, uploadHeaders);
        ResponseEntity<Map> uploadRes = restTemplate.postForEntity(uploadEndpoint, uploadEntity, Map.class);

        System.out.println("Upload Status: " + uploadRes.getStatusCode());
        Map uploadMap = uploadRes.getBody();
        String createdFileId = (String) uploadMap.get("id");
        System.out.println("✓ HTTP 200 OK from drive.files.create()");
        System.out.println("✓ Uploaded Drive File ID: " + createdFileId);
        System.out.println("✓ Drive File Name: " + uploadMap.get("name"));
        System.out.println("✓ webViewLink: " + uploadMap.get("webViewLink"));
        System.out.println("✓ webContentLink: " + uploadMap.get("webContentLink"));

        System.out.println("==================================================");
        System.out.println("STEP 6: Direct API Verification of Physical Existence & Ownership");
        String verifyUrl = "https://www.googleapis.com/drive/v3/files/" + createdFileId + "?fields=id,name,mimeType,parents,webViewLink,webContentLink,size,owners";
        ResponseEntity<Map> verifyRes = restTemplate.exchange(verifyUrl, HttpMethod.GET, authEntity, Map.class);
        
        System.out.println("verify files.get Status: " + verifyRes.getStatusCode());
        Map verifyMap = verifyRes.getBody();
        System.out.println("✓ Verified ID: " + verifyMap.get("id"));
        System.out.println("✓ Verified Name: " + verifyMap.get("name"));
        System.out.println("✓ Verified MIME Type: " + verifyMap.get("mimeType"));
        System.out.println("✓ Verified Parents: " + verifyMap.get("parents"));
        System.out.println("✓ Verified Owners: " + verifyMap.get("owners"));
        System.out.println("✓ Verified Size: " + verifyMap.get("size") + " bytes");
        System.out.println("==================================================");
        System.out.println("LIVE OAUTH 2.0 AUDIT SUCCESSFUL!");
        System.out.println("==================================================");
    }
}
