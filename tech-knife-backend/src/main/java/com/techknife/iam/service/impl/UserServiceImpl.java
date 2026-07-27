package com.techknife.iam.service.impl;

import com.techknife.audit.dto.AuditLogRequest;
import com.techknife.audit.entity.AuditAction;
import com.techknife.audit.entity.AuditModule;
import com.techknife.audit.service.AuditService;
import com.techknife.backend.exception.ResourceNotFoundException;
import com.techknife.iam.dto.request.UpdateProfileRequest;
import com.techknife.iam.dto.request.UploadProfileImageRequest;
import com.techknife.iam.dto.response.UserProfileResponse;
import com.techknife.iam.entity.User;
import com.techknife.iam.enums.AccountStatus;
import com.techknife.iam.repository.UserRepository;
import com.techknife.iam.service.UserService;
import com.techknife.storage.FileStorageService;
import com.techknife.storage.FileUploadResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.time.Instant;

/**
 * Implementation of UserService providing user profile management, avatar uploads, and administrative state controls.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final FileStorageService fileStorageService;
    private final AuditService auditService;

    @Override
    @Transactional(readOnly = true)
    public UserProfileResponse getUserById(String userId) {
        User user = userRepository.findByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "userId", userId));
        return mapToUserProfileResponse(user);
    }

    @Override
    @Transactional(readOnly = true)
    public UserProfileResponse getUserByEmail(String email) {
        User user = userRepository.findByOfficialEmail(email)
                .orElseGet(() -> userRepository.findByPersonalEmail(email)
                        .orElseThrow(() -> new ResourceNotFoundException("User", "email", email)));
        return mapToUserProfileResponse(user);
    }

    @Override
    @Transactional
    public UserProfileResponse updateProfile(String userId, UpdateProfileRequest request) {
        log.info("Updating profile for userId '{}'", userId);
        User user = userRepository.findByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "userId", userId));

        if (StringUtils.hasText(request.getFirstName())) {
            user.setFirstName(request.getFirstName().trim());
        }

        if (StringUtils.hasText(request.getLastName())) {
            user.setLastName(request.getLastName().trim());
        }

        if (StringUtils.hasText(request.getFirstName()) || StringUtils.hasText(request.getLastName())) {
            String fName = user.getFirstName() != null ? user.getFirstName() : "";
            String lName = user.getLastName() != null ? user.getLastName() : "";
            user.setFullName((fName + " " + lName).trim());
        }

        if (StringUtils.hasText(request.getPersonalEmail())) {
            user.setPersonalEmail(request.getPersonalEmail().trim());
        }

        if (StringUtils.hasText(request.getMobile())) {
            user.setMobile(request.getMobile().trim());
        }

        if (StringUtils.hasText(request.getDepartment())) {
            user.setDepartment(request.getDepartment().trim());
        }

        if (StringUtils.hasText(request.getDesignation())) {
            user.setDesignation(request.getDesignation().trim());
        }

        if (StringUtils.hasText(request.getBio())) {
            user.setBio(request.getBio().trim());
        }

        user.setUpdatedAt(Instant.now());
        User updatedUser = userRepository.save(user);

        auditService.logAsync(AuditLogRequest.builder()
                .userId(userId)
                .userName(updatedUser.getFullName())
                .userEmail(updatedUser.getOfficialEmail())
                .action(AuditAction.UPDATE)
                .module(AuditModule.AUTHENTICATION)
                .entityType("USER_PROFILE")
                .entityId(userId)
                .description("Updated user profile attributes")
                .status("SUCCESS")
                .timestamp(Instant.now())
                .build());

        return mapToUserProfileResponse(updatedUser);
    }

    @Override
    @Transactional
    public UserProfileResponse uploadProfileImage(String userId, UploadProfileImageRequest request) {
        log.info("Updating profile picture URL for userId '{}'", userId);
        User user = userRepository.findByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "userId", userId));

        user.setProfilePictureUrl(request.getProfileImageUrl());
        user.setUpdatedAt(Instant.now());
        User updatedUser = userRepository.save(user);

        auditService.logAsync(AuditLogRequest.builder()
                .userId(userId)
                .action(AuditAction.UPLOAD)
                .module(AuditModule.AUTHENTICATION)
                .entityType("PROFILE_IMAGE")
                .entityId(userId)
                .description("Updated profile image avatar URL")
                .status("SUCCESS")
                .timestamp(Instant.now())
                .build());

        return mapToUserProfileResponse(updatedUser);
    }

    @Override
    @Transactional
    public UserProfileResponse uploadProfileImage(String userId, MultipartFile file) {
        log.info("Uploading profile picture asset for userId '{}'", userId);
        User user = userRepository.findByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "userId", userId));

        FileUploadResponse uploadResponse = fileStorageService.uploadImage(file, "profiles/" + userId);
        user.setProfilePictureUrl(uploadResponse.getSecureUrl());
        user.setUpdatedAt(Instant.now());
        User updatedUser = userRepository.save(user);

        auditService.logAsync(AuditLogRequest.builder()
                .userId(userId)
                .action(AuditAction.UPLOAD)
                .module(AuditModule.AUTHENTICATION)
                .entityType("PROFILE_IMAGE")
                .entityId(userId)
                .newValue(uploadResponse.getSecureUrl())
                .description("Uploaded new profile picture to Cloudinary")
                .status("SUCCESS")
                .timestamp(Instant.now())
                .build());

        return mapToUserProfileResponse(updatedUser);
    }

    @Override
    @Transactional
    public UserProfileResponse activateUser(String userId) {
        log.info("Activating user account for userId '{}'", userId);
        User user = userRepository.findByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "userId", userId));

        user.setAccountStatus(AccountStatus.ACTIVE);
        user.setAccountLocked(false);
        user.setFailedLoginAttempts(0);
        user.setLockExpiration(null);
        user.setUpdatedAt(Instant.now());
        User updatedUser = userRepository.save(user);

        auditService.logAsync(AuditLogRequest.builder()
                .userId(userId)
                .action(AuditAction.UPDATE)
                .module(AuditModule.AUTHENTICATION)
                .entityType("USER")
                .entityId(userId)
                .description("Activated user account")
                .status("SUCCESS")
                .timestamp(Instant.now())
                .build());

        return mapToUserProfileResponse(updatedUser);
    }

    @Override
    @Transactional
    public UserProfileResponse deactivateUser(String userId) {
        log.info("Deactivating user account for userId '{}'", userId);
        User user = userRepository.findByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "userId", userId));

        user.setAccountStatus(AccountStatus.INACTIVE);
        user.setUpdatedAt(Instant.now());
        User updatedUser = userRepository.save(user);

        auditService.logAsync(AuditLogRequest.builder()
                .userId(userId)
                .action(AuditAction.UPDATE)
                .module(AuditModule.AUTHENTICATION)
                .entityType("USER")
                .entityId(userId)
                .description("Deactivated user account")
                .status("SUCCESS")
                .timestamp(Instant.now())
                .build());

        return mapToUserProfileResponse(updatedUser);
    }

    @Override
    @Transactional
    public UserProfileResponse lockUser(String userId, String reason) {
        log.info("Locking user account for userId '{}', reason: '{}'", userId, reason);
        User user = userRepository.findByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "userId", userId));

        user.setAccountLocked(true);
        user.setAccountStatus(AccountStatus.LOCKED);
        user.setUpdatedAt(Instant.now());
        User updatedUser = userRepository.save(user);

        auditService.logAsync(AuditLogRequest.builder()
                .userId(userId)
                .action(AuditAction.UPDATE)
                .module(AuditModule.AUTHENTICATION)
                .entityType("USER")
                .entityId(userId)
                .description("Manually locked account: " + reason)
                .status("SUCCESS")
                .timestamp(Instant.now())
                .build());

        return mapToUserProfileResponse(updatedUser);
    }

    @Override
    @Transactional
    public UserProfileResponse unlockUser(String userId) {
        log.info("Unlocking user account for userId '{}'", userId);
        User user = userRepository.findByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "userId", userId));

        user.setAccountLocked(false);
        user.setFailedLoginAttempts(0);
        user.setLockExpiration(null);
        user.setAccountStatus(AccountStatus.ACTIVE);
        user.setUpdatedAt(Instant.now());
        User updatedUser = userRepository.save(user);

        auditService.logAsync(AuditLogRequest.builder()
                .userId(userId)
                .action(AuditAction.UPDATE)
                .module(AuditModule.AUTHENTICATION)
                .entityType("USER")
                .entityId(userId)
                .description("Unlocked user account")
                .status("SUCCESS")
                .timestamp(Instant.now())
                .build());

        return mapToUserProfileResponse(updatedUser);
    }

    private UserProfileResponse mapToUserProfileResponse(User user) {
        return UserProfileResponse.builder()
                .userId(user.getUserId())
                .officialEmail(user.getOfficialEmail())
                .personalEmail(user.getPersonalEmail())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .fullName(user.getFullName())
                .mobile(user.getMobile())
                .profilePictureUrl(user.getProfilePictureUrl())
                .department(user.getDepartment())
                .designation(user.getDesignation())
                .employeeId(user.getEmployeeId())
                .customerId(user.getCustomerId())
                .accountStatus(user.getAccountStatus())
                .accountType(user.getAccountType())
                .emailVerified(user.isEmailVerified())
                .mobileVerified(user.isMobileVerified())
                .roles(user.getRoles())
                .permissions(user.getPermissions())
                .createdAt(user.getCreatedAt())
                .updatedAt(user.getUpdatedAt())
                .lastLogin(user.getLastLogin())
                .build();
    }
}
