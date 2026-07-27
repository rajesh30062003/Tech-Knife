package com.techknife.iam.repository;

import com.techknife.iam.entity.Role;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Spring Data MongoDB repository for system Role definition and authority management.
 */
@Repository
public interface RoleRepository extends MongoRepository<Role, String> {

    Optional<Role> findByRoleCode(String roleCode);

    Optional<Role> findByRoleName(String roleName);

    List<Role> findByActiveTrue();

    @Query("{ 'active': true }")
    List<Role> findActiveRoles();
}
