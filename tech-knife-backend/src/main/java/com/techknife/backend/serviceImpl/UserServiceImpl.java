package com.techknife.backend.serviceImpl;

import com.techknife.backend.constant.Permission;
import com.techknife.backend.constant.Role;
import com.techknife.backend.dto.*;
import com.techknife.backend.entity.User;
import com.techknife.backend.exception.BadRequestException;
import com.techknife.backend.exception.ResourceNotFoundException;
import com.techknife.backend.repository.UserRepository;
import com.techknife.backend.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public PagedResponse<UserResponse> getPaginatedUsers(int page, int size, String search, String department) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        Page<User> usersPage;

        if (search != null && !search.trim().isEmpty()) {
            usersPage = userRepository.searchUsers(search.trim(), pageable);
        } else if (department != null && !department.trim().isEmpty()) {
            usersPage = userRepository.findByDepartment(department.trim(), pageable);
        } else {
            usersPage = userRepository.findAll(pageable);
        }

        List<UserResponse> content = usersPage.getContent().stream()
                .map(this::mapToUserResponse)
                .collect(Collectors.toList());

        return PagedResponse.<UserResponse>builder()
                .content(content)
                .page(usersPage.getNumber())
                .size(usersPage.getSize())
                .totalElements(usersPage.getTotalElements())
                .totalPages(usersPage.getTotalPages())
                .last(usersPage.isLast())
                .build();
    }

    @Override
    public UserResponse getUserById(String id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", id));
        return mapToUserResponse(user);
    }

    @Override
    public UserResponse getUserByEmail(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User", "email", email));
        return mapToUserResponse(user);
    }

    @Override
    public UserResponse createUser(CreateUserRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new BadRequestException("Email address is already registered in Tech Knife: " + request.getEmail());
        }

        User user = User.builder()
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .designation(request.getDesignation() != null ? request.getDesignation() : "Enterprise Staff")
                .department(request.getDepartment() != null ? request.getDepartment() : "General")
                .phoneNumber(request.getPhoneNumber())
                .avatarUrl(request.getAvatarUrl() != null ? request.getAvatarUrl() : "https://images.unsplash.com/photo-1534528741775-53994a69daeb?auto=format&fit=crop&q=80&w=250")
                .enabled(true)
                .accountNonLocked(true)
                .roles(request.getRoles())
                .build();

        User savedUser = userRepository.save(user);
        return mapToUserResponse(savedUser);
    }

    @Override
    public UserResponse updateUser(String id, UpdateUserRequest request) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", id));

        if (request.getFirstName() != null) user.setFirstName(request.getFirstName());
        if (request.getLastName() != null) user.setLastName(request.getLastName());
        if (request.getDesignation() != null) user.setDesignation(request.getDesignation());
        if (request.getDepartment() != null) user.setDepartment(request.getDepartment());
        if (request.getPhoneNumber() != null) user.setPhoneNumber(request.getPhoneNumber());
        if (request.getAvatarUrl() != null) user.setAvatarUrl(request.getAvatarUrl());
        if (request.getRoles() != null && !request.getRoles().isEmpty()) user.setRoles(request.getRoles());
        if (request.getEnabled() != null) user.setEnabled(request.getEnabled());
        if (request.getAccountNonLocked() != null) user.setAccountNonLocked(request.getAccountNonLocked());

        User updated = userRepository.save(user);
        return mapToUserResponse(updated);
    }

    @Override
    public void deleteUser(String id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", id));
        userRepository.delete(user);
    }

    @Override
    public void changePassword(String userId, ChangePasswordRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", userId));

        if (!passwordEncoder.matches(request.getCurrentPassword(), user.getPassword())) {
            throw new BadRequestException("Current password provided is incorrect");
        }

        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        userRepository.save(user);
    }

    @Override
    public UserResponse toggleUserLock(String id, boolean locked) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", id));

        user.setAccountNonLocked(!locked);
        User updated = userRepository.save(user);
        return mapToUserResponse(updated);
    }

    @Override
    public UserResponse assignRolesToUser(String id, Set<Role> roles) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", id));

        user.setRoles(roles);
        User updated = userRepository.save(user);
        return mapToUserResponse(updated);
    }

    private UserResponse mapToUserResponse(User user) {
        Set<Permission> permissions = new HashSet<>();
        if (user.getRoles() != null) {
            for (Role role : user.getRoles()) {
                permissions.addAll(getPermissionsForRole(role));
            }
        }

        return UserResponse.builder()
                .id(user.getId())
                .email(user.getEmail())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .designation(user.getDesignation())
                .department(user.getDepartment())
                .phoneNumber(user.getPhoneNumber())
                .avatarUrl(user.getAvatarUrl())
                .enabled(user.isEnabled())
                .accountNonLocked(user.isAccountNonLocked())
                .roles(user.getRoles())
                .permissions(permissions)
                .lastLoginAt(user.getLastLoginAt())
                .createdAt(user.getCreatedAt())
                .updatedAt(user.getUpdatedAt())
                .build();
    }

    private Set<Permission> getPermissionsForRole(Role role) {
        Set<Permission> permissions = new HashSet<>();
        switch (role) {
            case ROLE_SUPER_ADMIN, ROLE_ADMIN:
                permissions.addAll(Arrays.asList(Permission.values()));
                break;
            case ROLE_MANAGER:
                permissions.addAll(Arrays.asList(
                        Permission.USER_READ, Permission.PROJECT_READ, Permission.PROJECT_WRITE,
                        Permission.CRM_READ, Permission.CRM_WRITE, Permission.RECRUITMENT_READ
                ));
                break;
            case ROLE_EMPLOYEE, ROLE_INTERN:
                permissions.addAll(Arrays.asList(
                        Permission.USER_READ, Permission.PROJECT_READ, Permission.PAYROLL_READ
                ));
                break;
            case ROLE_CUSTOMER:
                permissions.add(Permission.PROJECT_READ);
                break;
            default:
                permissions.add(Permission.USER_READ);
        }
        return permissions;
    }
}
