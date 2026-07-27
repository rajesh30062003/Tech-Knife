package com.techknife.backend.repository;

import com.techknife.backend.constant.Permission;
import com.techknife.backend.entity.PermissionEntity;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PermissionRepository extends MongoRepository<PermissionEntity, String> {
    Optional<PermissionEntity> findByPermission(Permission permission);
    Optional<PermissionEntity> findByCode(String code);
}
