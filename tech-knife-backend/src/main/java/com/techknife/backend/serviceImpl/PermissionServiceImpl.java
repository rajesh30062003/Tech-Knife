package com.techknife.backend.serviceImpl;

import com.techknife.backend.constant.Permission;
import com.techknife.backend.dto.PermissionResponse;
import com.techknife.backend.exception.ResourceNotFoundException;
import com.techknife.backend.repository.PermissionRepository;
import com.techknife.backend.service.PermissionService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PermissionServiceImpl implements PermissionService {

    private final PermissionRepository permissionRepository;

    @Override
    public List<PermissionResponse> getAllPermissions() {
        return Arrays.stream(Permission.values())
                .map(perm -> PermissionResponse.builder()
                        .permission(perm)
                        .code(perm.getCode())
                        .name(perm.name())
                        .category(perm.getCode().split(":")[0].toUpperCase())
                        .description(perm.getDescription())
                        .build())
                .collect(Collectors.toList());
    }

    @Override
    public PermissionResponse getPermissionByCode(String code) {
        return Arrays.stream(Permission.values())
                .filter(perm -> perm.getCode().equalsIgnoreCase(code) || perm.name().equalsIgnoreCase(code))
                .findFirst()
                .map(perm -> PermissionResponse.builder()
                        .permission(perm)
                        .code(perm.getCode())
                        .name(perm.name())
                        .category(perm.getCode().split(":")[0].toUpperCase())
                        .description(perm.getDescription())
                        .build())
                .orElseThrow(() -> new ResourceNotFoundException("Permission", "code", code));
    }
}
