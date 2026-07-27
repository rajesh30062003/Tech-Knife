package com.techknife.iam.service;

import com.techknife.iam.dto.request.UpdateProfileRequest;
import com.techknife.iam.dto.request.UploadProfileImageRequest;
import com.techknife.iam.dto.response.UserProfileResponse;
import org.springframework.web.multipart.MultipartFile;

/**
 * Service contract for user management, profile updates, account activation, and lock management.
 */
public interface UserService {

    UserProfileResponse getUserById(String userId);

    UserProfileResponse getUserByEmail(String email);

    UserProfileResponse updateProfile(String userId, UpdateProfileRequest request);

    UserProfileResponse uploadProfileImage(String userId, UploadProfileImageRequest request);

    UserProfileResponse uploadProfileImage(String userId, MultipartFile file);

    UserProfileResponse activateUser(String userId);

    UserProfileResponse deactivateUser(String userId);

    UserProfileResponse lockUser(String userId, String reason);

    UserProfileResponse unlockUser(String userId);
}
