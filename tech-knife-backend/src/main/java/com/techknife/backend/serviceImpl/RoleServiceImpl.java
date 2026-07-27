package com.techknife.backend.serviceImpl;

import com.techknife.backend.constant.Permission;
import com.techknife.backend.constant.Role;
import com.techknife.backend.dto.RoleRequest;
import com.techknife.backend.dto.RoleResponse;
import com.techknife.backend.entity.RoleEntity;
import com.techknife.backend.exception.BadRequestException;
import com.techknife.backend.exception.ResourceNotFoundException;
import com.techknife.backend.repository.RoleRepository;
import com.techknife.backend.service.RoleService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RoleServiceImpl implements RoleService {

    private final RoleRepository roleRepository;

    @Override
    public List<RoleResponse> getAllRoles() {
        List<RoleEntity> roles = roleRepository.findAll();
        if (roles.isEmpty()) {
            return Arrays.stream(Role.values())
                    .map(r -> RoleResponse.builder()
                            .role(r)
                            .name(r.name())
                            .description("System default role: " + r.name())
                            .isSystemRole(true)
                            .permissions(getDefaultPermissionsForRole(r))
                            .build())
                    .collect(Collectors.toList());
        }
        return roles.stream().map(this::mapToResponse).collect(Collectors.toList());
    }

    @Override
    public RoleResponse getRoleByEnum(Role role) {
        return roleRepository.findByRole(role)
                .map(this::mapToResponse)
                .orElseGet(() -> RoleResponse.builder()
                        .role(role)
                        .name(role.name())
                        .description("System default role: " + role.name())
                        .isSystemRole(true)
                        .permissions(getDefaultPermissionsForRole(role))
                        .build());
    }

    @Override
    public RoleResponse getRoleById(String id) {
        RoleEntity entity = roleRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Role", "id", id));
        return mapToResponse(entity);
    }

    @Override
    public RoleResponse createRole(RoleRequest.CreateRoleRequest request) {
        if (roleRepository.existsByRole(request.getRole())) {
            throw new BadRequestException("Role already exists: " + request.getRole());
        }

        RoleEntity roleEntity = RoleEntity.builder()
                .role(request.getRole())
                .name(request.getName())
                .description(request.getDescription())
                .isSystemRole(false)
                .permissions(request.getPermissions() != null ? request.getPermissions() : new HashSet<>())
                .build();

        RoleEntity saved = roleRepository.save(roleEntity);
        return mapToResponse(saved);
    }

    @Override
    public RoleResponse updateRolePermissions(String id, Set<Permission> permissions) {
        RoleEntity entity = roleRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Role", "id", id));

        entity.setPermissions(permissions);
        RoleEntity updated = roleRepository.save(entity);
        return mapToResponse(updated);
    }

    @Override
    public void deleteRole(String id) {
        RoleEntity entity = roleRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Role", "id", id));
        if (entity.isSystemRole()) {
            throw new BadRequestException("Cannot delete immutable system role: " + entity.getRole());
        }
        roleRepository.delete(entity);
    }

    private RoleResponse mapToResponse(RoleEntity entity) {
        return RoleResponse.builder()
                .id(entity.getId())
                .role(entity.getRole())
                .name(entity.getName())
                .description(entity.getDescription())
                .isSystemRole(entity.isSystemRole())
                .permissions(entity.getPermissions())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .build();
    }

    private Set<Permission> getDefaultPermissionsForRole(Role role) {
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
