package com.techknife.iam.controller;

import com.techknife.backend.exception.BadRequestException;
import com.techknife.backend.response.ApiResponse;
import com.techknife.iam.dto.request.UpdateProfileRequest;
import com.techknife.iam.dto.request.UploadProfileImageRequest;
import com.techknife.iam.dto.response.UserProfileResponse;
import com.techknife.iam.service.UserService;
import com.techknife.security.UserPrincipal;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

/**
 * REST controller for managing authenticated user profile details, contact updates, and avatar image uploads.
 */
@Slf4j
@RestController
@RequestMapping("/api/v1/profile")
@Validated
@RequiredArgsConstructor
@SecurityRequirement(name = "bearerAuth")
@Tag(name = "Profile API", description = "Endpoints for retrieving, updating, and managing user profiles and avatar pictures")
public class ProfileController {

    private final UserService userService;

    @GetMapping
    @Operation(summary = "Get Profile", description = "Fetch complete profile information for the authenticated user.")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "User profile fetched successfully"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "Unauthorized access"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "User account not found")
    })
    public ResponseEntity<ApiResponse<UserProfileResponse>> getProfile(
            @AuthenticationPrincipal UserPrincipal currentUser) {
        UserProfileResponse response = userService.getUserById(currentUser.getId());
        return ResponseEntity.ok(ApiResponse.success(response, "User profile retrieved successfully"));
    }

    @PutMapping
    @Operation(summary = "Update Profile", description = "Update mutable profile attributes for the authenticated user.")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "User profile updated successfully"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Invalid request payload attributes"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "Unauthorized access")
    })
    public ResponseEntity<ApiResponse<UserProfileResponse>> updateProfile(
            @AuthenticationPrincipal UserPrincipal currentUser,
            @Valid @RequestBody UpdateProfileRequest request) {
        UserProfileResponse response = userService.updateProfile(currentUser.getId(), request);
        return ResponseEntity.ok(ApiResponse.success(response, "User profile updated successfully"));
    }

    @PostMapping("/upload-image")
    @Operation(summary = "Upload Profile Image", description = "Upload a profile picture asset file or link an external avatar URL.")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Profile image updated successfully"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Missing or invalid image file or payload"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "Unauthorized access")
    })
    public ResponseEntity<ApiResponse<UserProfileResponse>> uploadProfileImage(
            @AuthenticationPrincipal UserPrincipal currentUser,
            @RequestParam(value = "file", required = false) MultipartFile file,
            @Valid @RequestBody(required = false) UploadProfileImageRequest request) {
        UserProfileResponse response;
        if (file != null && !file.isEmpty()) {
            response = userService.uploadProfileImage(currentUser.getId(), file);
        } else if (request != null && request.getProfileImageUrl() != null) {
            response = userService.uploadProfileImage(currentUser.getId(), request);
        } else {
            throw new BadRequestException("Either an image file or a valid profile image URL payload must be provided");
        }
        return ResponseEntity.ok(ApiResponse.success(response, "Profile image updated successfully"));
    }

    @DeleteMapping("/image")
    @Operation(summary = "Delete Profile Image", description = "Remove avatar picture for the authenticated user.")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Profile image removed successfully"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "Unauthorized access")
    })
    public ResponseEntity<ApiResponse<UserProfileResponse>> deleteProfileImage(
            @AuthenticationPrincipal UserPrincipal currentUser) {
        UploadProfileImageRequest clearRequest = UploadProfileImageRequest.builder().profileImageUrl(null).build();
        UserProfileResponse response = userService.uploadProfileImage(currentUser.getId(), clearRequest);
        return ResponseEntity.ok(ApiResponse.success(response, "Profile image removed successfully"));
    }
}
