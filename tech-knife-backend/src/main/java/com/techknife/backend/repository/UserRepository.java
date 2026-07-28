package com.techknife.backend.repository;

import com.techknife.backend.constant.Role;
import com.techknife.backend.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository("backendUserRepository")
public interface UserRepository extends MongoRepository<User, String> {


    Optional<User> findByEmail(String email);

    Boolean existsByEmail(String email);

    List<User> findByDepartment(String department);

    List<User> findByRolesContaining(Role role);

    @Query("{ '$or': [ " +
           "{ 'firstName': { '$regex': ?0, '$options': 'i' } }, " +
           "{ 'lastName': { '$regex': ?0, '$options': 'i' } }, " +
           "{ 'email': { '$regex': ?0, '$options': 'i' } }, " +
           "{ 'designation': { '$regex': ?0, '$options': 'i' } } " +
           "] }")
    Page<User> searchUsers(String query, Pageable pageable);

    Page<User> findByDepartment(String department, Pageable pageable);
}
