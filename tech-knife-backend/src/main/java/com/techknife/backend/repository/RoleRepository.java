package com.techknife.backend.repository;

import com.techknife.backend.constant.Role;
import com.techknife.backend.entity.RoleEntity;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository("backendRoleRepository")
public interface RoleRepository extends MongoRepository<RoleEntity, String> {

    Optional<RoleEntity> findByRole(Role role);
    boolean existsByRole(Role role);
}
