package com.techknife.backend.service;

import com.techknife.backend.constant.Role;
import com.techknife.backend.dto.*;

import java.util.Set;

public interface UserService {
    PagedResponse<UserResponse> getPaginatedUsers(int page, int size, String search, String department);
    UserResponse getUserById(String id);
    UserResponse getUserByEmail(String email);
    UserResponse createUser(CreateUserRequest request);
    UserResponse updateUser(String id, UpdateUserRequest request);
    void deleteUser(String id);
    void changePassword(String userId, ChangePasswordRequest request);
    UserResponse toggleUserLock(String id, boolean locked);
    UserResponse assignRolesToUser(String id, Set<Role> roles);
}
