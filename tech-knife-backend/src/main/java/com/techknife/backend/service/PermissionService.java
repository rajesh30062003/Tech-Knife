package com.techknife.backend.service;

import com.techknife.backend.dto.PermissionResponse;

import java.util.List;

public interface PermissionService {
    List<PermissionResponse> getAllPermissions();
    PermissionResponse getPermissionByCode(String code);
}
