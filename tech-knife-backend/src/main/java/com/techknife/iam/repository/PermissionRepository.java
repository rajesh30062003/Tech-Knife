package com.techknife.iam.repository;

import com.techknife.iam.entity.Permission;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Spring Data MongoDB repository for granular resource permissions and action rights.
 */
@Repository
public interface PermissionRepository extends MongoRepository<Permission, String> {

    List<Permission> findByResource(String resource);

    List<Permission> findByCategory(String category);

    List<Permission> findByActiveTrue();

    @Query("{ 'active': true }")
    List<Permission> findActivePermissions();
}
